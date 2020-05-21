package morajavier.pdm.voclearn.BaseDatos

import android.app.Activity
import android.content.Context
import android.util.Log
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import io.realm.kotlin.createObject
import morajavier.pdm.voclearn.Modelo.*
import morajavier.pdm.voclearn.SecurityCopy

class ManejadorBD (appContext:Context){

    //INSTANCIA BD
    lateinit var r: Realm
    //CONTEXTO PARA INICIAR LA BD
    lateinit var contexto:Activity
    companion object{
        //CONFIGURACION BD REALM
        var config: RealmConfiguration?=null
    }


    init{
        Realm.init(appContext)
    }

/*******************************************************************************************************************************/
    //MÉTODO QUE CONFIGURA LA BD REALM Y
    // OBTIENE LA INSTANCIA DE ELLA PARA USARLA EN LA VISTA DE LA APP
    fun actualizacionBD() {
        //SI HAY BASE DE DATOS LOCAL, QUIERE DECIR QUE NO SE HA DESINSTALADO LA APP
        //POR TANTO ACCEDEMOS A LOS DATOS LOCALES
        if (SecurityCopy.hayBDLocal(contexto)) {


                config = RealmConfiguration.Builder()
                .name("bdLocal.realm")
                .build()

            Realm.setDefaultConfiguration(config)


            Log.w("MAIN", "Hay bd en local")
        } else {
            //SI NO HAY BD LOCAL, PERO SI EXTERNA Y ADEMÁS TENEMOS PERMISOS
            //LA RESTAURAMOS
            //SI NO HAY CONSTANCIA DE NINGUNA DE LAS DOS, CREAMOS LA BD NUEVA
            if (SecurityCopy.hayBDExterna() && SecurityCopy.perAceptados) {

                SecurityCopy.restaurarCopiaSeguridad(contexto)



                val config = RealmConfiguration.Builder()
                    .name("bdLocal.realm")
                    .build()

                Realm.setDefaultConfiguration(config)


                Log.w("MAIN", "Se ha restaurado")

            } else {


                val config = RealmConfiguration.Builder()
                    .name("bdLocal.realm")
                    .build()

                Realm.setDefaultConfiguration(config)

                Log.w("MAIN", "No hay bd en local, se ha creado una nueva BD")
            }
        }
    }

    fun inyeccionContexto(contexto:Activity)
    {
        this.contexto=contexto
    }

    fun crearInstanciaBD()
    {
        r=Realm.getDefaultInstance()
    }
}