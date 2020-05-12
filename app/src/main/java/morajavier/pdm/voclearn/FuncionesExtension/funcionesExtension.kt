package morajavier.pdm.voclearn.FuncionesExtension

import android.media.MediaPlayer
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import jp.wasabeef.glide.transformations.CropCircleTransformation
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
            .into( this)
    }
}

fun ImageView.cargarImagen(url:String)
{
    url?.let{

        Glide.with(context)
            .load(File(url))
            .centerCrop()
            .into( this)
    }
}
