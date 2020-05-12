package morajavier.pdm.voclearn.Vistas

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.media.MediaRecorder
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.net.toFile
import androidx.loader.content.CursorLoader
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_add.*
import morajavier.pdm.voclearn.BaseDatos.CRUDEntradas
import morajavier.pdm.voclearn.FuncionesExtension.cargarImagen
import morajavier.pdm.voclearn.Modelo.Entrada
import morajavier.pdm.voclearn.R
import morajavier.pdm.voclearn.SecurityCopy
import morajavier.pdm.voclearn.SecurityCopy.Companion.PATH_TO_WRITE
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.lang.RuntimeException

class AddActivity : AppCompatActivity() {


    //OBJETO PARA LA GRABACIÓN DEL AUDIO
    var grabacion: MediaRecorder? =null
    //RUTA GRABACIÓN AUDIO
    var rutaAudio:String=""
    //RUTA DONDE SE GUARDARA LA IMAGEN
    var rutaImagen:String=""
    //FICHERO DONDE SE ALMACENARA LA IMAGEN OBTENIDA POR LA CAMARA O GALERIA
    var ficheroAlmacenImagen:File?=null

    companion object{
        //CONSTANTES PARA LA ELECCIÓN DE LA FUENTE DE IMAGEN, QUE PUEDE SER DESDE LA GALERIA O DE LA CAMARA
        const val RESPUESTA_GALERIA = 0
        const val RESPUESTA_CAMARA=1
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        //PERMISOS DE ACCESO A MICRÓFONO EN ESTA ACTIVITY, Y DE ALMACENAMIENTO
        if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(applicationContext,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED
        )
        {
            ActivityCompat.requestPermissions(
                this@AddActivity,
                arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                1000
            )
        }
        else
        {
            Log.e("PERMISOS", "los permisos de escritura y micrófono ya fueron concedidos")

        }


        //ESCONDEMOS EL TECLADO CUANDO PINCHAMOS FUERA DE LOS VIEW, Y QUITAMOS EL FOCO DE LOS CAMPOS DE TEXTO
        layout_prin.setOnClickListener{

           esconderTeclado(it)
            limpiarFocos()

        }


        //AL PULSAR EL BOTON ATRÁS VOLVEMOS AL FRAGMENT PADRE
        btn_atras.setOnClickListener{
            finish()
        }

        eventosCamposObligatorios()
        //CHEQUEAMOS EL RADIOBUTON AL DIFICIL POR DEFECTO
        grupo_radio.check(R.id.radio_dif)
        eventoBotonGrabar()

        //GUARDAMOS EL NUEVO OBJETO Y VOLVEMOS A LA ACTIVITY ANTERIOR
        btn_guardar.setOnClickListener{

            guardarObjetoEnBD()
            finish()
        }


        //EVENTO PARA CUANDO PULSE EL IMAGEVIEW
        img_click.setOnClickListener{

            seleccionarFuenteImagen()
        }


    }





    //MÉTODO QUE ABRE UN CUADRO DE DIALOGO CON EL USUARIO, PARA LA ELECCIÓN DE LA FUENTE DE LA IMAGEN
    fun seleccionarFuenteImagen()
    {
        //ARRAY CON LAS TRES OPCIONES QUE LE PASAREMOS AL AlertDialog
        val opciones =arrayOf<CharSequence>(resources.getString(R.string.galeria),
                                                   resources.getString(R.string.camara),
                                                   resources.getString(R.string.cancelar))

        val ventanDialogo=AlertDialog.Builder(this)
        ventanDialogo.setTitle(resources.getString(R.string.opciones))
        ventanDialogo.setItems(opciones,{ dialogInterface: DialogInterface, i: Int ->

            if(opciones[i].equals(resources.getString(R.string.galeria)))
            {
                //OBTENEMOS EL FICHERO DONDE SE ALMACENARÁ LA IMAGEN OBTENIDA POR GALERIA
                ficheroAlmacenImagen=creacionFicheroImagen()
                abrirActivityGaleria()
                println("GALERIA")
            }
            else if(opciones[i].equals(resources.getString(R.string.camara)))
            {
                val pm=packageManager
                //SI EL DISPOSITIVO TIENE CÁMARA, ACCEDEMOS A ELLA
                if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
                {
                    //OBTENEMOS EL FICHERO DONDE SE ALMACENARÁ LA IMAGEN OBTENIDA POR GALERIA
                    ficheroAlmacenImagen=creacionFicheroImagen()
                    abrirActivityCamara()
                }

                println("CAMARA")
            }
            else
            {
                dialogInterface.dismiss()
                println("CANCELAR")
            }

        })

        ventanDialogo.show()
    }

