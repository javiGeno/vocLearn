package morajavier.pdm.voclearn

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Environment
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.realm.Realm
import android.widget.Toast
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException


class SecurityCopy(actividad:Activity, clase:MainActivity) {

    //LA CLASE LO QUE HACE ES DAR PERMISOS DE ALMACENAMIENTO EXTERNO
    //Y CREAR UN DIRECTORIO, DONDE ALMACENAREMOS UNA COPIA DE SEGURIDAD DE LA BASE DE DATOS
    //Y LOS AUDIOS QUE GUARDARÁ EL USUARIO

    companion object {
        //CARPETA EXTERNA DONDE SE ALMACENARÁN ARCHIVOS DE SEGURIDAD DE LA APP, BASE DE DATOS, AUDIOS.
        val PATH_TO_WRITE = File( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),"VocLearSecurity")
        //NOMBRE QUE TENDRA EL FICHERO EXTERNO DE LA BD AL GUARDAR
        val FILE_TO_WRITE = "bdCopiaSeguridad.realm"
        //NOMBRE QUE TENDRA EL FICHERO INTERNO DE LA BD AL RESTAURAR
        val FILE_TO_RECOVERY = "bdLocal.realm"
        //ETIQUETA DE CONTROL PARA EL LOG
        val TAG = "COPIA/RESTAURACION"

        //PERMISOS DE ALMACENAMIENTO
        val REQUEST_EXTERNAL_STORAGE = 1
        val PERMISSIONS_STORAGE = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )


        fun hacerCopiaSeguridad(real :Realm?, act:Activity) {

            // PRIMERO COMPRUEBA LOS PERMISOS DE ALMACENAMIENTO
            comprobarPermisosAlmacenamiento(act)


            //FICHERO DONDE SE REALIZARÁ LA COPIA DE SEGURIDAD
            val exportRealmFile: File

            Log.d(TAG, "DIR LOCAL BASE DATOS = " + real?.getPath())

            try {
                //CARPETA EXTERNA PARA DATOS
                if (!PATH_TO_WRITE.exists())
                    PATH_TO_WRITE.mkdirs()

                //CREAMOS EL FICHERO DE LA BASE DE DATOS
                exportRealmFile = File(PATH_TO_WRITE, FILE_TO_WRITE)

                //SI EL FICHERO YA EXISTE LO BORRA
                exportRealmFile.delete()

                //COPIA LA ACTUAL BASE DE DATOS EN EL FICHERO CREADO EN ALMACENAMIENTO EXTERNO (CERRAMOS ANTES LA INSTANCIA REALM)
                real?.close()
                //FICHERO REALM EXTERNO (COPIA SEGURIDAD)
                val ficheroDestino = File(PATH_TO_WRITE, FILE_TO_WRITE)
                //FICHERO REALM INTERNO (LOCAL)
                val ficheroOrigen =
                    File(act.getApplicationContext().getFilesDir(), FILE_TO_RECOVERY)

                val rutaCopiada = volcarFichero(ficheroDestino, ficheroOrigen)
                val msg = "Fichero almacenado en la ruta: " + rutaCopiada
                Log.d(TAG, msg)


            } catch (e: IOException) {
                e.printStackTrace()
            }


        }

        fun restaurarCopiaSeguridad(act:Activity) {
            comprobarPermisosAlmacenamiento(act);

            //RUTA PARA OBTENER LA RESTAURACION
            val restoreFilePath = PATH_TO_WRITE.path + "/" + FILE_TO_WRITE
            Log.d(TAG, "LOCALIZACION FICHERO EXTERNO = " + restoreFilePath);

            //FICHERO REALM INTERNO (LOCAL)
            val ficheroDestino = File(act.getApplicationContext().getFilesDir(), FILE_TO_RECOVERY)
            //FICHERO REALM EXTERNO (COPIA SEGURIDAD)
            val ficheroOrigen = File(PATH_TO_WRITE, FILE_TO_WRITE)

            val rutaRestaurada = volcarFichero(ficheroDestino, ficheroOrigen)
            val msg = "Base de datos restaurada= " + rutaRestaurada
            Log.d(TAG, msg);
        }

        fun volcarFichero(ficheroSalida: File, ficheroEntrada: File): String {
            try {

                //UBICACION DONDE COPIAREMOS
                var flujoSalida = FileOutputStream(ficheroSalida)
                //UBICACION DESDE DONDE COPIAREMOS
                var flujoEntrada = FileInputStream(ficheroEntrada)


                var buf = ByteArray(1024)
                var bytesRead: Int

                bytesRead = flujoEntrada.read(buf)
                while (bytesRead > 0) {

                    flujoSalida.write(buf, 0, bytesRead)

                    bytesRead = flujoEntrada.read(buf)
                }
                flujoSalida.close();

                //DEVUELVE LA RUTA DONDE SE HA RESTAURADO EL ARCHIVO BD
                return ficheroSalida.getAbsolutePath();

            } catch (e: IOException) {
                e.printStackTrace();
            }
            return "";
        }


        fun comprobarPermisosAlmacenamiento(act:Activity) {

            // comprueba si se han escrito los permisos
            val permisos =
                ActivityCompat.checkSelfPermission(act, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (permisos != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    act,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
                )

                Log.e("PERMISOS", "permisos asignados en tiempo de ejecucion para escribir")


            } else {
                Log.e("PERMISOS", "los permisos de escritura ya fueron concedidos")

            }
        }

        fun hayBDLocal (act:Activity) : Boolean
        {
            return File(act.getApplicationContext().getFilesDir(), FILE_TO_RECOVERY).exists()
        }

        fun hayBDExterna (act:Activity) : Boolean
        {
            return File(PATH_TO_WRITE, FILE_TO_WRITE).exists()
        }

    }
}