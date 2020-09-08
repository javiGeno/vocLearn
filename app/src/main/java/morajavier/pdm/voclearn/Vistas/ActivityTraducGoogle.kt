package morajavier.pdm.voclearn.Vistas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_traduc_google.*
import kotlinx.android.synthetic.main.barra_guardar_atras.*
import morajavier.pdm.voclearn.R

class ActivityTraducGoogle : AppCompatActivity() {

   var palabra=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_traduc_google)

        palabra=intent.getStringExtra("palabraTraducir")

        val wS = googleTraduccion.getSettings()
        wS.setJavaScriptEnabled(true)

        googleTraduccion.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null)

        googleTraduccion.setWebViewClient(WebViewClient())

        btn_atras.setOnClickListener{
            finish()
        }
    }

    public override fun onResume() {
        super.onResume()


        googleTraduccion.loadUrl("https:translate.google.com/?hl=es#view=home&op=translate&sl=en&tl=es&text="+palabra)


    }

    override fun onBackPressed() {

        //sirve para que cuando le de atrás en el dispositivo compruebe si en la web hay más páginas
        //en caso de que haya se dirige a ella, por el contrario sale de la actividad

        if (googleTraduccion.canGoBack()) {
            googleTraduccion.goBack()
        } else {
            super.onBackPressed()
            finish()
        }
    }
}
