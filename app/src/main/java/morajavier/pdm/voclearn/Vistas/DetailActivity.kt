package morajavier.pdm.voclearn.Vistas

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaScannerConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_detail.field_traduccion
import kotlinx.android.synthetic.main.barra_guardar_atras.*
import morajavier.pdm.voclearn.*
import morajavier.pdm.voclearn.BaseDatos.CRUDEntradas
import morajavier.pdm.voclearn.FuncionesExtension.cambioImagen
import morajavier.pdm.voclearn.FuncionesExtension.cargarNotCache
import morajavier.pdm.voclearn.FuncionesExtension.crearSpinnerCarga
import morajavier.pdm.voclearn.Modelo.Entrada
import java.io.File

class DetailActivity : AppCompatActivity() {

    lateinit  var  objetoDetalle: Entrada
    //OBJETO GRABADORA
    lateinit  var gestionSondio: Sonido
    //OBJETO PARA REPRODUCIR EL AUDIO
    var reproducirAudio= MediaPlayer()
    //RUTAS DE IMAGEN Y AUDIO NUEVAS
    var ficheroAlmacenImagen : File? = null
    var rutaImagen=""
    var rutaAudio=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        //SE COMPRUEBAN LOS PERMISOS DE ALMACENAMIENTO EXTERNO Y MICRO
        SecurityCopy.comprobarTodosPermisos(this)



        limpiarFocos()

        val idEntrada=intent.getIntExtra("idEntrada", -1)
        objetoDetalle= CRUDEntradas.obtenerEntradaPorId(idEntrada)!!

        //EN ESTE CASO AL ACTUALIZAR, PASAMOS EL MISMO ID DE AUDIO, Y LA ACTIVITY DE CONTEXTO
        //YA QUE VA A SER EL MISMO OBJETO EL QUE SE VA A MODIFICAR
        gestionSondio= Sonido(this, idEntrada)

        objetoDetalle?.let{ cargarCamposDatos()}

        btn_atras.setOnClickListener{
            limpiarFocos()
            finish()
        }


        //ESCUCHAR EL AUDIO DE LA PALABRA
        btn_audio.setOnClickListener {

            it.cambioImagen(reproducirAudio)
        }

        //CARGA UNA NUEVA IMAGEN, PARA ELLO DEBE TENER PERMISOS
        card_imagen.setOnClickListener {
            if(SecurityCopy.perAceptados){
            //OBTENEMOS EL FICHERO DONDE SE ALMACENARÁ LA IMAGEN OBTENIDA POR GALERIA O CÁMARA
                //PASAMOS EL ID DEL OBJETO QUE SE VA A MODIFICAR
                ficheroAlmacenImagen = Imagen.creacionFicheroImagen(idEntrada)
                Imagen.seleccionarFuenteImagen(this, ficheroAlmacenImagen!!)
            }else{
                Toast.makeText(it.context,  getString(R.string.perImagenDenegado), Toast.LENGTH_SHORT).show()

            }

        }

        //ESTE EVENTO LLAMA A LA GRABADORA, SI ESTA PULSADO EL BOTÓN, GRABA, SI LO SUELTA DEJA DE GRABAR
        //SI LA GRABACIÓN SE REALIZA, isGuardado SE PONE A TRUE, Y SE LE PASAA GUARDAR AUDIO
        //PARA ELLO DEBE TENER LOS PERMISOS ASIGNADOS
        btn_record.setOnTouchListener{v, event->
            if(SecurityCopy.perAceptados) {
                val isGrabado = gestionSondio.grabadora(v, event)
                guardarAudio(isGrabado)
                return@setOnTouchListener isGrabado
            }
            else{
                Toast.makeText(v.context,  getString(R.string.permisosMicro), Toast.LENGTH_SHORT).show()
                return@setOnTouchListener false
            }

        }





