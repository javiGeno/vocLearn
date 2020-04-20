package morajavier.pdm.voclearn.BaseDatos

import android.util.Log
import morajavier.pdm.voclearn.App
import morajavier.pdm.voclearn.Modelo.Entrada

class GestionEntradas {

    companion object{


        fun nuevaOActualizarEntrada(nueva: Entrada)
        {
            //INSERTA O ACTUALIZA  UNA NUEVA ENTRADA
            App.gestorBD.r.executeTransaction({
                it.insertOrUpdate( nueva)
            })

        }

        fun borrarEntradaId(filtro: Int): List<Entrada>
        {
            //BORRA UNA ENTRADA FILTRADA POR SU ID, Y DEVUELVE  UNA LISTA ACTUALIZADA
            val objetivo=obtenerEntradaPorId(filtro)

            //SI EL OBJETIVO A BORRAR NO ES NULL BORRAMOS
            objetivo?.let{borrar(it)}

            return obtenerTodasEntradas()
        }

        fun borrar(borrar : Entrada)
        {
            //BORRA UNA ENTRADA DEL DICCIONARIO
            App.gestorBD.r.beginTransaction()
            borrar.deleteFromRealm()
            App.gestorBD.r.commitTransaction()
        }

        fun borrarTodasEntradas()
        {
            App.gestorBD.r.executeTransactionAsync({
                it.deleteAll()
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

        fun obtenerTodasEntradas() : List<Entrada>
        {
            //OBTENEMOS UNA LISTA CON TODAS LAS ENTRADAS ORDENADAS POR FECHA DE CREACIÓN
            return  App.gestorBD.r.where<Entrada>(Entrada::class.java)
                .findAll().sort("fechaCreacion").toList()

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
            Log.i("COUNT LISTA entrada", ""+lista?.count())

            for(i in lista?.let{it}){
                Log.i(""+i.idEntrada, "" +
                        ""+i.descripcion+" "+
                        i.significado+" "+
                        i.audio+" "+
                        i.escrituraIngles+" "+
                        i.probAcierto+" "+
                        i.fechaCreacion+" "+
                        i.tipo)
            }
        }
    }
}