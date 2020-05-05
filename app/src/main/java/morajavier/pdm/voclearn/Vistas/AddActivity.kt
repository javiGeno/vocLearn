package morajavier.pdm.voclearn.Vistas

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorSet
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.realm.internal.EmptyLoadChangeSet
import kotlinx.android.synthetic.main.activity_add.*
import morajavier.pdm.voclearn.BaseDatos.CRUDEntradas
import morajavier.pdm.voclearn.Modelo.Entrada
import morajavier.pdm.voclearn.R
import java.io.IOException

class AddActivity : AppCompatActivity() {


    //OBJETO PARA LA GRABACIÓN DEL AUDIO
    var grabacion: MediaRecorder? =null
    //RUTA GRABACIÓN AUDIO
    var rutaAudio:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        //PERMISOS DE ACCESO A MICRÓFONO EN ESTA ACTIVITY, Y DE ALMACENAMIENTO
        if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(
                this@AddActivity,
                arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
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

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.getWindowToken(), 0)

            field_descri.clearFocus()
            field_traduccion.clearFocus()
            field_word.clearFocus()

        }


        //AL PULSAR EL BOTON ATRÁS VOLVEMOS AL FRAGMENT PADRE
        btn_atras.setOnClickListener{
            finish()
        }

        eventosCamposObligatorios()
        //CHEQUEAMOS EL RADIOBUTON AL DIFICIL POR DEFECTO
        grupo_radio.check(R.id.radio_dif)
        eventoBotonGrabar()


        btn_guardar.setOnClickListener{

            guardarObjetoEnBD()
        }

    }


    fun guardarObjetoEnBD()
    {
        val id=CRUDEntradas.nuevoId()
        val palabraIngles=field_word.text.toString()
        val traduccion=field_traduccion.text.toString()
        val descripcion=field_descri.text.toString()
        val probabilidad=obtenerProbabilidad()
        val entradaNueva=Entrada(id!! ,traduccion, descripcion, probabilidad, palabraIngles,rutaAudio )

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
        rutaAudio=""
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
    //ADEMÁS AÑADIMOS UNA ANIMACIÓN EN ESCALA PRA QUE MIENTRAS ESTE GRABANDO EL MICRÓFONO ESTE ANIMADO
    fun eventoBotonGrabar()
    {


        btn_grabar.setOnTouchListener { v, event ->



            when (event.action) {
                MotionEvent.ACTION_DOWN -> {

                    grabacion=MediaRecorder()
                    v.setBackgroundResource(R.drawable.ic_action_grabando)
                    var animacionMicro= AnimationUtils.loadAnimation(this, R.anim.anim_micro)
                    v.startAnimation(animacionMicro)

                    grabacion?.let { grabandoAudio(it) }
                    tiempoDuracionAudio()

                    return@setOnTouchListener true
                }
                MotionEvent.ACTION_UP -> {
                    v.setBackgroundResource(R.drawable.ic_action_grabar)
                    v.clearAnimation()
                    Toast.makeText(this,"Grabación terminada", Toast.LENGTH_SHORT).show()

                    grabacion?.let { stopAudio(it) }
                    return@setOnTouchListener true
                }
                else-> false
            }
        }
    }

    //ESTA FUNCIÓN HARÁ QUE A LOS 20 SEGUNDOS EL BOTÓN VUELVA A SU ESTADO ORIGINAL AUNQUE SE MANTENGA PULSADO
    fun tiempoDuracionAudio()
    {
        var handler = Handler()
        handler.postDelayed( {

            btn_grabar.setBackgroundResource(R.drawable.ic_action_grabar)
            btn_grabar.clearAnimation()
            Toast.makeText(this,"Grabación detendida", Toast.LENGTH_SHORT).show()


        }, 20000);
    }

    fun grabandoAudio(grabacion:MediaRecorder)
    {
        //DEFINIMOS EL FICHERO DONDE SE GUARDARA EL AUDIO, DENTRO DE LA CARPETA DONDE ESTÁ NUESTA COPIA DE SEGURIDAD DE LA BASE DE DATOS
        //CON EL ID DEL OBJETO QUE EN PRINCIPIO SE GUARDARÍA EN LA BASE DE DATOS
        val ficheroAlmacenamientoAudio=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).absolutePath+CRUDEntradas.nuevoId()+".mp3"
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
        }catch(e: IOException)
        {
            Log.e("ERROR AUDIO", "Ha ocurrido un error al grabar el audio")
        }

        Toast.makeText(this,"Mantén pulsado para grabar durante un máx de 20 segundos", Toast.LENGTH_SHORT).show()
    }

    fun stopAudio(grabacion:MediaRecorder)
    {

        //PARAMOS GRABACIÓN
        grabacion.stop()
        //LIBERAMOS RECURSOS
        grabacion.release()
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
