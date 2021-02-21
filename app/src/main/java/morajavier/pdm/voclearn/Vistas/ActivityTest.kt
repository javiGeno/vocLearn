package morajavier.pdm.voclearn.Vistas

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.media.CamcorderProfile.get
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import kotlinx.android.synthetic.main.activity_test.*
import kotlinx.android.synthetic.main.barra_guardar_atras.*
import kotlinx.android.synthetic.main.informacion_test.view.*
import morajavier.pdm.voclearn.BaseDatos.CRUDEntradas
import morajavier.pdm.voclearn.Modelo.AnimaWidget
import morajavier.pdm.voclearn.Modelo.ClassTest
import morajavier.pdm.voclearn.Modelo.Entrada
import morajavier.pdm.voclearn.Modelo.Sonido
import morajavier.pdm.voclearn.R
import org.w3c.dom.Text

class ActivityTest : AppCompatActivity() {

    lateinit var test : ClassTest
    lateinit var audio: MediaPlayer
    lateinit var vistaInformacion: View
    var oportunidad=2
    var handler= Handler()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        test=ClassTest(obtenerListaParaTest(intent.getIntArrayExtra("listaIdsTest")))

        mostrarPalabrasConsola(test);

        mostrarPalabra()

        sigPregunta.setOnClickListener{

            var fallado=comprobarRespuesta(it)

            //SI NO TIENE AUDIO Y NO HA FALLADO O FALLA LA PREGUNTA, SIN TENER MÁS OPORTUNIDADES, SE MUESTRA OTRA PALABRA
            if((fallado && oportunidad==0)||(test.entradaPregunta.audio!!.isEmpty() && !fallado))
            {
                    mostrarPalabra()
            }
            else{
                //SI EL AUDIO TERMINA MOSTRAMOS PALABRA, DESPUÉS DE QUE TERMINE NO ANTES
                if(::audio.isInitialized) {

                    audio?.setOnCompletionListener {

                        sigPregunta.setEnabled(true)
                        mostrarPalabra()

                    }
                }

            }



        }

