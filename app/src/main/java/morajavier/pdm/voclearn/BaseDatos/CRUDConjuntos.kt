package morajavier.pdm.voclearn.BaseDatos

import android.util.Log
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
            /*   {Log.i("SUCCESS NUEVO GRUPO", "Un nuevo grupo ha sido introducido o actualizado satifactoriamente")},
               {error-> Log.e("ERROR NUEVO GRUPO", error.message)})*/

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

        fun borrarConjuntoId(filtro: Int): List<Conjunto>
        {
            //BORRA UN CONJUNTO FILTRADO POR SU ID, Y DEVUELVE  UNA LISTA ACTUALIZADA
            val objetivo= obtenerConjunto(filtro)

            //SI EL OBJETIVO A BORRAR ES DISTINTO DE NULO
            //BORRA EN CASCADA UTILIZANDO LA LISTA DE LINKING "fkGrupo" y "fkConjunto"
            objetivo?.let{
                App.gestorBD.r.beginTransaction()
                it.fkGrupo?.let{ borrarFkEnGrupos(it, objetivo) }
                it.fkConjunto?.let{ borrarFkEnConjunto(it, objetivo) }
                App.gestorBD.r.commitTransaction()
            }


            //SI EL OBJETIVO A BORRAR NO ES NULL BORRAMOS
            objetivo?.let{ borrar(it) }

            return obtenerTodosConjuntos()
        }

        //BORRA LAS REFERENCIAS DEL CONJUNTO EN LA LISTA DE CONJUNTOS DE LOS GRUPOS, DE
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
        }

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