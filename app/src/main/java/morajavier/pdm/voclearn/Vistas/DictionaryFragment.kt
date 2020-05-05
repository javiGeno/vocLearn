package morajavier.pdm.voclearn.Vistas

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_dictionary.*
import kotlinx.android.synthetic.main.navegacion_inferior.*
import kotlinx.android.synthetic.main.search_and_add.*
import morajavier.pdm.voclearn.Adapter.AdapterDiccionario
import morajavier.pdm.voclearn.Adapter.EspacioItemRecycler
import morajavier.pdm.voclearn.BaseDatos.CRUDEntradas
import morajavier.pdm.voclearn.R


private const val ESPACIO_ITEMS=48


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [DictionaryFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [DictionaryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
open class DictionaryFragment : Fragment(),
    androidx.appcompat.widget.SearchView.OnCloseListener,
    androidx.appcompat.widget.SearchView.OnQueryTextListener {


    private var listener: OnFragmentInteractionListener? = null
    private var adaptaor: AdapterDiccionario?=null
    private var separacionAniadida:Boolean=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }


    override fun onStart() {
        super.onStart()

        //ACTUALIZA EL RECYCLER SOLO SI EXISTEN ENTRADAS
        if(CRUDEntradas.hayEntradas()) {

            actualizarRecycleView()
            //INDICAMOS EL ESCUCHADOR DEL BUSCADOR, ESTE FRAGMENTO
            buscador.setOnQueryTextListener(this)
            buscador.setOnCloseListener(this)

            //HACEMOS VISIBLE EL RECYCLER Y QUITAMOS EL LAYOUT QUE INDICA QUE HAY ELEMENTOS EN LA BD
            listaDiccionario.visibility=VISIBLE
            layout_no_almacen.visibility= GONE
        }
        else
        {
            //SI NO HAY ENTRADA HACEMOS INVISIBLE EL RECYCLER Y VISIBLE AL LAYOUT QUE INDICA QUE NO HAY NADA EN LA BD
            listaDiccionario.visibility=GONE
            layout_no_almacen.visibility= VISIBLE
        }



        btn_add.setOnClickListener{


            val intentLista= Intent(activity, AddActivity::class.java)
            startActivity(intentLista)

        }

    }

    private fun actualizarRecycleView()
    {
            //PARA QUE AÑADA LA SEPARACIÓN ENTRE ITEMS UNA SOLA VEZ
            if(!separacionAniadida) {
                val lineaSeparacion =
                    DividerItemDecoration(listaDiccionario.context, DividerItemDecoration.VERTICAL)
                //GENERA UNA LINEA ENTRE ITEMS DEL RECYCLER
                listaDiccionario.addItemDecoration(lineaSeparacion)
                //GENERA UN ESPACIO ENTRE ITEMS DEL RECYCLER
                listaDiccionario.addItemDecoration(EspacioItemRecycler(ESPACIO_ITEMS))
                separacionAniadida=true
            }

            listaDiccionario.layoutManager = LinearLayoutManager(this.context)
            this.adaptaor= AdapterDiccionario(CRUDEntradas.obtenerTodasEntradas())
            listaDiccionario.adapter =this.adaptaor





        //ESTA IMPLEMENTACIÓN PERMITE QUE EL MENU DE LA ACTIVIDAD DESAPAREZCA AL HACER SCROLL HACIA ABAJO EN EL RECYCLER
        //Y VUELVA A APARECER AL HACER SCROLL HACIA ARRIBA
        listaDiccionario.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            val barraNavegacion= activity?.navigation
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                Log.e("SCROLL RECYCLE", "eje y: "+dy)
                if (dy > 0 && barraNavegacion!!.isShown()) {
                    barraNavegacion.setVisibility(View.GONE)
                } else if (dy < 0) {
                    barraNavegacion?.setVisibility(View.VISIBLE)

                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {

                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dictionary, container, false)

    }

    //MÉTODOS IMPLEMENTADOS AL IMPLEMENTAR LAS INTERFACES DEL SEARCHVIEW (BUSCADOR)

    override fun onQueryTextSubmit(query: String?): Boolean {
        Log.e("SEARCH", ""+query)
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        Log.e("SEARCH", ""+newText)
        println(""+newText)

        var listaFiltrada=this.adaptaor?.items?.filter {
             it.escrituraIngles?.toLowerCase()!!.contains(newText.toString())
                     || it.significado.toLowerCase().contains(newText.toString())}

        //SI LA LISTA FILTRADA ESTA VACIA QUITAMOS EL RECYCLER, Y MOSTRAMOS LA CAPA DE ABAJO EL  "layout_nothing"
        //SI ESTA LLENA LLAMAMOS AL MÉTODO DEL ADAPTADOR, QUE ACTUALIZA LA LISTA.
        if(!(listaFiltrada!!.isEmpty())) {
            listaDiccionario.setVisibility(VISIBLE)
            adaptaor!!.actualizaLista(listaFiltrada!!)
            println("listaLLena")
        }
        else {
            listaDiccionario.setVisibility(GONE)
            layout_nothing.setVisibility(VISIBLE)
            println("listaVacia")
        }

        return true
    }



    override fun onClose(): Boolean {
        Log.e("SEARCH", "onClose()")

        //RESTABLECE EL BUSCADOR A SU ESTADO INICIAL

        buscador.onActionViewCollapsed()

        return true
    }


    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }



}