        listenerCamposTexto()

    }

    fun guardarAudio(isGrabado :Boolean){

        if(isGrabado) {
            //GUARDAMOS LA NUEVA RUTA CON OTRO SONIDO, SI NO ESTA VACIA
            CRUDEntradas.actualizarPropiedadObjeto(objetoDetalle, "audio", gestionSondio.rutaAudio)
            //Y VOLVEMOS A CARGAR EL MEDIA PLAYER PARA EL NUEVO AUDIO
            cargaAudio()
        }


    }


    fun listenerCamposTexto()
    {
        //SI TIENEN EL FOCO EL BACKGROUND CAMBIA, COMO INDICACIÓN DE QUE VA A SER MODIFICADO

        field_ingles.setOnFocusChangeListener { v, hasFocus ->

            if(hasFocus)
            {
                v.setBackgroundResource(R.drawable.border_modificar)

            }
            else
            {
                v.setBackgroundResource(android.R.color.transparent)
                val datoTraduccion= field_traduccion.text.toString()
                val datoPalabra=field_ingles.text.toString()

                //SI NINGUNO DE LOS DOS CAMPOS OBLIGATORIOS ESTAN VACIOS
                if(!datoTraduccion.trim().isEmpty() && !datoPalabra.trim().isEmpty())
                    CRUDEntradas.actualizarPropiedadObjeto(objetoDetalle, "palabra", datoPalabra)
            }


        }

        field_traduccion.setOnFocusChangeListener { v, hasFocus->

            if(hasFocus)
            {
                v.setBackgroundResource(R.drawable.border_modificar)


            }
            else
            {
                v.setBackgroundResource(android.R.color.transparent)
                val datoTraduccion= field_traduccion.text.toString()
                val datoPalabra=field_ingles.text.toString()

                //SI NINGUNO DE LOS DOS CAMPOS OBLIGATORIOS ESTAN VACIOS
                if(!datoTraduccion.trim().isEmpty() && !datoPalabra.trim().isEmpty())
                    CRUDEntradas.actualizarPropiedadObjeto(objetoDetalle, "traduccion", datoTraduccion)

            }

        }

        field_descriccion.setOnFocusChangeListener { v, hasFocus->

            if(hasFocus)
            {
                v.setBackgroundResource(R.drawable.border_modificar)


            }
            else
            {
                v.setBackgroundResource(android.R.color.transparent)
                CRUDEntradas.actualizarPropiedadObjeto(objetoDetalle, "descripcion", field_descriccion.text.toString())

            }


        }
    }

    fun cargarCamposDatos() {

        field_ingles.setText(objetoDetalle.escrituraIngles)
        field_traduccion.setText(objetoDetalle.significado)
        rutaAudio = objetoDetalle.audio!!
        rutaImagen = objetoDetalle.imagen!!

        //SI LA DESCRIPCIÓN ESTÁ VACÍA COLOCAMOS EL HINT DE AYUDA
        if(objetoDetalle.descripcion!!.isEmpty()){

            field_descriccion.setHint(R.string.addDescrExpl)
        }
        else
        {
            field_descriccion.setText(objetoDetalle.descripcion)
        }

        //SI LA IMAGEN ESTA VACÍA PONEMOS DE FONDO EL RECURSO QUE TENEMOS PARA ELLO
        if(objetoDetalle.imagen!!.isEmpty())
        {
            card_imagen.setBackgroundResource(R.mipmap.ic_launcher_no_image)
        }
        else
        {
           card_imagen.cargarNotCache(objetoDetalle.imagen!!, this.crearSpinnerCarga(8f, 40f))
        }

        cargaAudio()
    }


    fun cargaAudio()
    {


        //SI LA ENTRADA DE AUDIO ES NULA O ES UNA CADENA VACÍA, HACEMOS DESAPARECER EL BOTON DEL PLAY,
        // POR LO CONTRARIO LO MOSTRAMOS
        objetoDetalle.audio?.let{
            if(!it.isEmpty()) {
                btn_audio.setVisibility(View.VISIBLE)

                //PREPARAMOS EL AUDIO CORRESPONDIENTE A ESTA PALABRA, QUE SE ENCUENTRA EN LA
                //MEMORIA INTERNA DEL MÓVIL.
                reproducirAudio= MediaPlayer()
                Sonido.preparacionAudio(reproducirAudio!!, objetoDetalle.idEntrada)

                //CUANDO TERMINE DE REPRODUCIRSE EL AUDIO, CAMBIAMOS EL ICONO
                reproducirAudio!!.setOnCompletionListener {
                    btn_audio.setBackgroundResource(android.R.drawable.ic_media_play)

                }
            }
            else
            {
                btn_audio.setVisibility( View.INVISIBLE)
            }


        }?:btn_audio.setVisibility(View.GONE)

    }

    //RESPUESTA PERMISOS
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    )
    {
        //MENSAJE CONTROL PARÁMETROS
        for(p in permissions  )
            Log.e("ARRAY DE PERMISOS ",""+p)
        for(p in grantResults  )
            Log.e("RESULTADOS ",""+p)
        Log.e("RESPUESTA CODE ", ""+requestCode)
        when (requestCode) {
            SecurityCopy.REQUEST_EXTERNAL_STORAGE -> {
                //SI LOS PERMISOS DE ALMACENAMIENTO EXTERNO NO SE ACEPTARON
                //LEVANTAMOS LA VANDERA DE LOS PERMISOS Y NOTIFICANOS POR CONSOLA
                if ((grantResults.isEmpty() ||
                     grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                     grantResults[1] != PackageManager.PERMISSION_GRANTED)||
                     grantResults[2] != PackageManager.PERMISSION_GRANTED) {

                    SecurityCopy.perAceptados=false


                }
                else
                {
                    SecurityCopy.perAceptados=true


                }
                return
            }

            else -> {

            }

        }

    }


    fun limpiarFocos()
    {
        field_descriccion.clearFocus()
        field_traduccion.clearFocus()
        field_ingles.clearFocus()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode== Activity.RESULT_OK)
        {

            if(requestCode== AddActivity.RESPUESTA_GALERIA)
            {

                //CREAMOS UN FICHERO CON LA URL(URI) QUE NOS DEVUELVE LA ACTIVIDAD (data.data)
                val ficheroEntrada= File(Imagen.getPath(data!!.data!!, this))

                //COPIAMOS LA IMAGEN EN EL ALMACENAMIENTO EXTERNO PARA NO DEPENDER DE LA GALERIA
                //GUARDAMOS LA RUTA EN LA VARIABLE QUE UTILIZAREMOS PARA EL OBJETO QUE GUARDEMOS
                rutaImagen= SecurityCopy.volcarFichero(ficheroAlmacenImagen!!, ficheroEntrada)
                println("IMAGEN RESPUESTA GALERIA "+rutaImagen)


                CRUDEntradas.actualizarPropiedadObjeto(objetoDetalle, "imagen", rutaImagen)
                //FUNCIÓN DE EXTENSION QUE CARGA LA IMAGEN CON GLIDE(LIBRERIA)
                card_imagen.cargarNotCache(rutaImagen, this.crearSpinnerCarga(8f, 40f))


            }
            else if (requestCode== AddActivity.RESPUESTA_CAMARA)
            {
                //CÓDIGO DE Cristian Henao(YOUTUBE
                //arrayOf(ficheroAlmacenImagen!!.absolutePath) -->RUTA DONDE SE ALMACENÓ LA IMAGEN AL CAPTURAR LA FOTO
                //ficheroAlmacenImagen-->LO PASAMOS POR EL PutExtra eN EL INTENT
                MediaScannerConnection.scanFile(this, arrayOf(ficheroAlmacenImagen!!.absolutePath) , null,
                    { path, uri ->

                        println("IMAGEN RESPUESTA FOTO "+ path)
                    })


                CRUDEntradas.actualizarPropiedadObjeto(objetoDetalle, "imagen", rutaImagen)
                //FUNCIÓN DE EXTENSION QUE CARGA LA IMAGEN CON GLIDE(LIBRERIA)
                rutaImagen=ficheroAlmacenImagen!!.absolutePath
                card_imagen.cargarNotCache(rutaImagen, this.crearSpinnerCarga(8f, 40f))


            }
        }
        else{
            //SI LA RESPUESTA DE LA IMAGEN NO ES OK, COLOCAMOS LA RUTA DE LA IMÁGEN QUE TENIA EL OBJETO
            rutaImagen=objetoDetalle.imagen!!
        }
    }


}
