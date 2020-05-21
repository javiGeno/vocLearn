package morajavier.pdm.voclearn

import android.app.Application
import morajavier.pdm.voclearn.BaseDatos.ManejadorBD

class App: Application() {

    //USAMOS ESTA CLASE PARA INICIALIZAR UNA VEZ LA BD
    companion object
    {
        lateinit var gestorBD: ManejadorBD
    }
    override fun onCreate() {
        super.onCreate()
        //INSTANCIAMOS LA CLASE QUE SE ENCARGA DE MANEJAR LA BD
        gestorBD=ManejadorBD(this)

    }
}