package morajavier.pdm.voclearn.FuncionesExtension

import android.media.MediaPlayer
import android.view.View

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