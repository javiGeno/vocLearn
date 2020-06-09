package morajavier.pdm.voclearn.Modelo

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View

class AnimaWidget {

    companion object{

        const val MEDIOSEGUNDO =500
        const val UNSEGUNDO =1000
        const val DOSSEGUNDO =2000
        const val TRESSEGUNDO =3000
        const val CUATROSEGUNDO =4000


        fun transiccion(id:View, desde:Float, hasta:Float, tiempo:Long)
        {
            var animatorAlpha = ObjectAnimator.ofFloat(id, View.ALPHA,desde, hasta)
            animatorAlpha.setDuration(tiempo)
            var animatorSetAlpha =  AnimatorSet()
            animatorSetAlpha.playTogether(animatorAlpha)
            animatorSetAlpha.start()
        }

        fun rotacion(id:View, desde:Float, hasta:Float, tiempo:Long)
        {
            var animatorRotation = ObjectAnimator.ofFloat(id, "rotation", desde, hasta);
            animatorRotation.setDuration(tiempo)
            var animatorSetRotator = AnimatorSet()
            animatorSetRotator.playTogether(animatorRotation)
            animatorSetRotator.start()
        }
    }
}