package morajavier.pdm.voclearn.BaseDatos

import android.app.Activity
import android.content.Context
import android.util.Log
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import morajavier.pdm.voclearn.Modelo.Entrada
import morajavier.pdm.voclearn.SecurityCopy

class ManejadorBD (contexto: Activity){

    //INSTANCIA BD
    var r: Realm?=null
    //CONTEXTO PARA INICIAR LA BD
    val contexto:Activity
    companion object{
        //CONFIGURACION BD REALM
        var config: RealmConfiguration?=null
    }


    init{
        Realm.init(contexto)
        this.contexto=contexto
        actualizacionBD()
    }
/******************************************TABLA ENTRADA*****************************************************************/
    fun nuevaOActualizarEntrada(nueva:Entrada)
    {
        r?.executeTransactionAsync({
            it.copyToRealmOrUpdate( nueva)
        },
        {Log.i("SUCCESS NUEVA ENTRADA", "Una nueva entrada ha sido introducida o actualizada satifactoriamente")},
        {error-> Log.e("ERROR NUEVA ENTRADA", error.message)})

    }

    fun borrarEntradaId(filtro: Int): List<Entrada>?
    {

        val objetivo=obtenerEntradaPorId(filtro)

        borrar(objetivo)

        return obtenerTodasEntradas()
    }

    fun borrar(borrar : Entrada?)
    {
        r?.beginTransaction()
        borrar?.deleteFromRealm()
        r?.commitTransaction()
    }

    fun borrarTodasEntradas()
    {
        r?.executeTransactionAsync({
            it.deleteAll()
        },
            {Log.i("BORRADO ENTRADAS", "Se han borrado todas las entradas")},
            {error-> Log.e("ERROR BORRADO ENTRADAS", error.message)})

    }



    fun obtenerEntradaPorId(idEntrada:Int) : Entrada?
    {
         return  r?.where(Entrada::class.java)
                 ?.equalTo("idEntrada", idEntrada)
                 ?.findFirst()

    }

    fun obtenerTodasEntradas() : List<Entrada>?
    {
        return  r?.where<Entrada>(Entrada::class.java)
                ?.findAll()?.sort("fechaCreacion")?.toList()

    }
/*******************************************************************************************************************************/






/******************************************TABLA GRUPOS*************************************************************************/






/*******************************************************************************************************************************/







/*******************************************************************************************************************************/
/*******************************************************************************************************************************/
    //MÉTODO QUE CONFIGURA LA BD REALM Y
    // OBTIENE LA INSTANCIA DE ELLA PARA USARLA EN LA VISTA DE LA APP
    fun actualizacionBD() {
        //SI HAY BASE DE DATOS LOCAL, QUIERE DECIR QUE NO SE HA DESINSTALADO LA APP
        //POR TANTO ACCEDEMOS A LOS DATOS LOCALES
        if (SecurityCopy.hayBDLocal(contexto)) {
            Realm.init(contexto)

                config = RealmConfiguration.Builder()
                .name("bdLocal.realm")
                .build()

            Realm.setDefaultConfiguration(config)
            this.r = Realm.getDefaultInstance()

            Log.w("MAIN", "Hay bd en local")
        } else {
            //SI NO HAY BD LOCAL, PERO SI EXTERNA
            //LA RESTAURAMOS EN LOCAL
            //SI NO HAY CONSTANCIA DE NINGUNA DE LAS DOS, CREAMOS LA BD NUEVA
            if (SecurityCopy.hayBDExterna(contexto)) {
                SecurityCopy.restaurarCopiaSeguridad(contexto)

                Realm.init(contexto)

                val config = RealmConfiguration.Builder()
                    .name("bdLocal.realm")
                    .build()

                Realm.setDefaultConfiguration(config)
                this.r = Realm.getDefaultInstance()

                Log.w("MAIN", "Se ha restaurado")

            } else {
                Realm.init(contexto)

                val config = RealmConfiguration.Builder()
                    .name("bdLocal.realm")
                    .build()

                Realm.setDefaultConfiguration(config)
                this.r = Realm.getDefaultInstance()

                Log.w("MAIN", "No hay bd en local, se ha creado una nueva BD")
            }
        }
    }
}