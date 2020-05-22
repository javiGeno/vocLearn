package morajavier.pdm.voclearn.BaseDatos

import android.util.Log
import io.realm.OrderedRealmCollection
import io.realm.RealmList
import morajavier.pdm.voclearn.App
import morajavier.pdm.voclearn.Modelo.Conjunto
import morajavier.pdm.voclearn.Modelo.Entrada
import morajavier.pdm.voclearn.Modelo.Grupo

class CRUDConjuntos {

    companion object{

        //INSERTA O ACTUALIZA UN CONJUNTO
        fun nuevoOActualizaConjunto(nuevo: Conjunto)
        {
            //INSERTA O ACTUALIZA EN SEGUNDO PLANO UN NUEVO GRUPO
            App.gestorBD.r.executeTransaction({
                it.insertOrUpdate( nuevo)
            })

        }

        fun obtenerConjunto(idConjunto:Int):Conjunto?
        {
            return  App.gestorBD.r.where(Conjunto::class.java)
                   .equalTo("idConjunto", idConjunto)
                   .findFirst()
        }

        fun obtenerTodosConjuntos():List<Conjunto>
        {
            return  App.gestorBD.r.where(Conjunto::class.java)
                .findAll().sort("fechaCreacion").toList()
        }

        fun nuevoId(): Int?
        {
            println(App.gestorBD.r.where(Conjunto::class.java).max("idConjunto")?.let{it}?.toInt()?.plus(1)?: 0)
            return App.gestorBD.r.where(Conjunto::class.java).max("idConjunto")?.let{it}?.toInt()?.plus(1)?: 0

        }

        private fun borrarConjuntoId(filtro: Int): List<Conjunto>
        {
            //BORRA UN CONJUNTO FILTRADO POR SU ID, Y DEVUELVE  UNA LISTA ACTUALIZADA
            val objetivo= obtenerConjunto(filtro)

            //SI EL OBJETIVO A BORRAR NO ES NULL BORRAMOS
            objetivo?.deleteFromRealm()

            return obtenerTodosConjuntos()
        }

        //BORRA UN CONJUNTO PASADO POR PARÁMETROS
        //Y TODAS SUS LISTAS DE CONJUNTOS DESCENDIENTES
        fun borrarUnConjunto(borrarConjunto : Conjunto){



            App.gestorBD.r.beginTransaction()

            println("CONJUNTO PRINCIPAL "+ borrarConjunto.nombreConjunto)
            println("CONJUNTO PRINCIPAL TAMAÑO LIS "+ borrarConjunto.listaConjuntos?.size)

            borrarTodosConjuntos(borrarConjunto.listaConjuntos!!)

            println("CONJUNTO PRINCIPAL BORRADO"+ borrarConjunto.nombreConjunto)
            borrarConjunto.deleteFromRealm()

            App.gestorBD.r.commitTransaction()
        }


        //BORRA TODOS LOS CONJUNTOS DE LA LISTA, BORRANDO ANTES TODOS LOS CONJUNTOS  DE LA LISTA
        //DE CADA CONJUNTO
        fun borrarTodosConjuntos(lista: RealmList<Conjunto>)
        {

            var snapshot: OrderedRealmCollection<Conjunto>  = lista.createSnapshot()
            val iterador=snapshot.iterator()


            while(iterador.hasNext())
            {
                var conjunto=iterador.next()
                println("CONJUNTO  "+ conjunto.nombreConjunto)
                println("CONJUNTO TAMAÑO LIS "+conjunto.nombreConjunto+" "+ conjunto.listaConjuntos?.size)
                conjunto.listaConjuntos?.let { borrarTodosConjuntos(it) }

                println("CONJUNTO  BORRADO "+ conjunto.nombreConjunto)
                borrarConjuntoId(conjunto.idConjunto)

            }

        }

        //QUITA UN CONJUNTO DE UNA LISTA
        fun quitarConjuntoDeLista(lista: RealmList<Conjunto>, conjunAquitar:Conjunto)
        {
            App.gestorBD.r.beginTransaction()
            lista.remove(conjunAquitar)
            App.gestorBD.r.commitTransaction()
        }

        /*//BORRA LAS REFERENCIAS DEL CONJUNTO EN LA LISTA DE CONJUNTOS DE LOS GRUPOS, DE
        //LA LISTA PASADA POR PARAMETROS "listaGrupos"
        fun borrarFkEnGrupos(listaGrupos: List<Grupo>, conjuntoBorrar:Conjunto){

            for(g in listaGrupos)
            {
                g.listaConjuntos?.let{it.remove(conjuntoBorrar)}
            }

        }

        //BORRA LAS REFERENCIAS DEL CONJUNTO EN LA LISTA DE CONJUNTOS DE LOS CONJUNTOS, DE
        //LA LISTA PASADA POR PARAMETROS "listaConjuntos"
        fun borrarFkEnConjunto(listaConjuntos: List<Conjunto>, conjuntoBorrar:Conjunto){
            for(c in listaConjuntos)
            {
                c.listaConjuntos?.let{it.remove(conjuntoBorrar)}
            }
        }*/

        private fun borrar(borrar : Conjunto)
        {
            //BORRA UNA ENTRADA DEL DICCIONARIO
            App.gestorBD.r.beginTransaction()
            borrar.deleteFromRealm()
            App.gestorBD.r.commitTransaction()
        }


        //BUSCAMOS LA PALABRA QUE SE QUIERE INSERTAR
        //SI LA BÚSQUEDA NO HA DEVUELTO NULO, ENTONCES BUSCAMOS EL CONJUNTO DONDE QUEREMOS INSERTARLA
        //SI NO ES NULO INSERTAMOS EN SU LISTA(SI NO ES NULA), Y VOLVEMOS A INSERTAR O ACTUALIZAR EL CONJUNTO
        fun insertarEntradaEnConjunto(idConjunto:Int, idEntrada:Int) {

            val palabraAInsertar=CRUDEntradas.obtenerEntradaPorId(idEntrada)

            if(palabraAInsertar!=null)
                App.gestorBD.r.where(Conjunto::class.java)
                    .equalTo("idConjunto", idConjunto)
                    .findFirst()?.let{it.listaPalabras?.let {
                        App.gestorBD.r.beginTransaction()
                        it.add(palabraAInsertar)
                        CRUDConjuntos.obtenerConjunto(idConjunto)?.let{ App.gestorBD.r.insertOrUpdate(it)}
                        App.gestorBD.r.commitTransaction()
                    }}
                    ?: Log.e("ERROR ", "El grupo "+idConjunto +" no existe")


        }

        fun insertarConjuntoEnConjuntos(conjuntoPadre:Conjunto, conjuntoHijo: Conjunto){


            App.gestorBD.r.beginTransaction()
            conjuntoPadre.listaConjuntos?.add(conjuntoHijo)
            App.gestorBD.r.commitTransaction()

        }

        //RECORRE UNA LISTA DE CONJUNTOS SI ESTA LLENA, YA QUE AL INSTANCIARSE SE HACE COMO UNA LISTA VACÍA
        //EN SEGUNDO LUGAR, RECORRE LA LISTA DE PALABRAS QUE PUEDA TENER EL CONJUNTO
        //POR ÚLTIMO RECORRE LA LISTA DE CONJUNTOS QUE TENGA, VOLVIENDO A LLAMAR A ESTE MÉTODO
        fun recorrerListaConjunto(lista : List<Conjunto>)
        {

            Log.i("COUNT LIST ConjGrupo", ""+lista.count() + " conjuntos hay en esta Lista")

            for(i in lista){

                println(".....................PALABRAS DEL CONJUNTO "+ i.nombreConjunto +"...................................")
                i.listaPalabras?.let { CRUDEntradas.recorrerListaEntrada(it) }

                println(".....................CONJUNTOS DEL CONJUNTO "+ i.nombreConjunto +"...................................")
                i.listaConjuntos?.let{  recorrerListaConjunto(it) }

                    Log.i("" + i.nombreConjunto, "tiene nº de conjuntos anidados: "+i.listaConjuntos?.let{it.count()} +"tiene nº Palabras "+i.listaPalabras?.count())


            }
        }

    }
}