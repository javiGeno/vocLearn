package morajavier.pdm.voclearn


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import morajavier.pdm.voclearn.BaseDatos.CRUDConjuntos
import morajavier.pdm.voclearn.BaseDatos.CRUDEntradas
import morajavier.pdm.voclearn.BaseDatos.CRUDGrupo
import morajavier.pdm.voclearn.Modelo.Conjunto
import morajavier.pdm.voclearn.Modelo.Entrada
import morajavier.pdm.voclearn.Modelo.Grupo

class MainActivity : AppCompatActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme ( R.style.AppTheme )
        setContentView(R.layout.activity_main)

        //SE COMPRUEBAN LOS PERMISOS DE ALMACENAMIENTO EXTERNO
        SecurityCopy.comprobarPermisosAlmacenamiento(this)
        //PASAMOS EL CONTEXTO DEL MAINACTIVITY, NOS SERVIRA PARA COMPROBAR
        // LOS PERMISOS DE ESCRITURA Y LECTURA EXTERNA
        App.gestorBD.inyeccionContexto(this)
        //COMPROBAMOS Y ACTUALIZAMOS LA BD LOCAL
        App.gestorBD.actualizacionBD()
        //OBTENEMOS LA INSTANCIA DE LA BD
        App.gestorBD.crearInstanciaBD()


        Log.i("MAIN", "La app entra en on create")


    }

    override fun onStart() {
        super.onStart()

       /* val e1=CRUDEntradas.nuevoId()?.let { Entrada(it, "casa","", "", 1, "home", "") }
        e1?.let { CRUDEntradas?.nuevaOActualizarEntrada(it) }
        val e2= CRUDEntradas.nuevoId()?.let { Entrada(it, "hacer","", "", 1, "make", "") }
        e2?.let { CRUDEntradas.nuevaOActualizarEntrada(it) }
        val e3= CRUDEntradas.nuevoId()?.let { Entrada(it, "libre","", "", 1, "free", "") }
        e3?.let { CRUDEntradas.nuevaOActualizarEntrada(it) }
        val e4= CRUDEntradas.nuevoId()?.let { Entrada(it, "balon","", "", 1, "ball", "") }
        e4?.let { CRUDEntradas.nuevaOActualizarEntrada(it) }
        val e5= CRUDEntradas.nuevoId()?.let { Entrada(it, "tambien","", "", 1, "too", "") }
        e5?.let { CRUDEntradas.nuevaOActualizarEntrada(it) }



        CRUDConjuntos.nuevoId()?.let{Conjunto(it, "Sustantivos")}?.let {
            CRUDConjuntos.nuevoOActualizaConjunto(
                it
            )
        }
        CRUDConjuntos.nuevoId()?.let{Conjunto(it, "Verbos")}?.let {
            CRUDConjuntos.nuevoOActualizaConjunto(
                it
            )
        }
        CRUDConjuntos.nuevoId()?.let{Conjunto(it, "Preposiciones")}?.let {
            CRUDConjuntos.nuevoOActualizaConjunto(
                it
            )
        }

        CRUDGrupo.nuevoOActualizaGrupo(Grupo("DIFICILES"))
        CRUDGrupo.nuevoOActualizaGrupo(Grupo("FACILES"))



        var lista=CRUDEntradas.obtenerTodasEntradas()

        lista?.let{CRUDEntradas.recorrerListaEntrada(it)}*/

        CRUDEntradas.borrarEntradaId(1)
        CRUDConjuntos.borrarConjuntoId(1)

        /*
        CRUDGrupo.insertarEntradaEnGrupo("DIFICILES",0)
        CRUDGrupo.insertarEntradaEnGrupo("FACILES",1)
        CRUDGrupo.insertarEntradaEnGrupo("FACILES",2)
        CRUDGrupo.insertarEntradaEnGrupo("DIFICILES",3)
        CRUDGrupo.insertarEntradaEnGrupo("DIFICILES",4)
        CRUDGrupo.insertarEntradaEnGrupo("DIFICILES",2)

        CRUDGrupo.insertarConjuntoEnGrupo("DIFICILES", 0)
        CRUDGrupo.insertarConjuntoEnGrupo("FACILES", 1)
        CRUDGrupo.insertarConjuntoEnGrupo("FACILES", 2)

        CRUDConjuntos.insertarEntradaEnConjunto(1, 1)
        CRUDConjuntos.insertarEntradaEnConjunto(0, 0)
        CRUDConjuntos.insertarEntradaEnConjunto(0, 2)*/

        CRUDGrupo.recorrerListaGrupo(CRUDGrupo.obtenerTodosLosGrupos())

        Log.w("PALABRAS TOTAL", "Palabras registradas")
        CRUDEntradas.recorrerListaEntrada(CRUDEntradas.obtenerTodasEntradas())



        Log.w("MAIN", "La app entra en on Start")
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
            SecurityCopy.hacerCopiaSeguridad(App.gestorBD.r, this)
        }





}
