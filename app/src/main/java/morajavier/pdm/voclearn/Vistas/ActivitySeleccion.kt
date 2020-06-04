package morajavier.pdm.voclearn.Vistas

import android.content.Intent
import android.graphics.text.LineBreaker.JUSTIFICATION_MODE_INTER_WORD
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_conj_entra_.*
import kotlinx.android.synthetic.main.activity_seleccion.*
import kotlinx.android.synthetic.main.navegacion_inferior.*
import kotlinx.android.synthetic.main.search_and_add.*
import morajavier.pdm.voclearn.Adapter.AdapterSeleccion
import morajavier.pdm.voclearn.BaseDatos.CRUDEntradas
import morajavier.pdm.voclearn.Modelo.Entrada
import morajavier.pdm.voclearn.R

class ActivitySeleccion : AppCompatActivity(),
    androidx.appcompat.widget.SearchView.OnCloseListener,
    androidx.appcompat.widget.SearchView.OnQueryTextListener{


    private lateinit var adaptador: AdapterSeleccion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seleccion)



        adaptador= AdapterSeleccion(CRUDEntradas.obtenerTodasEntradas() as MutableList<Entrada>, this)
        actualizarRecycler()


        //ACTUALIZAMOS EL TEXTO DEL BOTÓN INDICANDO AL USUARIO DONDE SE INSERTARÁ LO SELECCIONADO
        btn_add_selec.setText(getString(R.string.addTo) +" " + intent.getStringExtra("nombreCarpeta"))

        btn_cancel_selec.setOnClickListener{

            setResult(RESULT_CANCELED)
            finish()
        }

        btn_add_selec.setOnClickListener{

           devolverListaPalabras()
        }

    }

    fun devolverListaPalabras()
    {
        val intent = Intent(this, Conj_entra_Activity::class.java)
        intent.putExtra("listaIdPalabras", adaptador.listaCheckeada.toIntArray())

        Log.w("LISTA DEVUELTA ", ""+adaptador.listaCheckeada.toIntArray())

        setResult(RESULT_OK, intent)
        finish()
    }

    fun actualizarRecycler()
    {
        listaSeleccDiccionario.addItemDecoration(DividerItemDecoration(listaSeleccDiccionario.context, DividerItemDecoration.VERTICAL))
        listaSeleccDiccionario.layoutManager=LinearLayoutManager(this)
        listaSeleccDiccionario.adapter=adaptador

        //INDICAMOS EL ESCUCHADOR DEL BUSCADOR, ESTA ACTIVITY
        buscador.setOnQueryTextListener(this)
        buscador.setOnCloseListener(this)

        //ESTA IMPLEMENTACIÓN PERMITE LOS BOTONES DE ACEPTAR Y CANCELAR DE LA  ACTIVIDAD DESAPAREZCA
        // AL HACER SCROLL HACIA ABAJO EN EL RECYCLER
        //Y VUELVA A APARECER AL HACER SCROLL HACIA ARRIBA
        listaSeleccDiccionario.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            val botones= btns_cancel_acept
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                Log.e("SCROLL RECYCLE", "eje y: "+dy)
                if (dy > 0 && botones!!.isShown()) {
                    botones.setVisibility(View.GONE)
                } else if (dy <= 0) {
                    botones?.setVisibility(View.VISIBLE)

                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {

                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }

    //IMPLEMENTACIÓN MÉTODOS PARA BUSCADOR
    override fun onClose(): Boolean {
        Log.e("SEARCH", "onClose()")

        //RESTABLECE EL BUSCADOR A SU ESTADO INICIAL
        buscador.onActionViewCollapsed()
        //MOSTRAMOS EL MENÚ POR SI SE CONGELA EN INVISIBLE AL CERRAR EL BUSCADOR
        this.navigation?.visibility= View.VISIBLE

        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        Log.e("SEARCH", ""+newText)
        println(""+newText)


        Log.e("SEARCH", ""+newText)
        println(""+newText)


        if(CRUDEntradas.hayEntradas())
        {
            var listaFiltrada = this.adaptador?.itemsTotal?.filter {
                it.escrituraIngles?.toLowerCase()!!.contains(newText.toString())
                        || it.significado.toLowerCase().contains(newText.toString())
            }
            println("LISTA FILTRADA: "+listaFiltrada)

            //SI LA LISTA FILTRADA ESTA VACIA QUITAMOS EL RECYCLER, Y MOSTRAMOS LA CAPA DE ABAJO EL  "layout_nothing"
            //SI ESTA LLENA LLAMAMOS AL MÉTODO DEL ADAPTADOR, QUE ACTUALIZA LA LISTA.
            if (!(listaFiltrada!!.isEmpty())) {
                listaSeleccDiccionario.setVisibility(View.VISIBLE)
                layout_nothing.setVisibility(View.GONE)
                adaptador!!.actualizaLista(listaFiltrada!! as MutableList<Entrada>)
                println("lista buscada LLena")

            } else {
                listaSeleccDiccionario.setVisibility(View.GONE)
                layout_nothing.setVisibility(View.VISIBLE)
                println("lista buscada Vacia")
            }
        }




        return true




        return true    }

}
