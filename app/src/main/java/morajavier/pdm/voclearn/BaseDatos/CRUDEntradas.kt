package morajavier.pdm.voclearn.BaseDatos

import android.util.Log
import morajavier.pdm.voclearn.App
import morajavier.pdm.voclearn.Modelo.Conjunto
import morajavier.pdm.voclearn.Modelo.Entrada
import morajavier.pdm.voclearn.Modelo.Grupo

class CRUDEntradas {

    companion object{


        fun nuevaOActualizarEntrada(nueva: Entrada)
        {
            //INSERTA O ACTUALIZA  UNA NUEVA ENTRADA
            App.gestorBD.r.executeTransaction({
                it.insertOrUpdate( nueva)
            })

        }

        fun actualizarPropiedadObjeto(entradaActualizar:Entrada, campo:String, dato:String)
        {


            App.gestorBD.r.executeTransaction({
                when (campo) {
                    "palabra" -> {

                        entradaActualizar.escrituraIngles= dato

                    }
                    "traduccion" -> {

                        entradaActualizar.significado=dato
                    }
                    "descripcion" -> {

                        entradaActualizar.descripcion=dato
                    }
                    "imagen"->{
                        entradaActualizar.imagen=dato
                    }
                    "audio"->{
                        entradaActualizar.audio=dato

                    }
                    else ->  false
                }
            })
        }

        //CAMBIA LA DIFICULTAD DE LA PALABRA. MÉTODO UTILIZADO PARA EL TEST CUANDO EL USUARIO ACIERTA O
        //FALLA UNA PREGUNTA
        fun actualizacionDificultad(entradaMod:Entrada, probNueva:Int)
        {

            App.gestorBD.r.beginTransaction()
            entradaMod.probAcierto=probNueva
            App.gestorBD.r.commitTransaction()
        }

        fun borrarEntradaId(filtro: Int): List<Entrada>
        {
            //BORRA UNA ENTRADA FILTRADA POR SU ID, Y DEVUELVE  UNA LISTA ACTUALIZADA
            val objetivo=obtenerEntradaPorId(filtro)

            //SI EL OBJETIVO A BORRAR ES DISTINTO DE NULO
            //BORRA EN CASCADA UTILIZANDO LA LISTA DE LINKING "fkGrupo" y "fkConjunto"
            objetivo?.let{
                            App.gestorBD.r.beginTransaction()
                            it.fkGrupo?.let{borrarFkEnGrupos(it, objetivo) }
                            it.fkConjunto?.let{ borrarFkEnConjunto(it, objetivo)}
                            App.gestorBD.r.commitTransaction()
                         }


            //SI EL OBJETIVO A BORRAR NO ES NULL BORRAMOS
            objetivo?.let{borrar(it)}

            return obtenerTodasEntradas()
        }

        //BORRA LAS REFERENCIAS DE LA ENTRADA EN LA LISTA DE PALABRAS DE LOS GRUPOS DE
        // LA LISTA PASADA POR PARAMETROS "listaGrupos"
        fun borrarFkEnGrupos(listaGrupos: List<Grupo>, entradaBorrar:Entrada){

            for(g in listaGrupos)
            {

                g.palabras?.let{it.remove(entradaBorrar)}

            }

        }

        //BORRA LAS REFERENCIAS DE LA ENTRADA EN LA LISTA DE PALABRAS DE LOS CONJUNTOS DE
        //LA LISTA PASADA POR PARAMETROS "listaConjuntos"
        fun borrarFkEnConjunto(listaConjuntos: List<Conjunto>, entradaBorrar:Entrada){
            for(c in listaConjuntos)
            {

                c.listaPalabras?.let{it.remove(entradaBorrar)}

            }
        }

        private fun borrar(borrar : Entrada)
        {
            //BORRA UNA ENTRADA DEL DICCIONARIO
            App.gestorBD.r.beginTransaction()
            borrar.deleteFromRealm()
            App.gestorBD.r.commitTransaction()
        }

        fun borrarTodasEntradas()
        {
            App.gestorBD.r.executeTransactionAsync({
                it.delete(Entrada::class.java)
            },
                { Log.i("BORRADO ENTRADAS", "Se han borrado todas las entradas")},
                {error-> Log.e("ERROR BORRADO ENTRADAS", error.message)})

        }



        fun obtenerEntradaPorId(idEntrada:Int) : Entrada?
        {
            return  App.gestorBD.r.where(Entrada::class.java)
                .equalTo("idEntrada", idEntrada)
                .findFirst()

        }

        fun tamEntradas():Int
        {
            return obtenerTodasEntradas().size
        }

        fun obtenerTodasEntradas() : List<Entrada>
        {
            //OBTENEMOS UNA LISTA CON TODAS LAS ENTRADAS ORDENADAS POR FECHA DE CREACIÓN
            return  App.gestorBD.r.where<Entrada>(Entrada::class.java)
                .findAll().sort("idEntrada").toList()

        }

        fun obtenerTodosIdsEntradas(): IntArray {
            //OBTENEMOS UNA LISTA CON TODOS LOS IDS DE LAS ENTRADAS
            var lista=ArrayList<Int>()
            val listaEntradas= obtenerTodasEntradas()

            for(e in listaEntradas){

                lista.add(e.idEntrada)
            }

            return lista.toIntArray()


        }

        //DEVUELVE UN ID NUEVO PARA LA BD
        fun nuevoId(): Int?
        {
            println(App.gestorBD.r.where<Entrada>(Entrada::class.java).max("idEntrada")?.let{it}?.toInt()?.plus(1)?: 0)
            return App.gestorBD.r.where<Entrada>(Entrada::class.java).max("idEntrada")?.let{it}?.toInt()?.plus(1)?: 0


        }

        //RECORRE UNA LISTA DE ENTRADAS
        fun recorrerListaEntrada(lista : List<Entrada>)
        {
            Log.i("COUNT LISTA entrada", ""+lista.count())

            for(i in lista){
                Log.i(""+i.idEntrada, "" +
                        ""+i.descripcion+" "+
                        i.significado+" "+
                        i.audio+" "+
                        i.escrituraIngles+" "+
                        i.probAcierto+" "+
                        i.fechaCreacion)

            }
        }



        fun hayEntradas() : Boolean{
            //DEVUELVE VERDADERO SI HAY MAS DE UNA ENTRADA
            return App.gestorBD.r.where<Entrada>(Entrada::class.java).count()>0
        }
    }
}