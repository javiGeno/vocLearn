package morajavier.pdm.voclearn.BaseDatos

import android.util.Log
import morajavier.pdm.voclearn.App
import morajavier.pdm.voclearn.Modelo.Entrada
import morajavier.pdm.voclearn.Modelo.Grupo

class CRUDGrupo {

    companion object{
        fun nuevoOActualizaGrupo(nuevo: Grupo)
        {
            //INSERTA O ACTUALIZA EN SEGUNDO PLANO UN NUEVO GRUPO
            App.gestorBD.r.executeTransaction({
                it.insertOrUpdate( nuevo)
            })
            /*   {Log.i("SUCCESS NUEVO GRUPO", "Un nuevo grupo ha sido introducido o actualizado satifactoriamente")},
               {error-> Log.e("ERROR NUEVO GRUPO", error.message)})*/

        }

        fun obtenerTodosLosGrupos(): List<Grupo>
        {
            return App.gestorBD.r.where(Grupo::class.java).findAll().sort("fechaCreacion").toList()
        }



        //BORRA UN GRUPO PASADO POR PARÁMETROS
        fun borrarUnGrupo(borrarGrupo : Grupo){

            App.gestorBD.r.beginTransaction()
            borrarGrupo.deleteFromRealm()
            App.gestorBD.r.commitTransaction()
        }



        fun obtenerGrupoPorNombre(nombreGrupo:String): Grupo? {

            return  App.gestorBD.r.where(Grupo::class.java)
                .equalTo("nombreGrupo", nombreGrupo)
                .findFirst()
        }

        //EL MÉTODO COMPRUEBA SI HAY ALGÚN GRUPO CON EL NUEVO NOMBRE, PARA NO MACHACARLO EN ESE CASO
        //SI LO QUE DEVUELVE EL MÉTODO DE BÚSQUEDA ES NULO ENTONCES SE REALIZA LA OPERACIÓN DE MODIFICACIÓN
        //DEL NUEVO NOMBRE (ID)
        /*fun modificarNombreGrupo(grupoAntiguo : String,nuevoNombre:String)
        {
            App.gestorBD.r.executeTransaction {
                if (obtenerGrupoPorNombre(nuevoNombre) == null)
                    obtenerGrupoPorNombre(grupoAntiguo)?.let {
                        val nuevoObjeto = Grupo(nuevoNombre, it.palabras, it.listaGrupos)
                        App.gestorBD.r.copyFromRealm(nuevoObjeto)
                        nuevoOActualizaGrupo(it)
                        println("******" + it.nombreGrupo)
                    }
            }

//asldfkhaskdjfhsalfikhdsalkdshflñsk.jf//
            val teamRealmObj = realm.where(Team::class.java)?.equalTo("name", oldTeamName)?.findFirst()
            val newTeamObj = realm.copyFromRealm(teamRealmObj)
            newTeamObj?.name = newTeamName
            realm.copyToRealmOrUpdate(newTeamObj)
            teamRealmObj?.deleteFromRealm()
        }*/


        //BUSCAMOS LA PALABRA QUE SE QUIERE INSERTAR
        //SI LA BÚSQUEDA NO HA DEVUELTO NULO, ENTONCES BUSCAMOS EL GRUPO DONDE QUEREMOS INSERTARLA
        //SI NO ES NULO INSERTAMOS EN SU LISTA(SI NO ES NULA), Y VOLVEMOS A INSERTAR O ACTUALIZAR EL GRUPO
        fun insertarEntradaEnGrupo(nombreGrupo:String, idEntrada:Int) {

            val palabraAInsertar=CRUDEntradas.obtenerEntradaPorId(idEntrada)

            if(palabraAInsertar!=null)
                App.gestorBD.r.where(Grupo::class.java)
                .equalTo("nombreGrupo", nombreGrupo)
                .findFirst()?.let{it.palabras?.let {
                    App.gestorBD.r.beginTransaction()
                    it.add(palabraAInsertar)
                    obtenerGrupoPorNombre(nombreGrupo)?.let{ App.gestorBD.r.insertOrUpdate(it)}
                    App.gestorBD.r.commitTransaction()
                }}
                ?: Log.e("ERROR ", "El grupo "+nombreGrupo+" no existe")


        }

        //BUSCAMOS EL CONJUNTO QUE SE QUIERE INSERTAR
        //SI LA BÚSQUEDA NO HA DEVUELTO NULO, ENTONCES BUSCAMOS EL GRUPO DONDE QUEREMOS INSERTARLO
        //SI NO ES NULO INSERTAMOS EN SU LISTA(SI NO ES NULA), Y VOLVEMOS A INSERTAR O ACTUALIZAR EL GRUPO
        fun insertarConjuntoEnGrupo(nombreGrupo:String, idConjunto:Int ){

            val conjuntoAInsertar=CRUDConjuntos.obtenerConjunto(idConjunto)

            if(conjuntoAInsertar!=null)
                App.gestorBD.r.where(Grupo::class.java)
                    .equalTo("nombreGrupo", nombreGrupo)
                    .findFirst()?.let{it.listaConjuntos?.let {
                        App.gestorBD.r.beginTransaction()
                        it.add(conjuntoAInsertar)
                        obtenerGrupoPorNombre(nombreGrupo)?.let{ App.gestorBD.r.insertOrUpdate(it)}
                        App.gestorBD.r.commitTransaction()
                    }}
                    ?: Log.e("ERROR ", "El grupo "+nombreGrupo+" no existe")


        }

        //BORRA UNA PALABRA EN UN GRUPO CREADO, SI LA LISTA DE PALABRAS NO ES NULA,
        //BUSCA UN GRUPO Y LE BORRA LA REFERENCIA A ESA PALABRA DE SU LISTA
        fun borrarUnaPalabraDeGrupo(nombreGrupo:String, idEntrada:Int)
        {

            App.gestorBD.r.where(Grupo::class.java)
                .equalTo("nombreGrupo", nombreGrupo)
                .findFirst()?.let{
                    App.gestorBD.r.beginTransaction()
                    it.palabras?.let { it.remove(CRUDEntradas.obtenerEntradaPorId(idEntrada))
                        obtenerGrupoPorNombre(nombreGrupo)?.let{ App.gestorBD.r.insertOrUpdate(it)}
                        App.gestorBD.r.commitTransaction()
                    }}?: Log.e("ERR QUITAR LIST ENTRA ", "El grupo "+nombreGrupo+" no existe")
        }

        //BORRA UN CONJUNTO EN UN GRUPO CREADO, SI LA LISTA DE GRUPO NO ES NULA,
        //BUSCA UN GRUPO Y LE BORRRA UN GRUPO DE SU LISTA
        fun borrarUnConjuntoDeUnGrupo(nombreGrupoPadre:String, idConjunto:Int )
        {

            App.gestorBD.r.where(Grupo::class.java)
                .equalTo("nombreGrupo", nombreGrupoPadre)
                .findFirst()?.let{
                    App.gestorBD.r.beginTransaction()
                        it.listaConjuntos?.let { it.remove(CRUDConjuntos.obtenerConjunto(idConjunto))
                        obtenerGrupoPorNombre(nombreGrupoPadre)?.let{ App.gestorBD.r.insertOrUpdate(it)}
                        App.gestorBD.r.commitTransaction()
                    }}?: Log.e("ERR QUITAR LIST GRUPO", "El grupo "+nombreGrupoPadre+" no existe")

        }

        //RECORRE UNA LISTA DE GRUPOS SI ESTA LLENA, YA QUE AL INSTANCIARSE SE HACE COMO UNA LISTA VACÍA
        //EN SEGUNDO LUGAR, RECORRE LA LISTA DE PALABRAS
        //POR ÚLTIMO RECORRE LA LISTA DE CONJUNTOS QUE TENGA
        fun recorrerListaGrupo(lista : List<Grupo>)
        {


            for(i in lista){

                println("**************************"+i.nombreGrupo+"**************************************************************")
                Log.i("" + i.nombreGrupo, "tiene nº Conjuntos "+i.listaConjuntos?.let{it.count()} +" tiene nº Palabras "+i.palabras?.count())

                println("*****************************PALABRAS*********************************")
                i.palabras?.let { CRUDEntradas.recorrerListaEntrada(it) }

                println("*****************************CONJUNTOS*********************************")
                i.listaConjuntos?.let{ CRUDConjuntos.recorrerListaConjunto(it)}

                Log.i(
                    "" + i.nombreGrupo, "tiene nº Conjuntos "+i.listaConjuntos?.let{it.count()} +" tiene nº Palabras "+i.palabras?.count()
                )
                println("****************************************************************************************\n")


            }
        }
    }
}