package morajavier.pdm.voclearn


import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_dictionary.*
import kotlinx.android.synthetic.main.navegacion_inferior.*
import morajavier.pdm.voclearn.Modelo.SecurityCopy
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

        println("VERSION SDK "+ Build.VERSION.SDK_INT)
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
                //LEVANTAMOS LA BANDERA DE LOS PERMISOS Y NOTIFICANOS POR CONSOLA
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

    override fun onDestroy() {
        super.onDestroy()

        //LIMPIAMOS DISCO DE ARCHIVOS INSERVIBLES
        SecurityCopy.limpiezaDisco()
        //CREAMOS UNA COPIA DE SEGURIDAD CADA VEZ QUE EL USUARIO SALGA DE LA APP
        //SI TIENE PERMISOS
        if(SecurityCopy.perAceptados)
            SecurityCopy.hacerCopiaSeguridad(App.gestorBD.r, this)

    }



    override fun onFragmentInteraction(uri: Uri) {

    }



}
