package morajavier.pdm.voclearn


import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.navegacion_inferior.*
import morajavier.pdm.voclearn.Vistas.DictionaryFragment
import morajavier.pdm.voclearn.Vistas.FolderFragment
import morajavier.pdm.voclearn.Vistas.TestFragment

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

        setContentView(R.layout.activity_main)

        //SE COMPRUEBAN LOS PERMISOS DE ALMACENAMIENTO EXTERNO Y MIRCO
        SecurityCopy.comprobarTodosPermisos(this)

        //PASAMOS EL CONTEXTO DEL MAINACTIVITY, NOS SERVIRA PARA COMPROBAR
        // LOS PERMISOS DE ESCRITURA Y LECTURA EXTERNA
        App.gestorBD.inyeccionContexto(this)
        //COMPROBAMOS Y ACTUALIZAMOS LA BD LOCAL
        App.gestorBD.actualizacionBD()
        //OBTENEMOS LA INSTANCIA DE LA BD
        App.gestorBD.crearInstanciaBD()



        //PASAMOS EL RECEPTOR DE NAVEGACION A LA BARRA DE NAVEGACIÓN INFERIOR (navigation) PARA QUE ACTUE SEGUÚN LO QUE PULSE
        navigation.setOnNavigationItemSelectedListener(receptorNavigation)
        //AÑADIMOS FRAGMENT POR DEFECTO AL PULSAR AUTOMÁTICAMENTE EL BOTON
        navigation.selectedItemId=R.id.dictionary


        Log.i("MAIN", "La app entra en on create")


    }

    private fun cambiarFragmento(fragPrin: Fragment)
    {
        //CAMBIA EL FRAGMENTO Y LO INSERTA EN EL CONTENDOR "fragmentsPrincipal"
        val fragmentTransaction=supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentsPrincipal, fragPrin, fragPrin.javaClass.getSimpleName())
        fragmentTransaction.commit()
    }

    //RESPUESTA PERMISOS
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    )
    {
        //CONTROL PARÁMETROS
        for(p in permissions  )
            Log.e("ARRAY DE PERMISOS ",""+p)
        for(p in grantResults  )
            Log.e("RESULTADOS ",""+p)
        Log.e("RESPUESTA CODE ", ""+requestCode)
        when (requestCode) {
            SecurityCopy.REQUEST_EXTERNAL_STORAGE -> {

                //SI LOS PERMISOS DE ALMACENAMIENTO EXTERNO NO SE ACEPTARON
                //LEVANTAMOS LA VANDERA DE LOS PERMISOS Y NOTIFICANOS POR CONSOLA
                if (grantResults.isEmpty() ||
                    ((grantResults[0] != PackageManager.PERMISSION_GRANTED &&
                            grantResults[1] != PackageManager.PERMISSION_GRANTED))||
                    grantResults[2] != PackageManager.PERMISSION_GRANTED) {

                    SecurityCopy.perAceptados=false
                    Log.e("PERMISOS DENEGADOS ","permiso denegado")

                }
                else
                {

                    SecurityCopy.perAceptados=true
                    Log.e("PERMISOS ACEPTADOS ","permiso aceptado")
                }
                return
            }

            else -> {

            }

        }

    }

    override fun onFragmentInteraction(uri: Uri) {

    }

    override fun onStart() {
        super.onStart()

       /* val e1=CRUDEntradas.nuevoId()?.let { Entrada(it, "casa","", "", 1, "home", "") }
        e1?.let { CRUDEntradas?.nuevaOActualizarEntrada(it) }
        val e2= CRUDEntradas.nuevoId()?.let { Entrada(it, "hacer","", "", 1, "make", "") }
        e2?.let { CRUDEntradas.nuevaOActualizarEntrada(it) }
        val e3= CRUDEntradas.nuevoId()?.let { Entrada(it, "libre","", "", 2, "free", "") }
        e3?.let { CRUDEntradas.nuevaOActualizarEntrada(it) }
        val e4= CRUDEntradas.nuevoId()?.let { Entrada(it, "balon","", "", 3, "ball", "") }
        e4?.let { CRUDEntradas.nuevaOActualizarEntrada(it) }
        val e5= CRUDEntradas.nuevoId()?.let { Entrada(it, "tambien","", "", 1, "too", "") }
        e5?.let { CRUDEntradas.nuevaOActualizarEntrada(it) }
        */



        /*

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

            //LIMPIAMOS DISCO DE ARCHIVOS INSERVIBLES
            SecurityCopy.limpiezaDisco()
            //CREAMOS UNA COPIA DE SEGURIDAD CADA VEZ QUE EL USUARIO SALGA DE LA APP
            //SI TIENE PERMISOS
            if(SecurityCopy.perAceptados)
                SecurityCopy.hacerCopiaSeguridad(App.gestorBD.r, this)

        }





}
