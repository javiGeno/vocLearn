package morajavier.pdm.voclearn.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_diccionario.view.*
import kotlinx.android.synthetic.main.layout_diccionario.view.probabilidad
import kotlinx.android.synthetic.main.layout_diccionario.view.textView_palabra_ingles
import kotlinx.android.synthetic.main.layout_diccionario.view.textView_traduccion
import kotlinx.android.synthetic.main.layout_diccionario_seleccion.view.*
import morajavier.pdm.voclearn.FuncionesExtension.cargarImagenCircleNoCache
import morajavier.pdm.voclearn.FuncionesExtension.crearSpinnerCarga
import morajavier.pdm.voclearn.FuncionesExtension.fondoImg
import morajavier.pdm.voclearn.Modelo.Entrada
import morajavier.pdm.voclearn.R


class AdapterSeleccion(var items: MutableList<Entrada>, contenedorPadre : FragmentActivity): RecyclerView.Adapter<AdapterSeleccion.ViewHolderSelecc>() {

    val itemsTotal =items
    var contenedorPadre=contenedorPadre
    var listaCheckeada= mutableListOf<Int>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderSelecc {

        val vistaRecycle= LayoutInflater.from(parent.context).inflate(R.layout.layout_diccionario_seleccion, null, false)

        return AdapterSeleccion.ViewHolderSelecc(vistaRecycle)
    }

    override fun getItemCount(): Int {

        return  items.size
    }

    override fun onBindViewHolder(holder: ViewHolderSelecc, position: Int) {

        val entradaActual=items.get(position)
        holder.cambiarColor(entradaActual.probAcierto)
        entradaActual.imagen?.let {

            println("RUTA IMAGEN ADAPTER " + it)
            holder.circuloColor.cargarImagenCircleNoCache(it, contenedorPadre.crearSpinnerCarga(5f, 30f),entradaActual.fondoImg())

        }

        holder.palabra.text=entradaActual.escrituraIngles
        holder.traduccion.text=entradaActual.significado

        //COMPROBAMOS SI LA PALABRA ESTA EN LA LISTA, Y CONTROLAMOS EL CHECKEO DE LO CHECKBOX
        if(listaCheckeada.contains(entradaActual.idEntrada)) {
            println("La palabra " + entradaActual.significado + " esta en la lista sin check")
            holder.check.setChecked(true);
        }
        else{
            if(holder.check.isChecked) {
                println("La palabra " + entradaActual.significado + " NO esta en la lista y está check ")
                holder.check.setChecked(false);
            }
        }


        //SI PINCHAMOS ENCIMA DEL ELEMENTO TB CHECKEAMOS
        holder.itemView.click_Check.setOnClickListener{
           if (holder.check.isChecked)
             holder.check.setChecked(false)
           else
             holder.check.setChecked(true)

            comprobarCheck(entradaActual, holder.check)
        }

        //SI ESTA CHECKEADO EL ITEM LO AÑADIMOS A LA LISA
        //PARA PASARSELA AL ACTIVITY QUE CONTIENE LA CARPETA DONDE SE
        //INSERTARAN LAS PALABRAS CORRESPONDIENGTES AL LOS ID DE LA listaCkeckeada
        holder.check.setOnClickListener {
            comprobarCheck(entradaActual, holder.check)
        }
    }

     fun comprobarCheck(entradaActual: Entrada, check: AppCompatCheckBox?) {

         if (check!!.isChecked) {
             listaCheckeada.add(entradaActual.idEntrada)
         } else {
             if (listaCheckeada.contains(entradaActual.idEntrada))
                 listaCheckeada.remove(entradaActual.idEntrada)
         }

         Log.w("LISTA CHECK ", ""+listaCheckeada)
    }

    fun actualizaLista(listaFiltrada: MutableList<Entrada>) {

        items=listaFiltrada
        notifyDataSetChanged()

    }


    class ViewHolderSelecc (items: View):RecyclerView.ViewHolder(items){

        val palabra=itemView.textView_palabra_ingles
        val traduccion=itemView.textView_traduccion
        val circuloColor=itemView.probabilidad
        val check=itemView.check_select

        fun cambiarColor( probabilidad:Int)
        {
            when (probabilidad) {
                1-> {

                    circuloColor.setBackgroundResource(R.drawable.circulo_dif1)
                }
                2-> {
                    circuloColor.setBackgroundResource(R.drawable.circulo_dif2)
                }
                3-> {
                    circuloColor.setBackgroundResource(R.drawable.circulo_dif3)
                }
                else->{
                    circuloColor.setBackgroundResource(R.drawable.circulo_dif1)
                }


            }
        }
    }
}