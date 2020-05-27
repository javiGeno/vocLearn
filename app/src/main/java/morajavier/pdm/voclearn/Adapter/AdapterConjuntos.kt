package morajavier.pdm.voclearn.Adapter


import android.content.Intent
import android.util.Log
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_conjuntos.view.*
import morajavier.pdm.voclearn.BaseDatos.CRUDConjuntos
import morajavier.pdm.voclearn.BaseDatos.CRUDGrupo
import morajavier.pdm.voclearn.Modelo.Conjunto
import morajavier.pdm.voclearn.Modelo.Entrada
import morajavier.pdm.voclearn.Modelo.Grupo
import morajavier.pdm.voclearn.R
import morajavier.pdm.voclearn.Vistas.Conj_entra_Activity
import java.util.*
import kotlin.collections.ArrayList

class AdapterConjuntos( var items: MutableList<Conjunto>, val contenedorPadre : Conj_entra_Activity): RecyclerView.Adapter<AdapterConjuntos.ViewHolderDatosConjun>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderDatosConjun {

             val vistaRecycle =
                 LayoutInflater.from(parent.context).inflate(R.layout.layout_conjuntos, null, false)

       return ViewHolderDatosConjun(vistaRecycle)

    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolderDatosConjun, position: Int) {

        val conjuntoActual=items.get(position)

        holder.botonConjunto.setOnClickListener{
            val intent= Intent(contenedorPadre, Conj_entra_Activity::class.java)
            intent.putExtra("idConjunto", conjuntoActual.idConjunto)
            intent.putExtra("tipo", "conjunto")
            contenedorPadre.startActivity(intent)
        }

        holder.nombreConjunto.setText(conjuntoActual.nombreConjunto)

        holder.botonConjunto.setOnLongClickListener{

            contenedorPadre.alertaNuevoEditaConjunto(it,position,conjuntoActual)
            return@setOnLongClickListener true
        }

        holder.nombreConjunto.setOnClickListener{
            contenedorPadre.alertaNuevoEditaConjunto(it,position,conjuntoActual)
        }

        holder.botonConjunto.setOnDragListener{v,event->

            eventosEscuchaMovimiento(v, event, conjuntoActual)
        }


    }

    //CÓDIGO OBTENIDO Y MODIFICADO DE LA PÁGINA DE ANDROID
    //PARA QUE LAS CARPETAS PUEDAN ESCUCHAR Y OBTENER LA INFORMACIÓN MOVIDA Entrada
    fun eventosEscuchaMovimiento(v:View, event:DragEvent, conjuntoEscucha:Conjunto):Boolean
    {
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    Toast.makeText(contenedorPadre, R.string.comienzoDrag, Toast.LENGTH_SHORT).show()
                   return true
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    //CAMBIAMOS EL FONDO DEL OBJETO QUE ESCUCHA, PARA INDICAR AL USUARIO QUE SE VA
                    //A AÑADIR AHÍ
                    v.setBackgroundResource(R.drawable.borde_circular_marcado)
                    //Invalidar la vista para forzar un nuevo dibujo en el nuevo tinte
                    v.invalidate()
                    return true
                }

                DragEvent.ACTION_DRAG_LOCATION ->
                    // IGNORAMOS ESTE EVENTO
                    return true

                DragEvent.ACTION_DRAG_EXITED -> {
                    // CAMBIAMOS EL FONDO CUANDO EL OBJETO SALE DEL CUADRO DEL OBJETO QUE ESCUCHA
                    v.setBackgroundResource(R.drawable.borde_circular)
                    //Invalidar la vista para forzar un nuevo dibujo en el nuevo tinte
                    v.invalidate()
                    return true
                }
                DragEvent.ACTION_DROP -> {
                    //DATOS QUE PASAMOS EN EL STARTDRAG DEL ADAPTER DICCIONARIO
                    val arrayValores=event.localState as ArrayList<Objects>
                    val objetoMovido=arrayValores[0] as Entrada
                    val posicionEntrada=arrayValores[1] as Int
                    val adapter=arrayValores[2] as AdapterDiccionario


                    //INSERTAMOS EN LA LISTA DEL CONJUNTO ACTUAL EL CUAL HA ESCUCHADO EL EVENTO
                    CRUDConjuntos.insertarEntradaEnEntradas(conjuntoEscucha, objetoMovido!!)
                    //QUITAMOS DE LA LISTA DE LA CARPETA, LA ENTRADA MOVIDA "objetoMovido"
                    //contenedorPadre.carpeta PUEDE SER UN Grupo O UN Conjunto
                    adapter.quitarEntradaLista( objetoMovido, contenedorPadre.carpeta, posicionEntrada)

                    // CAMBIAMOS EL FONDO CUANDO EL OBJETO SE ARROJA ENCIMA DEL OBJETO QUE ESCUCHA
                    v.setBackgroundResource(R.drawable.borde_circular)
                    //Invalidar la vista para forzar un nuevo dibujo en el nuevo tinte
                    v.invalidate()

                    // Returns true. DragEvent.getResult() will return true.
                    return true
                }

                DragEvent.ACTION_DRAG_ENDED -> {

                    // Invalidates the view to force a redraw
                    v.invalidate()

                    // Does a getResult(), and displays what happened.
                    // Does a getResult(), and displays what happened.
                    when(event.result) {
                        true ->
                            Log.e("ERROR MOV", "El Movimiento de entrada se ha efectuado")
                        false->
                            Log.e("ERROR MOV", "El Movimiento de entrada no se ha efectuado")
                    }
                    // returns true; the value is ignored.
                    return true
                }
                else -> {
                    // An unknown action type was received.
                    Log.e("DragDrop Example", "Unknown action type received by OnDragListener.")
                    return false
                }
            }

        return false
    }


    class ViewHolderDatosConjun(itemView: View): RecyclerView.ViewHolder(itemView){
        val nombreConjunto=itemView.nombre_conjun
        val botonConjunto= itemView.btn_conjunto
    }

    fun inserccionConjunto(carpetaPadre:Any, nombreNuevo:String)
    {
        val nuevoConjunto=Conjunto(CRUDConjuntos.nuevoId()!!, nombreNuevo)

        if(carpetaPadre is Grupo )
        {
            (carpetaPadre)?.let{ CRUDGrupo.insertarConjuntoEnGrupo(it,nuevoConjunto)}
            items=(carpetaPadre)?.listaConjuntos!!
            notifyItemInserted(itemCount)
            notifyItemChanged(itemCount)

        }
        else if( carpetaPadre is Conjunto)
        {
            (carpetaPadre)?.let{CRUDConjuntos.insertarConjuntoEnConjuntos(it,nuevoConjunto)}
            items= (carpetaPadre)?.listaConjuntos!!
            notifyItemInserted(itemCount)
            notifyItemChanged(itemCount)
        }


    }

    fun borradoConjunto(conjuntoBorrar:Conjunto, carpetaPadre:Any, posicionBorrado: Int)
    {
        //BORRAMOS DE LA LISTA DE CONJUNTOS DEL PADRE(Grupo o Conjunto), EL CONJUNTO
        //LUEGO BORRAMOS DE LA BASE DE DATOS, BORRANDO TODOS LOS DESCENDIENTES QUE TENGA
        //PORQUE SOLO VA HABER UNA REFERENCIA AL CONJUNTO, POR ELLO BORRAMOS DE LA BD
        //SI BORRAMOS DE LA LISTA
        //POR ÚLTIMO NOTIFICAMOS
        if(carpetaPadre is Grupo)
        {
            CRUDConjuntos.quitarConjuntoDeLista((carpetaPadre as Grupo).listaConjuntos!!, conjuntoBorrar!!)
            items= (carpetaPadre as Grupo).listaConjuntos!!
        }
        else if(carpetaPadre is Conjunto)
        {
            CRUDConjuntos.quitarConjuntoDeLista((carpetaPadre as Conjunto).listaConjuntos!!, conjuntoBorrar!!)
            items= (carpetaPadre as Conjunto).listaConjuntos!!
        }
        CRUDConjuntos.borrarUnConjunto(conjuntoBorrar!!)

        notifyItemRemoved(posicionBorrado)
        notifyItemChanged(posicionBorrado)
    }




    fun editarNombreConjunto(posicionEditar :Int, conjuntoEdit: Conjunto, nombreNuevo:String) {


        conjuntoEdit?.nombreConjunto=nombreNuevo
        notifyItemChanged(posicionEditar)
    }

}