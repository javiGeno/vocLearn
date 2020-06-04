package morajavier.pdm.voclearn.Vistas

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.*
import com.google.android.material.snackbar.Snackbar
import io.realm.RealmResults
import kotlinx.android.synthetic.main.fragment_dictionary.*
import kotlinx.android.synthetic.main.navegacion_inferior.*
import kotlinx.android.synthetic.main.search_and_add.*
import morajavier.pdm.voclearn.Adapter.AdapterDiccionario
import morajavier.pdm.voclearn.BaseDatos.CRUDConjuntos
import morajavier.pdm.voclearn.BaseDatos.CRUDEntradas
import morajavier.pdm.voclearn.BaseDatos.CRUDGrupo
import morajavier.pdm.voclearn.Modelo.Conjunto
import morajavier.pdm.voclearn.Modelo.Entrada
import morajavier.pdm.voclearn.Modelo.Grupo
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

        //HACEMOS VISIBLE EL BOTÓN QUE NOS INTERESA EN EL MENÚ PARA QUE LA IMAGEN CAMBIE SEGUN EL FRAGMENT
        btn_add.visibility= VISIBLE
        btn_add_2.visibility= GONE

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
            intentLista.putExtra("soloInsertar", true)
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
            separacionAniadida = true
            //ANIMACIÓN PARA LOS CAMBIOS EN EL RECYCLER
            listaDiccionario.setItemAnimator(DefaultItemAnimator())

        }

        listaDiccionario.layoutManager = LinearLayoutManager(this.context)
        this.adaptaor = this.activity?.let { AdapterDiccionario(CRUDEntradas.obtenerTodasEntradas(), it, R.layout.layout_diccionario) }
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
                borrarElemento(entradaActual,posicionElemento)

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

    fun borrarElemento(entradaActual: Entrada, position:Int)
    {
        //BORRAMOS DE LA BD, Y NOTIFICAMOS CAMBIOS AL VOLVER A OBTENER LA LISTA DE LA BD
        //HEMOS TENIDO QUE BORRAR ANTES EN LE BD Y VOLVER A OBTENER TODA LA LISTA
        //BORRAR EN LA BD, Y EN LA LISTA DEL RECYCLER PARALELAMENTE NOS DABA PROBLEMAS
        //CREAMOS UN OBJETO DE RECUPERACIÓN POR SI EL USUARIO SE ARREPIENTE DE BORRAR, VOLVER A REINSERTARLO
        var objetoRecovery= Entrada(entradaActual.idEntrada,
            entradaActual.significado,
            entradaActual.descripcion,
            entradaActual.probAcierto,
            entradaActual.escrituraIngles,
            entradaActual.imagen,
            entradaActual.audio
            )

        //OBTENEMO LAS LISTA DE LAS FK, PARA CUANDO EL USUARIO DESHAGA LA OPERACIÓN BORRAR, SE AÑADA A LA
        val listaFkConjuntos:ArrayList<Conjunto> =ArrayList()
        val listaFkGrupo:ArrayList<Grupo> =ArrayList()

        //GUARDAMOS LAS REFERENCIAS EN LAS LISTAS LOCALES CREADAS QUE LUEGO UTILIZAREMOS EN LA RECUPERACIÓN
        //SI EL USUARIO DESAHCE LA OPERACIÓN
        entradaActual.fkConjunto?.let {
            for(fk in it) {
                listaFkConjuntos.add(fk)
            }
        }
        entradaActual.fkGrupo?.let {
            for(fk in it) {
                listaFkGrupo.add(fk)
            }

        }


        CRUDEntradas.borrarEntradaId(entradaActual.idEntrada)
        adaptaor?.listaItems= CRUDEntradas.obtenerTodasEntradas()
        adaptaor?.notifyItemRemoved(position)
        adaptaor?.notifyItemChanged(position)


        //SI NO HAY ITMES, NOTIFICAMOS TAMBIÉN AL USUARIO
        if(adaptaor?.itemCount==0)
        {
            listaDiccionario.setVisibility(View.GONE)
            layout_no_almacen.setVisibility(View.VISIBLE)

        }

        //MUESTRA EL SNACKBAR AL BORRAR UN ELEMENTO
        muestraSnack(position, objetoRecovery,listaFkConjuntos,listaFkGrupo)

        //ESCONDEMOS EL TECLADO CUANDO SE MUESTRA EL SNACKBAR
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(listaDiccionario.getWindowToken(), 0)

        //PRINT DE CONTROL
        println("LISLOC AFTERresultante: "+ adaptaor?.listaItems)
        println("LISIT AFTERresultante: "+ adaptaor?.items)
        println("LISLBD AFTERresultante: "+ CRUDEntradas.recorrerListaEntrada(CRUDEntradas.obtenerTodasEntradas()))
    }



    fun muestraSnack(position: Int, objetoRecovery: Entrada, listaFkConjuntos:ArrayList<Conjunto>, listaFkGrupos:ArrayList<Grupo>)
    {
        //ESTE SNACKBAR CANCELA EL BORRADO, REINSERTANDO DE NUEVO EL OBJETO EN LA BASE DE DATOS
        //LA FORMA DE BORRADO QUE HEMOS USADO NOS OBLIGA A REINSERTAR DE NUEVO EL OBJETO CON LOS DATOS QUE TENEMOS
        //BORRAR EN LA LISTA DEL RECYCLER ANTES QUE EN LA BASE DE DATOS NOS DABA PROBLEMAS
        Snackbar.make(listaDiccionario, R.string.borrado, 3000)
            .setAction(R.string.cancelar, {
                    _ -> CRUDEntradas.nuevaOActualizarEntrada(objetoRecovery)
                adaptaor?.listaItems= CRUDEntradas.obtenerTodasEntradas()
                adaptaor?.notifyItemInserted(position)
                adaptaor?.notifyItemChanged(position)

                //PRINT DE CONTROL
                println("LISLOC AFTERreinserccion: "+ adaptaor?.listaItems)
                println("LISIT AFTERreinserccion: "+ adaptaor?.items)
                println("LISLBD AFTERreinserccion: "+ CRUDEntradas.recorrerListaEntrada(CRUDEntradas.obtenerTodasEntradas()))


                //VOLVEMOS A INSERTAR LAS FK QUE TENIA EL USUARIO ALMACENADAS EN LAS DISTINTAS CARPETAS (Grupo o Conjunto)
               listaFkConjuntos.apply {
                    this?.let{for(fk in it)
                        CRUDConjuntos.insertarEntradaEnEntradas(fk, objetoRecovery)}
                }

                listaFkGrupos.apply {
                    this?.let{for(fk in it)
                        CRUDGrupo.insertarEntradaEnEntradas(fk, objetoRecovery)}
                }

                listaDiccionario.setVisibility(View.VISIBLE)

            }).show()


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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dictionary, container, false)



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


