package morajavier.pdm.voclearn.Vistas

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.android.synthetic.main.barra_guardar_atras.*
import morajavier.pdm.voclearn.BaseDatos.CRUDEntradas
import morajavier.pdm.voclearn.FuncionesExtension.cargarImagen
import morajavier.pdm.voclearn.FuncionesExtension.cargarNotCache
import morajavier.pdm.voclearn.FuncionesExtension.crearSpinnerCarga
import morajavier.pdm.voclearn.Imagen
import morajavier.pdm.voclearn.Modelo.Entrada
import morajavier.pdm.voclearn.R
import morajavier.pdm.voclearn.SecurityCopy
import morajavier.pdm.voclearn.Sonido
import java.io.File


class AddActivity : AppCompatActivity(),  ActivityCompat.OnRequestPermissionsResultCallback {




    //RUTA DONDE SE GUARDARA LA IMAGEN
    var rutaImagen:String=""
    //FICHERO DONDE SE ALMACENARA LA IMAGEN OBTENIDA POR LA CAMARA O GALERIA
    var ficheroAlmacenImagen:File?=null
    //OBJETO PARA LA GRABACIÓN DEL AUDIO PASAMOS EL CONTEXTO, Y EL ID DEL NUEVO AUDIO,  QUE SON LOS REQUERIDOS
    var gestionSondio=Sonido(this, CRUDEntradas.nuevoId()!!)

    companion object{
        //CONSTANTES PARA LA ELECCIÓN DE LA FUENTE DE IMAGEN, QUE PUEDE SER DESDE LA GALERIA O DE LA CAMARA
        const val RESPUESTA_GALERIA = 0
        const val RESPUESTA_CAMARA=1
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        //SE COMPRUEBAN LOS PERMISOS DE ALMACENAMIENTO EXTERNO
        SecurityCopy.comprobarTodosPermisos(this)


        //AL PULSAR EL BOTON ATRÁS VOLVEMOS AL FRAGMENT PADRE
        btn_atras.setOnClickListener{
            finish()
        }

        eventosCamposObligatorios()
        //CHEQUEAMOS EL RADIOBUTON AL DIFICIL POR DEFECTO
        grupo_radio.check(R.id.radio_dif)

        //GUARDAMOS EL NUEVO OBJETO Y VOLVEMOS A LA ACTIVITY ANTERIOR
        btn_guardar.setOnClickListener{

            guardarObjetoEnBD()
            finish()
        }

        btn_grabar.setOnTouchListener{v, event->
            if(SecurityCopy.perAceptados) {
                gestionSondio.grabadora(v, event )
            }else{
                Toast.makeText(v.context, getString(R.string.permisosMicro), Toast.LENGTH_SHORT).show()
                return@setOnTouchListener false
            }


        }



        //EVENTO PARA CUANDO PULSE EL IMAGEVIEW PARA ELLO DEBE TENER PERMISOS
        img_click.setOnClickListener{
            if(SecurityCopy.perAceptados) {
                //OBTENEMOS EL FICHERO DONDE SE ALMACENARÁ LA IMAGEN OBTENIDA POR GALERIA O CÁMARA
                //PASÁNDOLE UN NUEVO ID
                ficheroAlmacenImagen=Imagen.creacionFicheroImagen(CRUDEntradas.nuevoId()!!)
                Imagen.seleccionarFuenteImagen(this, ficheroAlmacenImagen!!)
            }else{
                Toast.makeText(it.context,  getString(R.string.perImagenDenegado), Toast.LENGTH_SHORT).show()

            }
        }


    }


