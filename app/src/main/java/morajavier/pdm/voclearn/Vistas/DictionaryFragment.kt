package morajavier.pdm.voclearn.Vistas

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_dictionary.*
import kotlinx.android.synthetic.main.navegacion_inferior.*
import morajavier.pdm.voclearn.Adapter.AdapterDiccionario
import morajavier.pdm.voclearn.Adapter.EspacioItemRecycler
import morajavier.pdm.voclearn.BaseDatos.CRUDEntradas
import morajavier.pdm.voclearn.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ESPACIO_ITEMS=48

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [DictionaryFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [DictionaryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
open class DictionaryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }


    }

    override fun onStart() {
        super.onStart()

        //ACTUALIZA EL RECYCLER SOLO SI EXISTEN ENTRADAS
        if(CRUDEntradas.hayEntradas())
            actualizarRecycleView()

    }

    private fun actualizarRecycleView()
    {

            val lineaSeparacion = DividerItemDecoration(listaDiccionario.context, DividerItemDecoration.VERTICAL)
            //GENERA UNA LINEA ENTRE ITEMS DEL RECYCLER
            listaDiccionario.addItemDecoration(lineaSeparacion)
            //GENERA UN ESPACIO ENTRE ITEMS DEL RECYCLER
            listaDiccionario.addItemDecoration(EspacioItemRecycler(ESPACIO_ITEMS))

            listaDiccionario.layoutManager = LinearLayoutManager(this.context)
            listaDiccionario.adapter = AdapterDiccionario(CRUDEntradas.obtenerTodasEntradas())





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
        //SI HAY ENTRADAS INFLAMOS CON LA VISTA QUE CONTIENE EL RECYCLER, EN CASO CONTRARIO INFLAMOS  VISTA DE NO ENCONTRADOS
        if(CRUDEntradas.hayEntradas()) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_dictionary, container, false)
        }
        else
        {
            return inflater.inflate(R.layout.not_item_found, container, false)
        }
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DictionaryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DictionaryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
