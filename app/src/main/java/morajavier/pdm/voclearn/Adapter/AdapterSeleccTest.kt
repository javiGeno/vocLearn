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
    var listaEntradasTest = mutableMapOf<Int, Int>()

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

        holder.itemView.grupoTest.setOnClickListener{
            contenedorPadre.seleccionar(it as CardView)
            obtenerIdsEntrada(grupo.palabras!!)
            recorrerConjuntos(grupo.listaConjuntos!!)
            controlMap()
        }



    }

    //CLASE DE LAS VISTAS DEL LAYOUT UTILIZADO PARA ESTE ADAPTADOR
    class ViewHolderDatosFol(itemView: View): RecyclerView.ViewHolder(itemView) {

       val carpeta=itemView.nombre_carpeta


    }

    fun recorrerConjuntos(lista : List<Conjunto>)
    {

        for(i in lista){

            i.listaPalabras?.let { obtenerIdsEntrada(it) }

            i.listaConjuntos?.let{  recorrerConjuntos(it) }

        }
    }

    fun obtenerIdsEntrada(lista : List<Entrada>)
    {
        //MAPEAMOS EL ID CON SU ID PARA QUE NO HAYA PALABRAS REPETIDAS EN LA LISTA DEL TEST CUANDO VAYAMOS A BUSCARLA A LA BD
        //AQUÍ OBTENDREMOS UNA LISTA MAPEADA DE IDS, QUE LUEGO USAREMOS EN EL TEST PARA EXTRAER TODAS LAS PALABRAS
        //Y PREGUNTARLE AL USUARIO
        for(i in lista){

            listaEntradasTest[i.idEntrada]=i.idEntrada

        }


    }

    fun controlMap()
    {
        //CONTROL DE LA LISTA MAP
        for(i in listaEntradasTest.keys){

            println("KEY MAP "+ i)
            println("VALOR MAP"+ listaEntradasTest.getValue(i))

        }
    }

    //VA A DEVOLVER UNA LISTA USANDO LAS KEYS DEL MAP, QUE SERÍA LAS ID DE LAS ENTRADAS SELECCIONADAS DE UNA CARPETA
    //Y PASAREMOS AL INTENT PARA HACER EL TEST
    fun obtenerIdsEntradas(): IntArray {

        var lista=ArrayList<Int>()

        for(i in listaEntradasTest.keys){

            lista.add(i)

        }

        return lista.toIntArray()
    }


}