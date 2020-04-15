package morajavier.pdm.voclearn


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import morajavier.pdm.voclearn.BaseDatos.ManejadorBD
import morajavier.pdm.voclearn.Modelo.Entrada

class MainActivity : AppCompatActivity() {

    var gestorBD: ManejadorBD?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        gestorBD= ManejadorBD(this)


        Log.i("MAIN", "La app entra en on create")


        val e1= Entrada(7)
        val e2= Entrada(8)
        val e3= Entrada(9)
        val e4= Entrada(10)
        val e5= Entrada(11)


        gestorBD?.nuevaOActualizarEntrada(e1)
        gestorBD?.nuevaOActualizarEntrada(e2)
        gestorBD?.nuevaOActualizarEntrada(e3)
        gestorBD?.nuevaOActualizarEntrada(e4)
        gestorBD?.nuevaOActualizarEntrada(e5)

        var lista=gestorBD?.obtenerTodasEntradas()

        Log.i("COUNT LISTA", ""+lista?.count())

        for(i in lista!!){
            Log.i(""+i.idEntrada, "" +
                    ""+i.descripcion+" "+i.significado)
        }

        gestorBD?.borrarEntradaId(lista.get(0).idEntrada)

        lista=gestorBD?.obtenerTodasEntradas()
        Log.i("COUNT LISTA", ""+lista?.let{it.count()})

        for(i in lista!!){
            Log.i(""+i.idEntrada, "" +
                    ""+i.descripcion+" "+i.significado)
        }
        gestorBD?.let{it.borrarTodasEntradas()}







    }

    /*fun actualizacionBD() {
        //SI HAY BASE DE DATOS LOCAL, QUIERE DECIR QUE NO SE HA DESINSTALADO LA APP
        //POR TANTO ACCEDEMOS A LOS DATOS LOCALES
        if (SecurityCopy.hayBDLocal(this)) {
            Realm.init(this)

            val config = RealmConfiguration.Builder()
                .name("bdLocal.realm")
                .build()

            Realm.setDefaultConfiguration(config)
            r = Realm.getDefaultInstance()

            Log.w("MAIN", "Hay bd en local")
        } else {
            //SI NO HAY BD LOCAL, PERO SI EXTERNA
            //LA RESTAURAMOS EN LOCAL
            //SI NO HAY CONSTANCIA DE NINGUNA DE LAS DOS, CREAMOS LA BD NUEVA
            if (SecurityCopy.hayBDExterna(this)) {
                SecurityCopy.restaurarCopiaSeguridad(this)

                Realm.init(this)

                val config = RealmConfiguration.Builder()
                    .name("bdLocal.realm")
                    .build()

                Realm.setDefaultConfiguration(config)
                r = Realm.getDefaultInstance()

                Log.w("MAIN", "Se ha restaurado")

            } else {
                Realm.init(this)

                val config = RealmConfiguration.Builder()
                    .name("bdLocal.realm")
                    .build()

                Realm.setDefaultConfiguration(config)
                r = Realm.getDefaultInstance()

                Log.w("MAIN", "No hay bd en local, se ha creado una nueva BD")
            }
        }
    }

        override fun onResume() {
        super.onResume()
        Log.w("MAIN", "La app entra en on resume")
    }

    override fun onStart() {
        super.onStart()
        Log.w("MAIN", "La app entra en on Start")
    }


    override fun onRestart() {
        super.onRestart()
        Log.w("MAIN", "La app entra en on restart")
    }

    override fun onStop() {
        super.onStop()
        Log.w("MAIN", "La app entra en on stop")
    }
    */
        override fun onDestroy() {
            super.onDestroy()

            //CREAMOS UNA COPIA DE SEGURIDAD CADA VEZ QUE EL USUARIO SALGA DE LA APP
            SecurityCopy.hacerCopiaSeguridad(gestorBD?.r, this)
        }





}
