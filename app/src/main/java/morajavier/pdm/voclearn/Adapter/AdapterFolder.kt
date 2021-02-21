package morajavier.pdm.voclearn.Adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_folder.view.*
import morajavier.pdm.voclearn.BaseDatos.CRUDGrupo
import morajavier.pdm.voclearn.Modelo.Grupo
import morajavier.pdm.voclearn.R
import morajavier.pdm.voclearn.Vistas.FolderFragment
import morajavier.pdm.voclearn.Vistas.Conj_entra_Activity


//CREAMOS UNA CLASE ADAPTER, PARA LA LISTA DE GRUPOS QUE CREE EL USUARIO
//IMPLEMENTAMOS LOS MÉTODOS QUE HACEN FALTA AL EXTENDER DE RecyclerView.Adapter<"clase creada interna que extiende de RecyclerView.ViewHolder">
//RECIBE LA REFERENCIA AL CONTENEDOR PADRE
class AdapterFolder (var items: MutableList<Grupo>,  contenedorPadre : FolderFragment): RecyclerView.Adapter<AdapterFolder.ViewHolderDatosFol>() {

    val listaTotalItems=items
    var contenedorPadre=contenedorPadre

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterFolder.ViewHolderDatosFol {
        val vistaRecycle=
            LayoutInflater.from(parent.context).inflate(R.layout.layout_folder, null, false)



        return ViewHolderDatosFol(vistaRecycle)
    }

    private fun recortarNombreCarpeta(nombreCarpeta: String): String {

        if(nombreCarpeta.length>25){
            return nombreCarpeta.substring(0, 20)+"..."
        }
        else{
            return nombreCarpeta
        }
    }

    override fun getItemCount(): Int {

        return items.size
    }

    override fun onBindViewHolder(holder: AdapterFolder.ViewHolderDatosFol, position: Int) {

        val grupoActual=items.get(position)
        holder.nombreCarpeta.setText(grupoActual.nombreGrupo)

        //PASAMOS EL NOMBRE DE LA CARPETA PARA RECORTARLO EN CASO DE QUE TENGA MUCHOS CARACTERES
        holder.nombreCarpeta.setText(recortarNombreCarpeta(holder.nombreCarpeta.text.toString()));

        holder.botonCarpeta.setOnClickListener{

            val intent= Intent(contenedorPadre.context, Conj_entra_Activity::class.java)
            intent.putExtra("nombreGrupo", grupoActual.nombreGrupo)
            intent.putExtra("tipo", "grupo")
            contenedorPadre.startActivity(intent)
        }

        holder.botonCarpeta.setOnLongClickListener{

            contenedorPadre.alertaNuevoEditaGrupo(it, grupoActual,position)
            return@setOnLongClickListener true
        }

        holder.nombreCarpeta.setOnClickListener{
            contenedorPadre.alertaNuevoEditaGrupo(it, grupoActual,position)
        }



    }

    //CLASE DE LAS VISTAS DEL LAYOUT UTILIZADO PARA ESTE ADAPTADOR
    class ViewHolderDatosFol(itemView: View): RecyclerView.ViewHolder(itemView) {

       val nombreCarpeta=itemView.nombre_carp
       val botonCarpeta=itemView.btn_grupo

    }


    fun actualizar(listaFiltrada: MutableList<Grupo>)
    {
        items=listaFiltrada
        notifyDataSetChanged()
    }

    fun inserccionItem()
    {


        items= CRUDGrupo.obtenerTodosLosGrupos() as MutableList<Grupo>
        notifyItemInserted((itemCount)!!)
        notifyItemChanged((itemCount)!!)


    }

    fun borradoItem(posicionBorrado :Int)
    {


        //SI LA LISTA DEVUELTA NO ESTA VACÍA NO ES NULA CASTEAMOS Y ACTUALIZAMOS ITEMS
        CRUDGrupo.obtenerTodosLosGrupos()?.let{
            if(it.isNotEmpty())
                items=it as MutableList<Grupo>
        }

        notifyItemRemoved(posicionBorrado)
        notifyItemChanged(posicionBorrado)
    }

    fun editarNombreGrupo(posicionEditar :Int) {
        items= CRUDGrupo.obtenerTodosLosGrupos() as MutableList<Grupo>
        notifyItemChanged(posicionEditar)
    }


}