    //RESPUESTA PARA IMAGEN
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode== Activity.RESULT_OK)
        {
            if(requestCode== RESPUESTA_GALERIA)
            {

                //CREAMOS UN FICHERO CON LA URL(URI) QUE NOS DEVUELVE LA ACTIVIDAD (data.data)
                val ficheroEntrada= File(Imagen.getPath(data!!.data!!, this))

                //COPIAMOS LA IMAGEN EN EL ALMACENAMIENTO EXTERNO PARA NO DEPENDER DE LA GALERIA
                //GUARDAMOS LA RUTA EN LA VARIABLE QUE UTILIZAREMOS PARA EL OBJETO QUE GUARDEMOS
               rutaImagen=SecurityCopy.volcarFichero(ficheroAlmacenImagen!!, ficheroEntrada)
                println("IMAGEN RESPUESTA GALERIA "+rutaImagen)

                //FUNCIÓN DE EXTENSION QUE CARGA LA IMAGEN CON GLIDE(LIBRERIA)
                img_click.cargarNotCache(rutaImagen, this.crearSpinnerCarga(5f, 30f))




            }
            else if (requestCode== RESPUESTA_CAMARA)
            {
                //CÓDIGO DE Cristian Henao(YOUTUBE
                //arrayOf(ficheroAlmacenImagen!!.absolutePath) -->RUTA DONDE SE ALMACENÓ LA IMAGEN AL CAPTURAR LA FOTO
                //ficheroAlmacenImagen-->LO PASAMOS POR EL PutExtra eN EL INTENT
                MediaScannerConnection.scanFile(this, arrayOf(ficheroAlmacenImagen!!.absolutePath) , null,
                    { path, uri ->

                        println("IMAGEN RESPUESTA FOTO "+ path)
                        println("IMAGEN RESPUESTA FOTO uri "+ uri)
                    })

                //FUNCIÓN DE EXTENSION QUE CARGA LA IMAGEN CON GLIDE(LIBRERIA)
                rutaImagen=ficheroAlmacenImagen!!.absolutePath
                img_click.cargarNotCache(rutaImagen, this.crearSpinnerCarga(5f, 30f))


            }
        }
    }

    fun guardarObjetoEnBD()
    {
        val id=CRUDEntradas.nuevoId()
        val palabraIngles=field_word.text.toString()
        val traduccion=field_traduccion.text.toString()
        val descripcion=field_descri.text.toString()
        val probabilidad=obtenerProbabilidad()
        val entradaNueva=Entrada(id!! ,traduccion, descripcion, probabilidad, palabraIngles, rutaImagen, gestionSondio.rutaAudio )

        CRUDEntradas.nuevaOActualizarEntrada(entradaNueva)

        vaciarCampos()

        Toast.makeText(this,"Se ha guardado la palabra "+palabraIngles, Toast.LENGTH_SHORT).show()


    }


    fun vaciarCampos()
    {
        field_traduccion.setText("")
        field_word.setText("")
        field_descri.setText("")
        grupo_radio.check(R.id.radio_dif)
        gestionSondio.rutaAudio=""


        field_descri.clearFocus()
        field_traduccion.clearFocus()
        field_word.clearFocus()
    }


    fun obtenerProbabilidad():Int{

        when (grupo_radio.checkedRadioButtonId) {
            R.id.radio_dif -> {
                return 1
            }
            R.id.radio_med -> {

                return 2
            }
            R.id.radio_fac -> {

                return 3
            }

        }

        return 1
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
                if (grantResults.isEmpty() ||
                    (grantResults[0] != PackageManager.PERMISSION_GRANTED &&
                            grantResults[1] != PackageManager.PERMISSION_GRANTED)||
                    grantResults[2] != PackageManager.PERMISSION_GRANTED) {

                    SecurityCopy.perAceptados=false
                    Log.e("PERMISOS DENEGADOS ","permiso denegado")


                }
                else
                {
                    SecurityCopy.perAceptados=true
                    Log.e("PERMISOS ACEPTADOS ","permiso aceptado")


                }
                return
            }

            else -> {

            }

        }

    }


    //ESTE MÉTODO CHEQUEA QUE LOS DOS CAMPOS OBLIGATORIOS ESTEN RELLENOS, Y EN EL CASO DE QUE LO ESTÉN,
    //SE MUESTRA EL BÓTON DE GUARDAR LA PALABRA
    fun eventosCamposObligatorios()
    {
        //PONEMOS EL BOTÓN DE GUARDAR INVISIBLE MIENTRAS QUE NO SE RELLENEN LOS DOS CAMPOS OBLIGATORIOS
        btn_guardar.visibility=GONE

        field_traduccion.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                println(" afterTextChanged "+ p0)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                println(" beforeTextChanged "+ p0 + " "+ p1+" "+p2+" "+p3+" ")
            }

            //SOLO HACEMOS USO DE ESTE MÉTODO, SI AL CAMBIAR EL TEXTO DEL CAMPO, EL MISMO ESTÁ VACÍO
            //O EL OTRO OBLIGATORIO ESTA VACÍO EL BOTÓN GUARDAR PERMANECERÁ OCULTO
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                println(" onTextChanged "+ p0 + " "+ p1+" "+p2+" "+p3+" ")

                if(p0.toString().isEmpty() || field_word.text!!.isEmpty())
                {
                    btn_guardar.visibility=GONE
                }
                else
                {
                    btn_guardar.visibility=VISIBLE

                }
            }
        })

        field_word.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                println(" afterTextChanged "+ p0)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                println(" beforeTextChanged "+ p0 + " "+ p1+" "+p2+" "+p3+" ")
            }

            //SOLO HACEMOS USO DE ESTE MÉTODO, SI AL CAMBIAR EL TEXTO DEL CAMPO, EL MISMO ESTÁ VACÍO
            //O EL OTRO OBLIGATORIO ESTA VACÍO EL BOTÓN GUARDAR PERMANECERÁ OCULTO
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                println(" onTextChanged "+ p0 + " "+ p1+" "+p2+" "+p3+" ")

                if(p0.toString().isEmpty() || field_traduccion.text!!.isEmpty())
                {
                    btn_guardar.visibility=GONE
                }
                else
                {
                    btn_guardar.visibility=VISIBLE

                }
            }
        })

    }


}
