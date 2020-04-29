package morajavier.pdm.voclearn.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_diccionario.view.*
import morajavier.pdm.voclearn.Modelo.Entrada
import morajavier.pdm.voclearn.R

//CREAMOS UNA CLASE ADAPTER, PARA LA LISTA DE ENTRADAS QUE INGRESE EL USUARIO
//IMPLEMENTAMOS LOS MÉTODOS QUE HACEN FALTA AL EXTENDER DE RecyclerView.Adapter<"clase creada interna que extiende de RecyclerView.ViewHolder">
class AdapterDiccionario(private val items: List<Entrada>): RecyclerView.Adapter<AdapterDiccionario.ViewHolderDatosDic>() {

    //INFLAMOS EL LAYOUT CREADO PARA ESTE RECYCLER "layout_diccionario"
    //GUARDAMOS EN UNA VARIABLE QUE PASAREMOS A LA INSTANCIA DE NUESTRA CLASE VIEWHOLDER INTERNA
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderDatosDic {

        val vistaRecycle=LayoutInflater.from(parent.context).inflate(R.layout.layout_diccionario, null, false)

        return ViewHolderDatosDic(vistaRecycle)
    }

    //NECESITA IMPLEMENTARSE PARA SABER EL NUMERO DE ELEMENTOS QUE HAY EN LA LISTA
    override fun getItemCount(): Int {

        return items.size
    }

    //CONECTA LOS DATOS DE CADA ELEMENTO DE LA LISTA, CON CADA CAJITA DE MUESTRA DE DATOS INDIVIDUAL
    override fun onBindViewHolder(holder: ViewHolderDatosDic, position: Int) {

        val entradaActual=items.get(position)
        holder.cambiarColor(entradaActual.probAcierto)
        holder.palabra.text=entradaActual.escrituraIngles
        holder.traduccion.text=entradaActual.significado

    }

    class ViewHolderDatosDic(itemView: View): RecyclerView.ViewHolder(itemView) {

        val palabra=itemView.textView_palabra_ingles
        val traduccion=itemView.textView_traduccion
        val circuloColor=itemView.probabilidad

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