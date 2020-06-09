package morajavier.pdm.voclearn.Vistas

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.barra_comienzo_test.*
import kotlinx.android.synthetic.main.fragment_test.*
import kotlinx.android.synthetic.main.layout_seleccion_test.*
import kotlinx.android.synthetic.main.nothing_to_test.*
import morajavier.pdm.voclearn.Adapter.AdapterSeleccTest
import morajavier.pdm.voclearn.BaseDatos.CRUDEntradas
import morajavier.pdm.voclearn.BaseDatos.CRUDGrupo
import morajavier.pdm.voclearn.Modelo.Entrada
import morajavier.pdm.voclearn.Modelo.Grupo

import morajavier.pdm.voclearn.R



class TestFragment : Fragment() {

    private var listener: OnFragmentInteractionListener? = null
    private  var adaptador: AdapterSeleccTest?=null
    //BANDERA QUE SIRVE PARA SABER SI SE HA SELECCIONADO TODAS LAS PALABRAS O NO
    var todasLasEntradas=false
    //CONTADOR QUE SIRVE PARA SABER CUANTAS CARPETAS SE HAN SELECCIONADO Y ASI PODER OFRECER
    //AL USUARIO ACCEDER AL TEST
    var seleccionados=0


    companion object{
        const val ELEVACIONMAX=25.0f
        const val SINELEVACION=0.0f

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onStart() {
        super.onStart()

        //COMPROBAMOS SI HAY ENTRADAS POR SI HAY QUE VISIBILIZAR UN LAYOUT U OTRO
        comprobarEntradas()

        inicializarRecycler()

        grupoTest.setOnClickListener{
            seleccionar(it as CardView)
            //SI CUANDO HAYA PINCHADO EN EL BOTÓN DEL DICCIONARIO, ESTA LEVANTADO,
            //QUIERE DECIR QUE HA SELECCIONADO TODAS LAS PALABRAS PARA EL TEST
            //UTILIZAREMOS ESTA BANDERA A LA HORA DE PASARLE LA LISTA DE PALABRAS AL TEST
            if(it.cardElevation== ELEVACIONMAX) {
                todasLasEntradas = true
                println("ENTRADAS TEST all "+todasLasEntradas)
            }
            else {
                todasLasEntradas = false
                println("ENTRADAS TEST all "+todasLasEntradas)
            }
        }

        nombre_carpeta.text=nombre_carpeta.text.toString().toUpperCase()

        btn_com_test.setOnClickListener{
            if(todasLasEntradas)
                irATest(CRUDEntradas.obtenerTodosIdsEntradas())
            else
                irATest(adaptador!!.obtenerIdsEntradas())
        }

    }

    //MÉTODO QUE REDIRIGE A LA ACTIVITY PARA COMENZAR EL TEST PASANDO LA LISTA DE IDS, EN CASO DE NO HABER IDS,
    //SIGNIFICA QUE NO TIENE ENTRADAS POR TANTO NOTIFICAMOS AL USUARIO
    fun irATest(lista: IntArray)
    {
        if(lista.isNotEmpty()) {
            val intent = Intent(activity, ActivityTest::class.java)
            intent.putExtra("listaIdsTest", lista)
            startActivity(intent)
        }
        else{
            Toast.makeText(this.activity,getString(R.string.noTest), Toast.LENGTH_SHORT).show()
        }

    }

    //CUANDO PASA A LA OTRA ACTIVITY, RESETEAMOS LOS VALORES
    override fun onPause() {
        super.onPause()

        seleccionados=0
        todasLasEntradas=false
        grupoTest.cardElevation = SINELEVACION
        activarBotonTest()


    }


    fun comprobarEntradas() {

        if(!CRUDEntradas.hayEntradas())
        {
            no_entradas.visibility=VISIBLE

            //TANTO SI PINCHA EN EL IMAGEBUTTON COMO EN EL TEXTO, NOS MOVEMOS AL ACTIVITY DE AÑADIR
            click_no_entradas.setOnClickListener{
                ir_a_add()
            }
            btn_test_no_entradas.setOnClickListener{
                ir_a_add()
            }
        }
        else{
            no_entradas.visibility=GONE
        }
    }

    fun inicializarRecycler()
    {
        listaTest.layoutManager = LinearLayoutManager(this.context)
        this.adaptador= AdapterSeleccTest(CRUDGrupo.obtenerTodosLosGrupos() as MutableList<Grupo>, this)
        listaTest.adapter = this.adaptador

    }

    fun ir_a_add() {
        val intentLista= Intent(activity, AddActivity::class.java)
        intentLista.putExtra("soloInsertar", true)
        startActivity(intentLista)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_test, container, false)
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

    //CAMBIA LA SOMBRA DEL GRUPO SELECCIONADO PARA SABER QUE ENTRARÁ EN EL TEST
    fun seleccionar(it: CardView) {

        //SI TIENE ELEVACION LO PONEMOS SIN ELEVACIÓN Y BAJAMOS UNO EN LOS SELECCIONADOS
        //Y VIVEVERSA
        if(it.cardElevation== ELEVACIONMAX) {
            it.cardElevation = SINELEVACION
            seleccionados--
        }
        else {
            it.cardElevation = ELEVACIONMAX
            seleccionados++
        }

        activarBotonTest()

    }

    fun activarBotonTest()
    {
        if(seleccionados>0)
        {
            btn_com_test.visibility= VISIBLE
        }else{
            btn_com_test.visibility= GONE
        }
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
