package morajavier.pdm.voclearn.Adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_folder.view.*
import kotlinx.android.synthetic.main.layout_seleccion_test.view.*
import morajavier.pdm.voclearn.BaseDatos.CRUDConjuntos.Companion.recorrerListaConjunto
import morajavier.pdm.voclearn.BaseDatos.CRUDGrupo
import morajavier.pdm.voclearn.Modelo.Conjunto
import morajavier.pdm.voclearn.Modelo.Entrada
import morajavier.pdm.voclearn.Modelo.Grupo
import morajavier.pdm.voclearn.R
import morajavier.pdm.voclearn.Vistas.Conj_entra_Activity
import morajavier.pdm.voclearn.Vistas.FolderFragment
import morajavier.pdm.voclearn.Vistas.TestFragment

class AdapterSeleccTest (var items: MutableList<Grupo>, contenedorPadre : TestFragment): RecyclerView.Adapter<AdapterSeleccTest.ViewHolderDatosFol>(){


    var contenedorPadre=contenedorPadre
    //MAP QUE GUARDARÁ LOS ID DE LAS ENTRADAS DE CADA GRUPO Y TODAS LAS QUE LLEVA ANIDADAS
    var listaGruposTest = mutableListOf<Grupo>()
    companion object{
        const val ELEVACIONMAX=25.0f
        const val SINELEVACION=0.0f

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderDatosFol {
        val vistaRecycle=
            LayoutInflater.from(parent.context).inflate(R.layout.layout_seleccion_test, null, false)

        return ViewHolderDatosFol(vistaRecycle)
    }

    override fun getItemCount(): Int {

        return items.size
    }

    override fun onBindViewHolder(holder: AdapterSeleccTest.ViewHolderDatosFol, position: Int) {

        val grupo=items.get(position)
        holder.carpeta.text=grupo.nombreGrupo.toUpperCase()

        //COMPRUEBA SI REALMENTE LA SELECCION DE LA INTERFAZ CAZA CON LA SELECCIÓN DE GRUPOS PARA EL TEST
        if(listaGruposTest.contains(grupo))
        {
            if( holder.itemView.grupoTest.cardElevation== SINELEVACION) {
                println("El grupo " + grupo.nombreGrupo + " esta en la lista test pero no está seleccionado")
                holder.itemView.grupoTest.cardElevation = ELEVACIONMAX
            }
        }
        else{
            if( holder.itemView.grupoTest.cardElevation== ELEVACIONMAX) {
                println("El grupo " + grupo.nombreGrupo + " NO esta en la lista test pero está seleccionado")
                holder.itemView.grupoTest.cardElevation = SINELEVACION
            }
        }


        holder.itemView.grupoTest.setOnClickListener{
            contenedorPadre.seleccionar(it as CardView)
            if(it.cardElevation== ELEVACIONMAX) {
               listaGruposTest.add(grupo)
            }
            else {
               listaGruposTest.remove(grupo)
            }

            controlMap()
        }


    }

    //CLASE DE LAS VISTAS DEL LAYOUT UTILIZADO PARA ESTE ADAPTADOR
    class ViewHolderDatosFol(itemView: View): RecyclerView.ViewHolder(itemView) {

       val carpeta=itemView.nombre_carpeta


    }


    fun controlMap()
    {
        var orden =0;

        //CONTROL DE LA LISTA MAP
        for(i in listaGruposTest){

            println(""+(orden+1)+"-GRUPOS ELEGIDO "+ i.nombreGrupo)
            orden++

        }
    }




}