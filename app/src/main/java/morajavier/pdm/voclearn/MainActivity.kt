package morajavier.pdm.voclearn


import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.navegacion_inferior.*
import morajavier.pdm.voclearn.BaseDatos.CRUDConjuntos
import morajavier.pdm.voclearn.BaseDatos.CRUDEntradas
import morajavier.pdm.voclearn.BaseDatos.CRUDGrupo
import morajavier.pdm.voclearn.Modelo.Conjunto
import morajavier.pdm.voclearn.Modelo.Entrada
import morajavier.pdm.voclearn.Modelo.Grupo
import morajavier.pdm.voclearn.Vistas.DictionaryFragment
import morajavier.pdm.voclearn.Vistas.FolderFragment
import morajavier.pdm.voclearn.Vistas.TestFragment
import morajavier.pdm.voclearn.Vistas.menu

class MainActivity : AppCompatActivity(), DictionaryFragment.OnFragmentInteractionListener, FolderFragment.OnFragmentInteractionListener, TestFragment.OnFragmentInteractionListener{


    //RECEPTOR PARA LOS ITEM DEL MENÚ DE NAVEGACION INFERIOR
    private val receptorNavigation= BottomNavigationView.OnNavigationItemSelectedListener {

        when (it.itemId) {
            R.id.folder -> {

                val fragmento=FolderFragment()
                cambiarFragmento(fragmento)
                return@OnNavigationItemSelectedListener true
            }
            R.id.dictionary -> {
                val fragmento=DictionaryFragment()
                cambiarFragmento(fragmento)
                return@OnNavigationItemSelectedListener true
            }
            R.id.Test -> {
                val fragmento= TestFragment()
                cambiarFragmento(fragmento)
                return@OnNavigationItemSelectedListener true
            }

            else ->  false
        }
    }


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

        //PASAMOS EL RECEPTOR DE NAVEGACION A LA BARRA DE NAVEGACIÓN INFERIOR (navigation) PARA QUE ACTUE SEGUÚN LO QUE PULSE
        navigation.setOnNavigationItemSelectedListener(receptorNavigation)



        Log.i("MAIN", "La app entra en on create")


    }

    private fun cambiarFragmento(fragPrin: Fragment)
    {
        //CAMBIA EL FRAGMENTO Y LO INSERTA EN EL CONTENDOR "fragmentsPrincipal"
        val fragmentTransaction=supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentsPrincipal, fragPrin, fragPrin.javaClass.getSimpleName())
        fragmentTransaction.commit()
    }

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

        lista?.let{CRUDEntradas.recorrerListaEntrada(it)}

        CRUDEntradas.borrarEntradaId(1)
        CRUDConjuntos.borrarConjuntoId(1)


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
        CRUDConjuntos.insertarEntradaEnConjunto(0, 2)

        CRUDGrupo.recorrerListaGrupo(CRUDGrupo.obtenerTodosLosGrupos())

        Log.w("PALABRAS TOTAL", "Palabras registradas")
        CRUDEntradas.recorrerListaEntrada(CRUDEntradas.obtenerTodasEntradas())



        Log.w("MAIN", "La app entra en on Start")*/
    }





    /*

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
