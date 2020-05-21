package morajavier.pdm.voclearn

import android.app.Activity
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add.*
import morajavier.pdm.voclearn.BaseDatos.CRUDEntradas
import java.io.File
import java.io.IOException
import java.lang.RuntimeException

class Sonido(var actividad: Activity, var idAudio:Int, var grabacion: MediaRecorder= MediaRecorder(), var rutaAudio:String="" ) {

    //VARIABLE QUE UTILIZAREMOS PARA EL CONTROL DE LOS 20 SEGUNDOS DE DURACIÓN DEL AUDIO
    var handler = Handler()
    //CARPETA AUDIO
    val carpetaAudios="/AudiosVocLearn"

    //EN ESTE MÉTODO COMPROBAMOS SI EL BOTON DE GRABAR ESTA PULSADO O NO PARA GRABAR O DEJAR DE GRABAR
    //ADEMÁS AÑADIMOS UNA ANIMACIÓN EN ESCALA PARA QUE MIENTRAS ESTE GRABANDO EL MICRÓFONO ESTE ANIMADO
    fun grabadora(v: View, event:MotionEvent):Boolean
    {

            println("ACTION BOTON"+ event.action)
            if (event.action == MotionEvent.ACTION_DOWN ) {
                grabacion = MediaRecorder()
                v.setBackgroundResource(R.drawable.ic_action_grabando)
                var animacionMicro = AnimationUtils.loadAnimation(actividad, R.anim.anim_micro)
                v.startAnimation(animacionMicro)

                grabacion?.let { grabandoAudio(it) }

                //FUERCE A AVISAR AL USUARIO QUE LA GRABACIÓN SE HA DETENIDO. LA GRABACIÓN ESTA CONFIGURADA A 20 SEG MAX
                tiempoDuracionAudio(handler)

                return  true

            } else if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL ) {
                v.setBackgroundResource(R.drawable.ic_action_grabar)
                v.clearAnimation()
                grabacion?.let { stopAudio(it) }

                //CANCELAMOS CUALQUIER POSTDELAY QUE ESTÉ A LA ESPERA, LLAMADO EN EL METODO tiempoDuracion ANTERIOR
                handler.removeCallbacksAndMessages(null)

                return true
            }
            else
            {
                return false
            }



    }



    //ESTA FUNCIÓN HARÁ QUE A LOS 20 SEGUNDOS EL BOTÓN VUELVA A SU ESTADO ORIGINAL AUNQUE SE MANTENGA PULSADO
    fun tiempoDuracionAudio(handler: Handler)
    {

        handler.postDelayed( {

            //SI PASA EL TIEMPO DE GRABACIÓN Y AÚN SIGUE GRABANDO, SE CAMBIA EL BOTÓN Y SE NOTIFICA AL USUARIO
            actividad.btn_grabar.setBackgroundResource(R.drawable.ic_action_grabar)
            actividad.btn_grabar.clearAnimation()
            Toast.makeText(actividad, R.string.grabDetenida, Toast.LENGTH_SHORT).show()

        }, 20000)

    }

    fun creacionFicheroAudio (): File
    {
        //CREAMOS UN DIRECTORIO PARA GUARDAR LOS AUDIOS
        val dirAlmacenamientoAudio=
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), carpetaAudios)
        //SI NO ESTA CREADO SE CREA
        if (!dirAlmacenamientoAudio.exists())
            dirAlmacenamientoAudio.mkdirs()

        //CREAMOS EL FICHERO DONDE SE ALMACENARÁ EL AUDIO
        val ficheroImg="AUD"+idAudio+".mp3"
        var ficheroAlmacenamientoAudio= File(dirAlmacenamientoAudio,ficheroImg)

        return ficheroAlmacenamientoAudio
    }

    fun grabandoAudio(grabacion:MediaRecorder)
    {
        //DEFINIMOS EL FICHERO DONDE SE GUARDARA EL AUDIO, DENTRO DE LA CARPETA DONDE ESTÁ NUESTA COPIA DE SEGURIDAD DE LA BASE DE DATOS
        //CON EL ID DEL OBJETO QUE EN PRINCIPIO SE GUARDARÍA EN LA BASE DE DATOS
        val ficheroAlmacenamientoAudio=creacionFicheroAudio().absolutePath
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

            Toast.makeText(actividad,R.string.grabAviso, Toast.LENGTH_SHORT).show()

        }catch(e: IOException)
        {
            rutaAudio=""
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

            Toast.makeText(actividad,R.string.grabTerminada, Toast.LENGTH_SHORT).show()

        }
        catch (e: RuntimeException)
        {
            Toast.makeText(actividad, R.string.grabFallo, Toast.LENGTH_SHORT).show()
            //NO GUARDAMOS NADA EN LA RUTA PORQUE HA FALLADO AL CREARSE
            rutaAudio=""
            e.printStackTrace()
        }
    }

    //-----------------------------------PARA OIR EL AUDIO------------------------------------------
    companion object {

        fun preparacionAudio(reproducirAudio: MediaPlayer, idEntrada: Int) {

            try {
                reproducirAudio.setDataSource(
                    Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_MUSIC
                    ).absolutePath + "/AudiosVocLearn/AUD" + idEntrada + ".mp3"
                )

                //PREPARAMOS EL AUDIO
                reproducirAudio.prepare()

            } catch (e: IOException) {
                Log.e("ERROR AUDIO", "Ha ocurrido un error al reproducir el audio")

            }

        }
    }


}