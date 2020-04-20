package morajavier.pdm.voclearn


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import io.realm.RealmList
import morajavier.pdm.voclearn.BaseDatos.GestionEntradas
import morajavier.pdm.voclearn.BaseDatos.GestionGrupos
import morajavier.pdm.voclearn.BaseDatos.ManejadorBD
import morajavier.pdm.voclearn.Modelo.Entrada
import morajavier.pdm.voclearn.Modelo.Grupo

class MainActivity : AppCompatActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

        val e1=GestionEntradas.nuevoId()?.let { Entrada(it) }
        e1?.let { GestionEntradas?.nuevaOActualizarEntrada(it) }
        val e2= GestionEntradas.nuevoId()?.let { Entrada(it) }
        e2?.let { GestionEntradas.nuevaOActualizarEntrada(it) }
        val e3= GestionEntradas.nuevoId()?.let { Entrada(it) }
        e3?.let { GestionEntradas.nuevaOActualizarEntrada(it) }
        val e4= GestionEntradas.nuevoId()?.let { Entrada(it) }
        e4?.let { GestionEntradas.nuevaOActualizarEntrada(it) }
        val e5= GestionEntradas.nuevoId()?.let { Entrada(it) }
        e5?.let { GestionEntradas.nuevaOActualizarEntrada(it) }

        val g1= Grupo("Grupo1")
        val g2= Grupo("Grupo2")
        val g3= Grupo("Grupo3")

        GestionGrupos.nuevoOActualizaGrupo(g1)
        GestionGrupos.nuevoOActualizaGrupo(g2)
        GestionGrupos.nuevoOActualizaGrupo(g3)



        var lista=GestionEntradas.obtenerTodasEntradas()

        lista?.let{recorrerEntradas(it)}

        //GestionGrupos.obtenerGrupoPorNombre("Grupo1")?.let { GestionGrupos.borrarUnGrupo(it) }
        //GestionGrupos.obtenerGrupoPorNombre("Grupo2")?.let { GestionGrupos.borrarUnGrupo(it) }

        GestionGrupos.modificarNombreGrupo("Grupo2", "grupo2nuevo")

        for(g in App.gestorBD.r.where(Grupo::class.java).findAll().toList())
            println("********"+g.nombreGrupo+"***************")

        GestionEntradas.obtenerEntradaPorId(0)?.let { GestionGrupos.insertarEntradaEnGrupo("Grupo1", it) }
        GestionEntradas.obtenerEntradaPorId(1)?.let { GestionGrupos.insertarEntradaEnGrupo("Grupo1", it) }
        GestionEntradas.obtenerEntradaPorId(3)?.let { GestionGrupos.insertarEntradaEnGrupo("Grupo1", it) }
        println("GESTION GRUPO 1")
        println("---------------------------")
        App.gestorBD?.r.where(Grupo::class.java).equalTo("nombreGrupo", "Grupo1").findFirst()?.palabras?.let{recorrerListaEntrada(it)}
        App.gestorBD?.r.where(Grupo::class.java).equalTo("nombreGrupo", "Grupo1").findFirst()?.listaGrupos?.let{recorrerListaGrupo(it)}


        GestionEntradas.obtenerEntradaPorId(4)?.let { GestionGrupos.insertarEntradaEnGrupo("Grupo2", it) }
        GestionEntradas.obtenerEntradaPorId(2)?.let { GestionGrupos.insertarEntradaEnGrupo("Grupo2", it) }
        GestionGrupos.obtenerGrupoPorNombre("Grupo1")?.let {
            GestionGrupos.insertarGrupoEnGrupo("Grupo2",
                it
            )
        }
        println("GESTION GRUPO 2")
        println("---------------------------")
        App.gestorBD?.r.where(Grupo::class.java).equalTo("nombreGrupo", "Grupo2").findFirst()?.palabras?.let{recorrerListaEntrada(it)}
        App.gestorBD?.r.where(Grupo::class.java).equalTo("nombreGrupo", "Grupo2").findFirst()?.listaGrupos?.let{recorrerListaGrupo(it)}


        GestionEntradas.obtenerEntradaPorId(0)?.let { GestionGrupos.insertarEntradaEnGrupo("Grupo3", it) }
        GestionEntradas.obtenerEntradaPorId(2)?.let { GestionGrupos.insertarEntradaEnGrupo("Grupo3", it) }
        GestionGrupos.obtenerGrupoPorNombre("Grupo2")?.let {
            GestionGrupos.insertarGrupoEnGrupo("Grupo3",
                it
            )
        }
        println("GESTION GRUPO3")
        println("---------------------------")
        App.gestorBD?.r.where(Grupo::class.java).equalTo("nombreGrupo", "Grupo3").findFirst()?.palabras?.let{recorrerListaEntrada(it)}
        App.gestorBD?.r.where(Grupo::class.java).equalTo("nombreGrupo", "Grupo3").findFirst()?.listaGrupos?.let{recorrerListaGrupo(it)}


        Log.w("MAIN", "La app entra en on Start")
    }

    fun recorrerEntradas(lista:List<Entrada>)
    {
        Log.i("COUNT LISTA Entrada", ""+lista?.count())

        for(i in lista?.let{it}){
            Log.i(""+i.idEntrada, "" +
                    ""+i.descripcion+" "+i.significado)
        }
    }

    fun recorrerListaEntrada(lista : List<Entrada>)
    {
        Log.i("COUNT LIST entradaGrupo", ""+lista?.count())

        for(i in lista?.let{it}){
            Log.i(""+i.idEntrada, "" +
                    ""+i.descripcion+" "+i.significado)
        }
    }

    fun recorrerListaGrupo(lista : List<Grupo>)
    {

        Log.i("COUNT LIST GrupoGrupo", ""+lista?.count())

        for(i in lista?.let{it}){

            if(!lista.isEmpty()) {

                i.palabras?.let { recorrerListaEntrada(it) }

                i.listaGrupos?.let{recorrerListaGrupo(it)}

                Log.i(
                    "" + i.nombreGrupo, "nº Grupos "+i.listaGrupos?.let{it.count()} +" nº Palabras "+i.palabras?.count()
                )
            }
        }
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
            SecurityCopy.hacerCopiaSeguridad(App.gestorBD?.r, this)
        }





}