        btn_atras.setOnClickListener{

            //INFORMAR AL USUARIO
            estadisticas()
            alertafinEstadisticas()

        }
    }

    private fun mostrarPalabrasConsola(test: ClassTest) {

        for(i in test.listaTest)
        {
            println("PALABRAS TEST :"+i.escrituraIngles)
        }
    }

    fun estadisticas()
    {
        println("NÚMERO ACIERTOS "+test.numeroAcertadas)
        println("NÚMERO FALLOS "+test.numeroFallidas)
        var laMasFallidas= test.obtenerLaPalabraMasFallida()

        if(test.listaMasFallos.any {it>=ClassTest.MUCHOSFALLOS })
            println(getString(R.string.muchosFallos))
        else if(test.listaMasFallos.any {it<=ClassTest.POCOSFALLOS })
        {
            println(getString(R.string.pocosFallos))
        }
        for(p in laMasFallidas) {
            println(p)
        }

    }

    fun comprobarRespuesta(botonSig : View):Boolean
    {
        val respuesta=field_respuesta.text.toString()
        val pregunta=test.entradaPregunta.escrituraIngles

        //COMPROBAMOS SI HA ACERTADO LA PALABRA CON LA QUE HA SALIDO ALEATORIAMENTE
        if(respuesta.toLowerCase().trim() == pregunta?.toLowerCase()?.trim())
        {
            cambioEstilo(R.drawable.borde_respuesta_acertada, R.color.dif_center_2)

            if(!(test.entradaPregunta.audio!!.isEmpty()))
                sigPregunta.setEnabled(false)
            else{
                sigPregunta.setEnabled(true)
            }

            println("LISTA ACIERTO"+test.entradaPregunta)

            actualizacionProbPalabra(listOf(test.entradaPregunta))


            test.numeroAcertadas++

            return false
        }
        else
        {
            //EN CASO DE QUE NO HAYA ACERTADO CON LA QUE HA SALIDO ALEATORIAMENTE, BUSCAMOS SI HAY ALGUNA
            //CON ALGUN SIGNIFICADO EN ESPAÑOL IGUAL A LA QUE HA SALIDO ALEATORIAMENTE
            if(test.masDeUnaConElMismoSignificado(test.entradaPregunta.significado))
            {

                var palabrasDistintoSig = test.listaTest.filter {
                    it.escrituraIngles?.toLowerCase()?.trim() == respuesta.toLowerCase()?.trim()
                            && it.significado.toLowerCase()?.trim() == test.entradaPregunta.significado
                }
                println("LISTA ACIERTO" + palabrasDistintoSig)


                //SI HAY MAS PALABRAS, Y HA ACERTADO CON ALMENOS UNA, LA LISTA NO ESTARÁ VACÍA, POR LO TANTO
                //REALIZAMOS LA OPERACIÓN DE ACIERTO CON ESA PALABRA/S EN CONCRETO
                //POR EL CONTRARIO, SI ESTÁ VACÍA FALLAMOS EN TODAS LAS PALABRAS CON ESE SIGNIFICADO EN ESPAÑOL
                if(palabrasDistintoSig.isNotEmpty()) {
                    cambioEstilo(R.drawable.borde_respuesta_acertada, R.color.dif_center_2)
                    actualizacionProbPalabra(palabrasDistintoSig)
                    test.numeroAcertadas++

                    if(!(test.entradaPregunta.audio!!.isEmpty()))
                        sigPregunta.setEnabled(false)
                    else{
                        sigPregunta.setEnabled(true)
                    }

                    return false
                }
                else
                {
                    var palabrasFalladas= test.listaTest.filter {
                        it.significado.toLowerCase()?.trim() == test.entradaPregunta.significado
                    }

                    haFallado(palabrasFalladas)

                    sigPregunta.setEnabled(true)
                    println("BOTON HABILITADo int")

                    oportunidad--
                    test.numeroFallidas++

                    return true
                }


            }
            else
            {
                haFallado(listOf(test.entradaPregunta))

                sigPregunta.setEnabled(true)
                println("BOTON HABILITADo erxt")

                oportunidad--
                test.numeroFallidas++

                return true
            }


        }
    }



    fun haFallado(entradasRespuesta:List<Entrada>)
    {
        //HACEMOS TEMBLAR EL CAMPO CUANDO FALLA
        AnimaWidget.rotacion(field_respuesta, 0.0f, 5.0f, 100)
        AnimaWidget.rotacion(field_respuesta, 1.0f, -5.0f, 100)
        AnimaWidget.rotacion(field_respuesta, -5.0f, 0.0f, 100)
        cambioEstilo(R.drawable.borde_respuesta_fallida, R.color.dif_end_1)

        for(entradaRespuesta in entradasRespuesta) {
            CRUDEntradas.actualizacionDificultad(entradaRespuesta, ClassTest.PROBABILIDADROJA)
            test.añadirFalloAPalabra(entradaRespuesta)

        }


    }


    fun actualizacionProbPalabra(entradasRespuesta:List<Entrada>)
    {
        var contVeces=0

        for (entradaRespuesta in entradasRespuesta) {
            if (entradaRespuesta.probAcierto != ClassTest.PROBABILIDADVERDE) {
                var probabilidadActu = entradaRespuesta.probAcierto
                println("NUEVA PROBABILIDAD " + (probabilidadActu + 1))
                CRUDEntradas.actualizacionDificultad(entradaRespuesta, probabilidadActu + 1)
            }

            if(contVeces==0)
                //REPRODUCIMOS EL AUDIO, SI TIENE,  DE LA PALABRA SI ACIERTA
                //SOLO UNA VEZ, EN EL CASO DE QUE HAYA PALABRAS IGUALES EN SIGNIFICADO Y ESCRITURA EN INGLES
                if (entradaRespuesta.audio!!.isNotEmpty()) {
                    audio = MediaPlayer()
                    Sonido.preparacionAudio(audio, entradaRespuesta.idEntrada)
                    audio.start()

                }

            contVeces++
        }
    }

    fun obtenerListaParaTest(listaIds: IntArray):MutableList<Entrada>
    {
        var palabras= mutableListOf<Entrada>()

        for(id in listaIds )
        {
            palabras.add(CRUDEntradas.obtenerEntradaPorId(id)!!)
        }

        return palabras
    }

    fun cambioEstilo(fondo:Int, colorTexto:Int)
    {
        field_respuesta.setBackgroundResource(fondo)
        field_respuesta.setTextColor(ContextCompat.getColor(this, colorTexto))
    }

    fun mostrarPalabra()
    {


        do {
            test.entradaPregunta = test.obtenerPalabra(test.listaTest)

            //VUELVE A PEDIR OTRA PALABRA SI LA ANTERIOR ES IGUAL, QUE LA QUE ACABA DE PEDIR
        }while(test.entradaPregunta.escrituraIngles==test.ultimaMostrada && test.listaTest.size>1)


        oportunidad=2

        //ASIGNAMOS A LA ULTIMA PALABRA LA PALABRA QUE ACABA DE SALIR
        test.ultimaMostrada = test.entradaPregunta.escrituraIngles!!


        //ESPERAMOS PARA CAMBIAR EL ESTILO DEL CAMPO RESPUESTA
        //ASÍ HACEMOS VER AL USUARIO QUE HA ACERTADO O FALLADO ANTES DE CAMBIAR LA PALABRA
        //Y ACTUALIZAMOS PARA LA NUEVA PREGUNTA
        handler.postDelayed( {

            cardView.visibility= VISIBLE
            field_pregunta.setText(getString(R.string.preguntaTest)+test.entradaPregunta.significado?.toUpperCase()+"\" ?")
            AnimaWidget.transiccion(cardView,0.0f, 1.0f, AnimaWidget.UNSEGUNDO.toLong())
            cambioEstilo(R.drawable.borde_respuesta_test, R.color.colorPrimary)
            field_respuesta.setText("")

        }, 800)


        //ESCONDEMOS EL TECLADO CADA VEZ QUE SE MUESTRA UNA NUEVA PALABRA
        esconderTeclado()
        //QUITAMOS EL FOCO DE LA RESPUESTA
        field_respuesta.clearFocus()

    }

    fun esconderTeclado() {

        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(field_respuesta.getWindowToken(), 0)
    }

    fun alertafinEstadisticas() {

        val inflater= layoutInflater

        vistaInformacion=inflater.inflate(R.layout.informacion_test, null)



        rellenarCamposVistaInformacion()

        AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialog))
            .setView(vistaInformacion)
            .setPositiveButton(
                R.string.aceptar,
                { dialogInterface: DialogInterface, i: Int ->
                    finish()
                })
            .setNegativeButton(R.string.cancelar, {dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()
            })
            .create().show()

    }


    fun rellenarCamposVistaInformacion()
    {
        vistaInformacion.campo_aciertos.text=test.numeroAcertadas.toString()
        vistaInformacion.campo_fallos.text=test.numeroFallidas.toString()

        //LISTA DE PALABRAS MÁS FALLIDAS
        var laMasFallidas= test.obtenerLaPalabraMasFallida()

        if(laMasFallidas.isNotEmpty()) {

            vistaInformacion.necesita_mejorar.visibility=VISIBLE
            if (test.listaMasFallos.any { it >= ClassTest.MUCHOSFALLOS })
                vistaInformacion.necesita_mejorar.setText(getString(R.string.muchosFallos))
            else if (test.listaMasFallos.any { it <= ClassTest.POCOSFALLOS })
                vistaInformacion.necesita_mejorar.setText(getString(R.string.pocosFallos))

            for (p in laMasFallidas) {
                vistaInformacion.caja_words_fallos.addView(nuevaPalabraVista(p.escrituraIngles))
            }
        }
        else{
            if(test.numeroAcertadas>0) {
                vistaInformacion.necesita_mejorar.setText(getString(R.string.congrat))
                vistaInformacion.necesita_mejorar.visibility= VISIBLE
            }
            else
                vistaInformacion.necesita_mejorar.visibility=GONE
        }
    }

    fun nuevaPalabraVista(palabra:String?):TextView
    {
        var textView=TextView(this)
        textView.setText(palabra?.toUpperCase())
        textView.textSize=24f
        textView.gravity=Gravity.CENTER
        textView.setTextColor(ContextCompat.getColor(this, R.color.dif_end_2))
        textView.setTypeface(ResourcesCompat.getFont(this, R.font.londrina_shadow))

        return textView
    }




}
