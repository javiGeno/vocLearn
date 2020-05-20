package morajavier.pdm.voclearn.Adapter

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_folder.*
import kotlinx.android.synthetic.main.layout_diccionario.view.*
import kotlinx.android.synthetic.main.layout_folder.view.*
import morajavier.pdm.voclearn.Modelo.Grupo
import morajavier.pdm.voclearn.R
import morajavier.pdm.voclearn.Vistas.AddActivity
import morajavier.pdm.voclearn.Vistas.DetailActivity
import morajavier.pdm.voclearn.Vistas.FolderFragment


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

    override fun getItemCount(): Int {

        return items.size
    }

    override fun onBindViewHolder(holder: AdapterFolder.ViewHolderDatosFol, position: Int) {

        val grupoActual=items.get(position)
        holder.nombreCarpeta.setText(grupoActual.nombreGrupo)

        holder.botonCarpeta.setOnClickListener{

            val intent= Intent(contenedorPadre.context, AddActivity::class.java)
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
}