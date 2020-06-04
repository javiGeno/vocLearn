package morajavier.pdm.voclearn.FuncionesExtension

import android.app.Activity
import android.media.MediaPlayer
import android.view.View
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.StringSignature
import jp.wasabeef.glide.transformations.CropCircleTransformation
import morajavier.pdm.voclearn.Modelo.Entrada
import morajavier.pdm.voclearn.R
import java.io.File

fun View.cambioImagen(media: MediaPlayer){


        if(media.isPlaying) {
            media.pause()
            this.setBackgroundResource(android.R.drawable.ic_media_play)

        }
        else
        {
            media.start()
            this.setBackgroundResource(android.R.drawable.ic_media_pause)

        }
}

fun ImageView.cargarImagenCircle(url:String)
{
    url?.let{

        Glide.with(context)
            .load(File(url))
            .bitmapTransform(CropCircleTransformation(context))//CARGAR IMÁGENES REDONDAS
            .signature(StringSignature(System.currentTimeMillis().toString()))
            .into( this)
    }
}

fun ImageView.cargarImagenCircleNoCache(url:String, spinner: CircularProgressDrawable,  imgError:Int)
{


    url?.let{
        Glide.with(context)
            .load(File(url))
            .bitmapTransform(CropCircleTransformation(context))//CARGAR IMÁGENES REDONDAS
            .diskCacheStrategy(DiskCacheStrategy.NONE)//MANEJO DE LA MEMORIA CACHE
            .skipMemoryCache(true)//OMISIÓN CACHÉ
            .placeholder(spinner)
            .error(imgError)
            .into( this)
    }
}

fun ImageView.cargarImagen(url:String)
{



    url?.let{

        Glide.with(context)
            .load(File(url))
            .signature(StringSignature(System.currentTimeMillis().toString()))
            .centerCrop()
            .into( this)
    }
}

//FUNCION DE EXTENSION PARA UN ACTIVITY QUE CREA UN SPINNER DE CARGA DEL TAMAÑO QUE LE PASEMOS POR PARÁMETRO
fun Activity.crearSpinnerCarga(recuadroSpinner:Float, radioSpinner:Float):CircularProgressDrawable{

    val circularProgressDrawable = CircularProgressDrawable(this)
    circularProgressDrawable.strokeWidth = recuadroSpinner
    circularProgressDrawable.centerRadius = radioSpinner
    circularProgressDrawable.start()

    return circularProgressDrawable
}

fun ImageView.cargarNotCache(url:String, spinner: CircularProgressDrawable, imgError: Int)
{


    url?.let{

        Glide.with(context)
            .load(File(url))
            .diskCacheStrategy(DiskCacheStrategy.NONE)//MANEJO DE LA MEMORIA CACHE
            .skipMemoryCache(true)//OMISIÓN CACHÉ
            .centerCrop()
            .placeholder(spinner)
            .error(imgError)
            .into( this)
    }
}

//FUNCION DE EXTENSION QUE DEVUELVE EL RECURSO DRAWABLE PARA PONER EN CASO DE DAR ERROR AL CARGAR LA IMAGEN QUE LE CORRESPONDER
fun Entrada.fondoImg():Int{


    when (this.probAcierto) {
        1 -> {

            return R.drawable.circulo_dif1
        }
        2 -> {
            return R.drawable.circulo_dif2
        }
        3 -> {
            return R.drawable.circulo_dif3
        }
        else -> {
            return R.drawable.circulo_dif1
        }

    }
}
