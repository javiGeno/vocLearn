package morajavier.pdm.voclearn.Vistas

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.*
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.android.synthetic.main.fragment_dictionary.*
import kotlinx.android.synthetic.main.layout_diccionario.*
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

        configuracionRecyclerView()
        comprobarEntradas()
    }

    fun configuracionRecyclerView()
    {
        listaDiccionario.setHasFixedSize(true)
        listaDiccionario.setItemViewCacheSize(20)

    }

    fun comprobarEntradas()
    {
        //ACTUALIZA EL RECYCLER SOLO SI EXISTEN ENTRADAS
        if(CRUDEntradas.hayEntradas()) {

            actualizarRecycleView()
            //INDICAMOS EL ESCUCHADOR DEL BUSCADOR, ESTE FRAGMENTO
            buscador.setOnQueryTextListener(this)
            buscador.setOnCloseListener(this)

            //HACEMOS VISIBLE EL RECYCLER Y QUITAMOS EL LAYOUT QUE INDICA QUE NO HAY ELEMENTOS EN LA BD
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
            //RESTABLECE EL BUSCADOR A SU ESTADO INICIAL
            buscador.onActionViewCollapsed()

        }

    }



    private fun actualizarRecycleView() {
        //PARA QUE AÑADA LA SEPARACIÓN ENTRE ITEMS UNA SOLA VEZ
        if (!separacionAniadida) {
            val lineaSeparacion =
                DividerItemDecoration(listaDiccionario.context, DividerItemDecoration.VERTICAL)
            //GENERA UNA LINEA ENTRE ITEMS DEL RECYCLER
            listaDiccionario.addItemDecoration(lineaSeparacion)
            //GENERA UN ESPACIO ENTRE ITEMS DEL RECYCLER
            //listaDiccionario.addItemDecoration(EspacioItemRecycler(ESPACIO_ITEMS))
            separacionAniadida = true
            //ANIMACIÓN PARA LOS CAMBIOS EN EL RECYCLER
            listaDiccionario.setItemAnimator(DefaultItemAnimator())

        }

        listaDiccionario.layoutManager = LinearLayoutManager(this.context)
        this.adaptaor = this.activity?.let { AdapterDiccionario(CRUDEntradas.obtenerTodasEntradas(), it) }
        listaDiccionario.adapter = this.adaptaor



        //ASIGNAMOS LA CONFIGURACIÓN DEL ItemTouchHelper AL NUESTRO RECYCLER
        ItemTouchHelper(getConfiguracionSwiped()).attachToRecyclerView(listaDiccionario)



        //ESTA IMPLEMENTACIÓN PERMITE QUE EL MENU DE LA ACTIVIDAD DESAPAREZCA AL HACER SCROLL HACIA ABAJO EN EL RECYCLER
        //Y VUELVA A APARECER AL HACER SCROLL HACIA ARRIBA
        listaDiccionario.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            val barraNavegacion= activity?.navigation
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                Log.e("SCROLL RECYCLE", "eje y: "+dy)
                if (dy > 0 && barraNavegacion!!.isShown()) {
                    barraNavegacion.setVisibility(View.GONE)
                } else if (dy <= 0) {
                    barraNavegacion?.setVisibility(View.VISIBLE)

                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {

                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }


    fun getConfiguracionSwiped():ItemTouchHelper.SimpleCallback
    {
        //PARA ARRASTRAR LOS ITEM DEL RECYCLER VIEW Y PODER BORRARLOS
        val mySimpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            )
                    : Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val posicionElemento = viewHolder.adapterPosition
                var entradaActual= adaptaor!!.listaItems.get(posicionElemento)
                println(posicionElemento)
                adaptaor!!.borrarElemento(entradaActual,posicionElemento)

            }

            //PERMITE DIBUJAR UN FONDO PARA CUANDO SE ARRASTRE EL ITEM DEL RECYLER INFORME AL USUARIO DE LO QUE VA HACER
            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

                //OBTENEMOS EL ITEMVIEW DEL VIEWHOLDER Y EL DESPLAZAMIENTO SOBRE EL EJEX DEL MISMO
                var itemView = viewHolder.itemView
                var translationX = dX

                //OBTENEMOS EL ICONO QUE PONDREMOS DE FONDO
                val trashBinIcon =resources.getDrawable(R.drawable.ic_delete_black_50dp)

                //ASIGNAMOS LA POSICION QUE TENDRÁ DENTRO DEL CANVAS
                trashBinIcon.setBounds(
                    itemView.right-trashBinIcon.intrinsicWidth,
                    itemView.top +itemView.height/4,
                    itemView.right+0,
                    itemView.top + trashBinIcon.intrinsicHeight+itemView.height/4)

                //CREAMOS EL RECTÁNGULO QUE SE PINTARÁ CONFORME SE DESPLACE EL ITEM
                val background = RectF(
                    itemView.right.toFloat() + translationX,
                    itemView.top.toFloat(),
                    itemView.right.toFloat(),
                    itemView.bottom.toFloat()
                )
                //PINTAMOS EL RECTÁNGULO EN EL CANVAS
                c.clipRect(background)
                //AÑADIMOS EL COLOR
                c.drawColor(Color.RED)
                //AÑADIMOS EL ICONO QUE CREAMOS ANTERIORMENTE
                trashBinIcon.draw(c)



                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }

        }

        return mySimpleCallback
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


        if(CRUDEntradas.hayEntradas())
        {
            var listaFiltrada = this.adaptaor?.obtenerListaCompleta()?.filter {
                it.escrituraIngles?.toLowerCase()!!.contains(newText.toString())
                        || it.significado.toLowerCase().contains(newText.toString())
            }
            println("LISTA FILTRADA: "+listaFiltrada)

            //SI LA LISTA FILTRADA ESTA VACIA QUITAMOS EL RECYCLER, Y MOSTRAMOS LA CAPA DE ABAJO EL  "layout_nothing"
            //SI ESTA LLENA LLAMAMOS AL MÉTODO DEL ADAPTADOR, QUE ACTUALIZA LA LISTA.
            if (!(listaFiltrada!!.isEmpty())) {
                listaDiccionario.setVisibility(VISIBLE)
                layout_nothing.setVisibility(GONE)
                adaptaor!!.actualizaLista(listaFiltrada!!)
                println("lista buscada LLena")

            } else {
                listaDiccionario.setVisibility(GONE)
                layout_nothing.setVisibility(VISIBLE)
                println("lista buscada Vacia")
            }
        }




        return true
    }


    //EVENTO QUE SE DISPARA CUANDO SE CIERRA EL BUSCADOR
    override fun onClose(): Boolean {
        Log.e("SEARCH", "onClose()")

        //RESTABLECE EL BUSCADOR A SU ESTADO INICIAL
        buscador.onActionViewCollapsed()
        //MOSTRAMOS EL MENÚ POR SI SE CONGELA EN INVISIBLE
        activity?.navigation?.visibility= VISIBLE

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


