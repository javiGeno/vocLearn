package morajavier.pdm.voclearn.Modelo

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import morajavier.pdm.voclearn.R
import morajavier.pdm.voclearn.Vistas.AddActivity
import java.io.File

class Imagen {

    companion object{


        //MÉTODO QUE ABRE UN CUADRO DE DIALOGO CON EL USUARIO, PARA LA ELECCIÓN DE LA FUENTE DE LA IMAGEN
        fun seleccionarFuenteImagen(act:Activity, ficheroImagen: File)
        {
            //ARRAY CON LAS TRES OPCIONES QUE LE PASAREMOS AL AlertDialog
            val opciones =arrayOf<CharSequence>(act.resources.getString(R.string.galeria),
                act.resources.getString(R.string.camara),
                act.resources.getString(R.string.cancelar))


            val ventanDialogo= AlertDialog.Builder(act)
            ventanDialogo.setTitle(act.resources.getString(R.string.opciones))
            ventanDialogo.setItems(opciones,{ dialogInterface: DialogInterface, i: Int ->

                //SI ELIGE LA OPCION DE GALERIA
                if(opciones[i].equals(act.getString(R.string.galeria)))
                {

                    abrirActivityGaleria(act)
                    println("GALERIA")
                }
                //SI ELIGE LA OPCION DE CÁMARA
                else if(opciones[i].equals(act.resources.getString(R.string.camara)))
                {
                    val pm=act.packageManager
                    //SI EL DISPOSITIVO TIENE CÁMARA, ACCEDEMOS A ELLA
                    if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
                    {

                        abrirActivityCamara(
                            act,
                            ficheroImagen!!
                        )

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



        fun abrirActivityCamara(act:Activity, ficheroAlmacenImagen: File)
        {

            //OBTENEMOS LA URI CON EL FILE PROVIDER CREADO
            var imageUri=FileProvider.getUriForFile(act, "com.example.android.fileprovider", ficheroAlmacenImagen)
            //IGUAL QUE CON LA OPCIÓN DE GALERIA, COMPROBAMOS SI EL INTENT NO ES NULO, SI NO LO ES
            //LANZAMOS EL startActivityForResult CON EL OBJETO INTENT
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(act.packageManager)?.also {
                    //PASAMOS POR EL putExtra EL FICHERO DONDE SE ALMACENARÁ LA CAPTURA DE LA FOTO
                    //INDICANDO CON EL MediaStore.EXTRA_OUTPUT QUE REALICE ESA ACCIÓN
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                    try {
                        act.startActivityForResult(takePictureIntent, AddActivity.RESPUESTA_CAMARA)
                    }
                    catch(e:Exception)
                    {
                        Toast.makeText(act, "Error al abrir la cámara:"+e.toString(), Toast.LENGTH_SHORT)
                            .show()
                        Log.e("Cámara ", e.toString())
                    }
                }
            }

        }

        fun abrirActivityGaleria(act:Activity)
        {
            //CREAMOS EL INTENT, Y USAMOS Intent.ACTION_PICK, PARA QUE MUESTRA UNA LISTA DE OBJETOS(IMAGENES) A SELECCIONAR
            //EL SEGUNDO PARÁMETRO NOS PERMITIRA ACCEDER EN LA RESPUESTA, A LA OPCION ELEGIDA
            //PARA LANZAR ESTA ACTIVIDAD ES NECESARIO HACERLO CON startActivityForResult(), POR QUE LA RESPUESTA ES SEGURA

            var intent= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

            //SETEAMOS SU TIPO
            intent.setType("image/")

            //LANZAMOS LA ACTIVIDAD(SI resolveActivity NO ES NULO, CON UN CREATE CHOOSER POR SI HAY MÁS DE UNA APP DE IMAGENES
            intent.resolveActivity(act.packageManager)?.let{
                act.startActivityForResult(
                    Intent.createChooser(intent,act.resources.getString(R.string.opciones) ),
                    AddActivity.RESPUESTA_GALERIA
                )
            }
        }


        fun creacionFicheroImagen(idImagen:Int):File
        {
            //CREAMOS UN DIRECTORIO PARA GUARDAR LA IMAGENES OBTENIDAS YA SEA CÁMARA O GALERIA
            val dirAlmacenamientoImagen=File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"/ImagenesVocLearn")
            //SI NO ESTA CREADO SE CREA
            if (!dirAlmacenamientoImagen.exists())
                dirAlmacenamientoImagen.mkdirs()

            //CREAMOS EL FICHERO DONDE SE ALMACENARÁ LA IMAGEN NUEVA
            val ficheroImg="IMG"+idImagen+".jpg"
            var ficheroAlmacenamientoImagen=File(dirAlmacenamientoImagen,ficheroImg)

            return ficheroAlmacenamientoImagen
        }

        //MÉTODO ENCONTRADO EN STACKOVERFLOW, QUE DEVUELVE UN STRING CON LA RUTA DE LA IMAGEN OBTENIDA POR LA GALERIA
        //PARA COPIARLA EN LA MEMORIA EXTERNA Y HACER USO DE ELLA PARA OBTENER LAS IMAGENES DE LA APP
        fun getPath(uri :Uri, act:Activity):String{
            var cursor: Cursor? =null
            try {
                var projection = arrayOf("_data")
                cursor = act.applicationContext.getContentResolver()
                    .query(uri, projection, null, null, null)
                var columnIndex = cursor!!.getColumnIndexOrThrow("_data")
                cursor.moveToFirst()
                return cursor.getString(columnIndex)
            }
            finally {
                cursor?.let{it.close()}
            }
        }

    }

}