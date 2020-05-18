package morajavier.pdm.voclearn

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Environment
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.realm.Realm
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.res.TypedArrayUtils.getString
import androidx.core.content.res.TypedArrayUtils.getText
import morajavier.pdm.voclearn.BaseDatos.CRUDEntradas
import morajavier.pdm.voclearn.Modelo.Entrada
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException


class SecurityCopy  {


    //LA CLASE LO QUE HACE ES DAR PERMISOS DE ALMACENAMIENTO EXTERNO
    //Y CREAR UN DIRECTORIO, DONDE ALMACENAREMOS UNA COPIA DE SEGURIDAD DE LA BASE DE DATOS
    //Y LOS AUDIOS QUE GUARDARÁ EL USUARIO

    companion object {
        //CARPETA EXTERNA DONDE SE ALMACENARÁN ARCHIVOS DE SEGURIDAD DE LA APP, BASE DE DATOS, AUDIOS.
        val PATH_TO_WRITE = File( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),"VocLearSecurity")
        //CARPETA IMAGENES CREADA PARA LA APP
        val FOLDER_IMAGES = File( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"ImagenesVocLearn")
        //STRING PARA LLEGAR A LA UBICACIÓN DE LOS AUDIOS DE LA APP
        val FOLDER_MUSIC =  File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "AudiosVocLearn")
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
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
        )
        //BANDERA QUE INDICA QUE TODOS LOS PERMISOS FUERON CONCEDIDOS(FOTOS, MICRO, COPIA SEGURIDAD)
        var perAceptados=false



        fun hacerCopiaSeguridad(real :Realm?, act:Activity) {

            // PRIMERO COMPRUEBA LOS PERMISOS DE ALMACENAMIENTO
            comprobarPermisoAlmacenamiento(act)


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

                //COPIA LA ACTUAL BASE DE DATOS
                val rutaCopiada = volcarFichero(ficheroDestino, ficheroOrigen)
                val msg = "Fichero almacenado en la ruta: " + rutaCopiada
                Log.d(TAG, msg)


            } catch (e: IOException) {
                e.printStackTrace()
            }


        }

        fun restaurarCopiaSeguridad(act:Activity) {

            comprobarPermisoAlmacenamiento(act)

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
                flujoSalida.close()

                //DEVUELVE LA RUTA DONDE SE HA RESTAURADO EL ARCHIVO BD
                return ficheroSalida.getAbsolutePath();

            } catch (e: IOException) {
                e.printStackTrace()
            }
            return "";
        }

        fun comprobarPermisoAlmacenamiento(act:Activity)
        {
            // comprueba si se han escrito los permisos
            val permisosEscritura = ActivityCompat.checkSelfPermission(act, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val permisosLectura = ActivityCompat.checkSelfPermission(act, Manifest.permission.READ_EXTERNAL_STORAGE)



            if ((permisosEscritura != PackageManager.PERMISSION_GRANTED && permisosLectura != PackageManager.PERMISSION_GRANTED)) {

                permisosSiNo(act,  act.getString(R.string.permisos), PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE)

                Log.e("PERMISOS", "permisos asignados en tiempo de ejecucion ")


            } else {
                Log.e("PERMISOS", "los permisos de almacenamiento externo fueron concedidos")


            }
        }

        fun comprobarTodosPermisos(act:Activity) {

            // comprueba si se han escrito los permisos
            val permisosEscritura = ActivityCompat.checkSelfPermission(act, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val permisosLectura = ActivityCompat.checkSelfPermission(act, Manifest.permission.READ_EXTERNAL_STORAGE)
            val permisosAudio = ActivityCompat.checkSelfPermission(act, Manifest.permission.RECORD_AUDIO)


            if ((permisosEscritura != PackageManager.PERMISSION_GRANTED
                && permisosLectura != PackageManager.PERMISSION_GRANTED)
                || permisosAudio != PackageManager.PERMISSION_GRANTED) {

                permisosSiNo(act,  act.getString(R.string.permisos), PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE)

                Log.e("PERMISOS", "permisos asignados en tiempo de ejecucion para escribir")


            } else {
                Log.e("PERMISOS", "Todos los permisos fueron concedidos")
                perAceptados=true

            }
        }


        fun permisosSiNo(act: Activity, explicacion:String, permisos:Array<String>, codigoPermiso:Int) {

            //IF UNO DE LOS PERMISOS DEL CONJUNTO DE PERMISOS PEDIDOS AL USUARIO, FUE DENEGADO SE LE DA UNA EXPLICACIÓN
            //ANTES DE LA SOLICITUD DE PERMISOS
            //EN CASO CONTRARIO SE LE MUESTRA LA SOLICITUD DE PERMISOS DIRECTAMENTE
            if (shouldShowRequestPermissionRationale(act, permisos[0])) {
                //LECTURA ESCRITURA
                AlertDialog.Builder(act)
                    .setTitle(R.string.necesidadPermisos)
                    .setMessage(explicacion)
                    .setPositiveButton(
                        R.string.aceptar,
                        { dialogInterface: DialogInterface, i: Int ->

                            ActivityCompat.requestPermissions(act, permisos, codigoPermiso)

                        })
                    .setNegativeButton(R.string.cancelar,
                        { dialogInterface: DialogInterface, i: Int ->

                            dialogInterface.dismiss()

                        })
                    .create().show()
            }
            else if(shouldShowRequestPermissionRationale(act, permisos[2])){
                    //MICROFONO
                AlertDialog.Builder(act)
                    .setTitle(R.string.necesidadPermisos)
                    .setMessage(act.getString(R.string.permisosMicro))
                    .setPositiveButton(
                        R.string.aceptar,
                        { dialogInterface: DialogInterface, i: Int ->

                            ActivityCompat.requestPermissions(act, permisos, codigoPermiso)

                        })
                    .setNegativeButton(R.string.cancelar,
                        { dialogInterface: DialogInterface, i: Int ->

                            dialogInterface.dismiss()

                        })
                    .create().show()
            } else {
                ActivityCompat.requestPermissions(act, permisos, codigoPermiso)
            }


        }


        fun hayBDLocal (act:Activity) : Boolean
        {
            return File(act.getApplicationContext().getFilesDir(), FILE_TO_RECOVERY).exists()
        }

        fun hayBDExterna () : Boolean
        {
            return File(PATH_TO_WRITE, FILE_TO_WRITE).exists()
        }

        //MÉTODO PARA BORRAR ARCHIVOS DE IMÁGENES Y AUDIO SI NINGÚN OBJETO DE LA LISTA APUNTA A ELLOS
        fun limpiezaDisco()
        {
            var listaAudios : Array<File>?= null
            var listaImagenes: Array<File>?= null

            if(FOLDER_IMAGES.exists() )
                 listaImagenes =getListaFicheros(FOLDER_IMAGES)

            if(FOLDER_MUSIC.exists())
                listaAudios =getListaFicheros(FOLDER_MUSIC)

            var entrada: Entrada?
            var idExtraido:Int?

            //RECCORREMOS LAS LISTAS DE FICHEROS DE AUDIOS E IMAGENES,
            //DE CADA NOMBRE DE FICHERO, EXTRAEMOS OS NUMEROS, QUE ES LO QUE NOS HARÁ FALTA PARA
            //BUSCAR EN LA BASE DE DATOS POR EL ID (CADA NOMBRE DEL FICHERO TIENE UN NUMERO QUE LO RELACIONA CON SU ENTRADA)
            //EN EL CADO DE QUE LA BUSQUEDA DEVUELVA NULO, QUIERE DECIR QUE NO HAY ENTRADA NINGUNA QUE APUNTE A ESE
            //FICHERO. POR TANTO BORRAMOS EL FICHERO DEL DISCO
            if(listaAudios!=null) {

                for (n in 0 until listaAudios.size) {

                    //EXTRAEMOS EL NOMBRE DEL FICHERO
                    var nombreFichero=listaAudios[n].name
                    //LE QUITAMOS  EL ÚLTIMO 3  DE LA EXTENSIÓN "mp3"
                    var nombreSinExtension=nombreFichero.substring(0, (nombreFichero.length-1))
                    //CON ESE NOMBRE EXTRAEMOS EL NUMERO QUE CORRESPONDE CON EL ID
                    idExtraido=nombreSinExtension.filter{it.isDigit() }.toInt()
                    entrada=CRUDEntradas.obtenerEntradaPorId(idExtraido)

                    entrada?.let{}?: listaAudios[n].delete()
                }
            }

            if(listaImagenes!=null) {

                for (n in 0 until listaImagenes.size) {

                    idExtraido=listaImagenes[n].name.filter { it.isDigit() }.toInt()
                    entrada=CRUDEntradas.obtenerEntradaPorId(idExtraido)

                    entrada?.let{}?: listaImagenes[n].delete()
                }
            }

        }

        //OBTIENE LA LISTA DE FICHEROS DE UNA CARPETA PASADA POR PARÁMETROS
        fun getListaFicheros(carpeta:File):Array<File>?
        {
            var listaFicheros:Array<File>?=null

            if(carpeta.exists()) {
                listaFicheros = carpeta.listFiles()


            }

            return listaFicheros
        }


    }
}