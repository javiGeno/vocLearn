package morajavier.pdm.voclearn.BaseDatos

import android.util.Log
import morajavier.pdm.voclearn.App
import morajavier.pdm.voclearn.Modelo.Conjunto
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

        fun hayGrupos() : Boolean{
            //DEVUELVE VERDADERO SI HAY MAS DE UN GRUPO
            return App.gestorBD.r.where<Grupo>(Grupo::class.java).count()>0
        }

        fun existeGrupo(nombreNuevo : String) :Boolean
        {
            val existe=obtenerGrupoPorNombre(nombreNuevo)

            existe?.let{return true}?: return false

        }

        //BORRA UN GRUPO PASADO POR PARÁMETROS
        //Y TODAS SUS LISTAS DE CONJUNTOS DESCENDIENTES
        fun borrarUnGrupo(borrarGrupo : Grupo){

            App.gestorBD.r.beginTransaction()

            println("CONJUNTO PRINCIPAL "+ borrarGrupo.nombreGrupo)
            println("CONJUNTO PRINCIPAL TAMAÑO LIS "+ borrarGrupo.listaConjuntos?.size)


            CRUDConjuntos.borrarTodosConjuntos(borrarGrupo.listaConjuntos!!)

            println("CONJUNTO PRINCIPAL BORRADO"+ borrarGrupo.nombreGrupo)

            borrarGrupo.deleteFromRealm()
            App.gestorBD.r.commitTransaction()
        }




        fun obtenerGrupoPorNombre(nombreGrupo:String): Grupo? {

            return  App.gestorBD.r.where(Grupo::class.java)
                .equalTo("nombreGrupo", nombreGrupo)
                .findFirst()
        }

        //EL MÉTODO OBTIENE EL GRUPO CON EL NOMBRE grupoAntiguo , LO COPIA EN grupoNew
        //LE CAMBIAMOS EL NOMBRE A grupoNew, LO INSERTAMOS, Y BORRAMOS EL ANTIGUO

        fun modificarNombreGrupo(grupoAntiguo : String,nuevoNombre:String)
        {
            App.gestorBD.r.executeTransaction({
                val grupoOld = App.gestorBD.r.where(Grupo::class.java)?.equalTo("nombreGrupo", grupoAntiguo)?.findFirst()
                val grupoNew = App.gestorBD.r.copyFromRealm(grupoOld)
                grupoNew?.nombreGrupo = nuevoNombre
                App.gestorBD.r.copyToRealmOrUpdate(grupoNew)
                grupoOld?.deleteFromRealm()
            })


        }


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


        fun insertarConjuntoEnGrupo(grupo:Grupo, conjunto: Conjunto){


            App.gestorBD.r.beginTransaction()
            grupo.listaConjuntos?.add(conjunto)
            App.gestorBD.r.commitTransaction()

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