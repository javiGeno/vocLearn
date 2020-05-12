package morajavier.pdm.voclearn.FuncionesExtension

import android.media.MediaPlayer
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.google.android.material.circularreveal.cardview.CircularRevealCardView
import com.squareup.picasso.Picasso
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

fun ImageView.cargarImagen(url:String)
{
    url?.let{

        Glide.with(context)
            .load(File(url))
            .bitmapTransform(CropCircleTransformation(context))//CARGAR IMÁGENES REDONDAS
            .into( this)
    }
}

fun ImageView.cargarImagenCicular(url:String)
{
    url?.let{

        Picasso.with(this.context)
            .load(url)
            .into(this)
    }
}