    fun abrirActivityCamara()
    {


        //IGUAL QUE CON LA OPCIÓN DE GALERIA, COMPROBAMOS SI EL INTENT NO ES NULO, SI NO LO ES
        //LANZAMOS EL startActivityForResult CON EL OBJETO INTENT
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                //PASAMOS POR EL putExtra EL FICHERO DONDE SE ALMACENARÁ LA CAPTURA DE LA FOTO
                //INDICANDO CON EL MediaStore.EXTRA_OUTPUT QUE REALICE ESA ACCIÓN
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(ficheroAlmacenImagen))
                startActivityForResult(takePictureIntent, RESPUESTA_CAMARA )
            }
        }

    }

    fun abrirActivityGaleria()
    {
        //CREAMOS EL INTENT, Y USAMOS Intent.ACTION_PICK, PARA QUE MUESTRA UNA LISTA DE OBJETOS(IMAGENES) A SELECCIONAR
        //EL SEGUNDO PARÁMETRO NOS PERMITIRA ACCEDER EN LA RESPUESTA, A LA OPCION ELEGIDA
        //PARA LANZAR ESTA ACTIVIDAD ES NECESARIO HACERLO CON startActivityForResult(), POR QUE LA RESPUESTA ES SEGURA
        var intent=Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        //SETEAMOS SU TIPO
        intent.setType("image/")

        //LANZAMOS LA ACTIVIDAD(SI resolveActivity NO ES NULO, CON UN CREATE CHOOSER POR SI HAY MÁS DE UNA APP DE IMAGENES
        intent.resolveActivity(packageManager)?.let{
            startActivityForResult(Intent.createChooser(intent,resources.getString(R.string.opciones) ), RESPUESTA_GALERIA)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode== Activity.RESULT_OK)
        {


            if(requestCode== RESPUESTA_GALERIA)
            {

                //CREAMOS UN FICHERO CON LA URL QUE NOS DEVUELVE LA ACTIVIDAD
                val ficheroEntrada= File(getPath(data!!.data!!))

                //COPIAMOS LA IMAGEN EN EL ALMACENAMIENTO EXTERNO PARA NO DEPENDER DE LA GALERIA
                //GUARDAMOS LA RUTA EN LA VARIABLE QUE UTILIZAREMOS PARA EL OBJETO QUE GUARDEMOS
               rutaImagen=SecurityCopy.volcarFichero(ficheroAlmacenImagen!!, ficheroEntrada)
                println("IMAGEN RESPUESTA GALERIA "+rutaImagen)

                //FUNCIÓN DE EXTENSION QUE CARGA LA IMAGEN CON GLIDE(LIBRERIA)
                img_click.cargarImagen(rutaImagen)



                //FUNCIÓN DE EXTENSION QUE CARGA LA IMAGEN CON GLIDE(LIBRERIA)
                rutaImagen=ficheroAlmacenImagen!!.absolutePath
                img_click.cargarImagen(rutaImagen)



            }
            else if (requestCode== RESPUESTA_CAMARA)
            {
                //CÓDIGO DE Cristian Henao(YOUTUBE
                //arrayOf(ficheroAlmacenImagen!!.absolutePath) -->RUTA DONDE SE ALMACENÓ LA IMAGEN AL CAPTURAR LA FOTO
                //ficheroAlmacenImagen-->LO PASAMOS POR EL PutExtra eN EL INTENT
                MediaScannerConnection.scanFile(this, arrayOf(ficheroAlmacenImagen!!.absolutePath) , null,
                                                { path, uri ->

                                                    println("IMAGEN RESPUESTA FOTO "+ path)
                                                })

                //FUNCIÓN DE EXTENSION QUE CARGA LA IMAGEN CON GLIDE(LIBRERIA)
                rutaImagen=ficheroAlmacenImagen!!.absolutePath
                img_click.cargarImagen(rutaImagen)


            }
        }
    }

    fun creacionFicheroImagen():File
    {
        //CREAMOS UN DIRECTORIO PARA GUARDAR LA IMAGENES OBTENIDAS YA SEA CÁMARA O GALERIA
        val dirAlmacenamientoImagen=File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"/ImagenesVocLearn")
        //SI NO ESTA CREADO SE CREA
        if (!dirAlmacenamientoImagen.exists())
            dirAlmacenamientoImagen.mkdirs()

        //CREAMOS EL FICHERO DONDE SE ALMACENARÁ LA IMAGEN NUEVA
        val ficheroImg="IMG"+CRUDEntradas.nuevoId()+".jpg"
        var ficheroAlmacenamientoImagen=File(dirAlmacenamientoImagen,ficheroImg)

        return ficheroAlmacenamientoImagen
    }

    //MÉTODO ENCONTRADO EN STACKOVERFLOW, QUE DEVUELVE UN STRING CON LA RUTA DE LA IMAGEN OBTENIDA POR LA GALERIA
    //PARA COPIARLA EN LA MEMORIA EXTERNA Y HACER USO DE ELLA PARA OBTENER LAS IMAGENES DE LA APP
    fun getPath(uri :Uri):String{
        var cursor: Cursor? =null
        try {
            var projection = arrayOf("_data")
            cursor = this.applicationContext.getContentResolver()
                .query(uri, projection, null, null, null)
            var columnIndex = cursor!!.getColumnIndexOrThrow("_data")
            cursor.moveToFirst()
            return cursor.getString(columnIndex)
        }
        finally {
            cursor?.let{it.close()}
        }
    }





    fun guardarObjetoEnBD()
    {
        val id=CRUDEntradas.nuevoId()
        val palabraIngles=field_word.text.toString()
        val traduccion=field_traduccion.text.toString()
        val descripcion=field_descri.text.toString()
        val probabilidad=obtenerProbabilidad()
        val entradaNueva=Entrada(id!! ,traduccion, descripcion, probabilidad, palabraIngles, rutaImagen, rutaAudio )

        CRUDEntradas.nuevaOActualizarEntrada(entradaNueva)

        vaciarCampos()

        Toast.makeText(this,"Se ha guardado la palabra "+palabraIngles, Toast.LENGTH_SHORT).show()


    }

    fun limpiarFocos()
    {
        field_descri.clearFocus()
        field_traduccion.clearFocus()
        field_word.clearFocus()
    }

    fun esconderTeclado(it: View)
    {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(it.getWindowToken(), 0)
    }

    fun vaciarCampos()
    {
        field_traduccion.setText("")
        field_word.setText("")
        field_descri.setText("")
        grupo_radio.check(R.id.radio_dif)
        rutaAudio=""

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

    //EN ESTE MÉTODO COMPROBAMOS SI EL BOTON DE GRABAR ESTA PULSADO O NO PARA GRABAR O DEJAR DE GRABAR
    //ADEMÁS AÑADIMOS UNA ANIMACIÓN EN ESCALA PARA QUE MIENTRAS ESTE GRABANDO EL MICRÓFONO ESTE ANIMADO
    fun eventoBotonGrabar()
    {
        //VARIABLE QUE UTILIZAREMOS PARA EL CONTROL DE LOS 20 SEGUNDOS DE DURACIÓN DEL AUDIO
        var handler = Handler()

        btn_grabar.setOnTouchListener { v, event ->

            println("ACTION BOTON"+ event.action)
            if (event.action == MotionEvent.ACTION_DOWN ) {
                grabacion = MediaRecorder()
                v.setBackgroundResource(R.drawable.ic_action_grabando)
                var animacionMicro = AnimationUtils.loadAnimation(this, R.anim.anim_micro)
                v.startAnimation(animacionMicro)

                grabacion?.let { grabandoAudio(it) }

                //FUERCE A AVISAR AL USUARIO QUE LA GRABACIÓN SE HA DETENIDO. LA GRABACIÓN ESTA CONFIGURADA A 20 SEG MAX
                 tiempoDuracionAudio(handler)

                return@setOnTouchListener true

            } else if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL ) {
                v.setBackgroundResource(R.drawable.ic_action_grabar)
                v.clearAnimation()
                grabacion?.let { stopAudio(it) }

                //CANCELAMOS CUALQUIER POSTDELAY QUE ESTÉ A LA ESPERA, LLAMADO EN EL METODO tiempoDuracion ANTERIOR
                handler.removeCallbacksAndMessages(null)

                return@setOnTouchListener true
            }
            else
            {
                return@setOnTouchListener false
            }


        }
    }

    //ESTA FUNCIÓN HARÁ QUE A LOS 20 SEGUNDOS EL BOTÓN VUELVA A SU ESTADO ORIGINAL AUNQUE SE MANTENGA PULSADO
    fun tiempoDuracionAudio(handler: Handler)
    {

        handler.postDelayed( {

            //SI PASA EL TIEMPO DE GRABACIÓN Y AÚN SIGUE GRABANDO, SE CAMBIA EL BOTÓN Y SE NOTIFICA AL USUARIO
            btn_grabar.setBackgroundResource(R.drawable.ic_action_grabar)
            btn_grabar.clearAnimation()
            Toast.makeText(this, R.string.grabDetenida, Toast.LENGTH_SHORT).show()

        }, 20000)

    }

    fun grabandoAudio(grabacion:MediaRecorder)
    {
        //DEFINIMOS EL FICHERO DONDE SE GUARDARA EL AUDIO, DENTRO DE LA CARPETA DONDE ESTÁ NUESTA COPIA DE SEGURIDAD DE LA BASE DE DATOS
        //CON EL ID DEL OBJETO QUE EN PRINCIPIO SE GUARDARÍA EN LA BASE DE DATOS
        val ficheroAlmacenamientoAudio=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).absolutePath+"/AudioVocLearn"+CRUDEntradas.nuevoId()+".mp3"
        Log.w("AUDIO ALMACENADO", ""+ficheroAlmacenamientoAudio)
        //GUARDAMOS LA RUTA EN UN STRING PARA CUANDO ALMACENEMOS EL OBJETO
        rutaAudio=ficheroAlmacenamientoAudio
        //LE DECIMOS QUE VAMOS A USAR EL MICRÓFONO
        grabacion.setAudioSource(MediaRecorder.AudioSource.MIC)
        //DEFINIMOS EL FORMATO DE SALIDA
        grabacion.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        //DEFINIMOR CODIFICACIÓN DE AUDIO
        grabacion.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)
        //DEFINIMOS EL TIEMPO MAX DE AUDIO, 20 segundos
        grabacion.setMaxDuration(20000)
        //ALMACENAMOS
        grabacion.setOutputFile(ficheroAlmacenamientoAudio)


        try{
            //PREPARAMOS MICRO
            grabacion.prepare()
            //COMIENZA A GRABAR
            grabacion.start()

            Toast.makeText(this,R.string.grabAviso, Toast.LENGTH_SHORT).show()

        }catch(e: IOException)
        {
            Log.e("ERROR AUDIO", "Ha ocurrido un error al grabar el audio")
        }

    }

    fun stopAudio(grabacion:MediaRecorder)
    {
        try {
            //PARAMOS GRABACIÓN
            grabacion.stop()
            //LIBERAMOS RECURSOS
            grabacion.release()

            Toast.makeText(this,R.string.grabTerminada, Toast.LENGTH_SHORT).show()

        }
        catch (e: RuntimeException)
        {
            Toast.makeText(this, R.string.grabFallo, Toast.LENGTH_SHORT).show()
            //NO GUARDAMOS NADA EN LA RUTA PORQUE HA FALLADO AL CREARSE
            rutaAudio=""
            e.printStackTrace()
        }
    }

    //ESTE MÉTODO CHEQUEA QUE LOS DOS CAMPOS OBLIGATORIOS ESTEN RELLENOS, Y EN EL CASO DE QUE LO ESTÉN,
    //SE MUESTRA EL BÓTON DE GUARDAR LA PALABRA
    fun eventosCamposObligatorios()
    {
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
