package morajavier.pdm.voclearn.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_conjuntos.view.*
import morajavier.pdm.voclearn.BaseDatos.CRUDConjuntos
import morajavier.pdm.voclearn.Modelo.Conjunto
import morajavier.pdm.voclearn.R
import morajavier.pdm.voclearn.Vistas.Conj_entra_Activity

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
        println("******************** "+conjuntoActual.nombreConjunto+" ***************************************")
    }


    class ViewHolderDatosConjun(itemView: View): RecyclerView.ViewHolder(itemView){
        val nombreConjunto=itemView.nombre_conjun
        val botonConjunto= itemView.btn_conjunto
    }

}