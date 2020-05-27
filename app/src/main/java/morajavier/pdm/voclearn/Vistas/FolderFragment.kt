package morajavier.pdm.voclearn.Vistas

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.*
import kotlinx.android.synthetic.main.fragment_folder.*
import kotlinx.android.synthetic.main.navegacion_inferior.*
import kotlinx.android.synthetic.main.nuevo_grupo.view.*
import kotlinx.android.synthetic.main.search_and_add.*
import morajavier.pdm.voclearn.Adapter.AdapterFolder
import morajavier.pdm.voclearn.BaseDatos.CRUDConjuntos
import morajavier.pdm.voclearn.BaseDatos.CRUDGrupo
import morajavier.pdm.voclearn.Modelo.Grupo

import morajavier.pdm.voclearn.R



class FolderFragment : Fragment() ,
    androidx.appcompat.widget.SearchView.OnCloseListener,
    androidx.appcompat.widget.SearchView.OnQueryTextListener{

    private var listener: OnFragmentInteractionListener? = null

    private  var adaptador: AdapterFolder?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onStart() {
        super.onStart()

        //HACEMOS VISIBLE EL BOTÓN QUE NOS INTERESA EN EL MENÚ PARA QUE LA IMAGEN CAMBIE SEGUN EL FRAGMENT
        btn_add.visibility= View.GONE
        btn_add_2.visibility= View.VISIBLE

        comprobarGrupos()


        btn_add_2.setOnClickListener {
            //EL SEGUNDO Y TERCER PARÁMETRO, ES CUANDO SE QUIERA MODIFICAR
            alertaNuevoEditaGrupo(it, null, -1)
        }

        Log.w("CONTROL CONJUNTOS", ""+CRUDConjuntos.obtenerTodosConjuntos())

    }


   fun alertaNuevoEditaGrupo(it:View, carpeta: Grupo?, posicionEditar:Int) {



        val inflater= requireActivity().layoutInflater
        val vistaDialogo=inflater.inflate(R.layout.nuevo_grupo, null)
        //SI SE VA A AÑADIR NUEVA CARPETA SE MANTIENE EN FALSO, EN CASO DE QUE CARPETA
        //NO SEA NULO, QUIERE DECIR QUE LA CARPETA SE VA A EDITAR O BORRAR
        //POR TANTO SE LEVANTA LA BANDERA
        var carpetaEditable=false

        //SI NOMBRE NO ES NULO QUIERE DECIR QUE VA A INSERTAR UNA NUEVA CARPETA, NO EDITAR SU NOMBRE
        //SI NO ES NULO LLENAMOS EL CAMPO CON EL NOMBRE ACTUAL, ADEMÁS VISIBILIZAMOS EL CHECKBOS QUE PERMITE BORRAR
        //LA CARPETA
        carpeta?.let{vistaDialogo.nombre_carp_nuevo.setText(it.nombreGrupo)
                        vistaDialogo.check_borrar.visibility=VISIBLE
                     carpetaEditable=true}?:let{carpetaEditable=false}

        AlertDialog.Builder(ContextThemeWrapper(it.context, R.style.AlertDialog))
            .setView(vistaDialogo)
            .setPositiveButton(
                R.string.aceptar,
                { dialogInterface: DialogInterface, i: Int ->

                    //BORRAMOS LA CARPETA SI EL USUARIO A CHECKEADO PARA BORRAR
                    //SI NO CHECKEA EDITAMOS O INSERTAMOS
                    if(vistaDialogo.check_borrar.isChecked) {

                        //BORRAMOS DE LA BD
                        carpeta?.let { CRUDGrupo.borrarUnGrupo(it) }
                        adaptador?.borradoItem(posicionEditar)
                        comprobarGrupos()

                    }
                    else {
                        val nombreNuevo = vistaDialogo.nombre_carp_nuevo.text.toString()
                        if (!CRUDGrupo.existeGrupo(nombreNuevo)) {

                              //SI EL MÉTODO SE HA LLAMADO PARA EDITAR, COMO INDICA LA VARIABLE carpetaEditable,
                              //SE MODIFICA EL NOMBRE, EN CASO CONTRARIO SE INSERTA
                               if(carpetaEditable) {
                                   CRUDGrupo.modificarNombreGrupo(carpeta!!.nombreGrupo, nombreNuevo)
                                   adaptador?.editarNombreGrupo(posicionEditar)
                               }
                               else {

                                   //INSERTAMOS EN LA BD
                                   CRUDGrupo.nuevoOActualizaGrupo(Grupo(nombreNuevo))
                                   adaptador?.inserccionItem()

                               }


                            comprobarGrupos()

                        } else {
                            Toast.makeText(it.context, R.string.grupoExistente, Toast.LENGTH_LONG)
                                .show()
                        }
                    }

                })
            .setNegativeButton(R.string.cancelar,
                { dialogInterface: DialogInterface, i: Int ->

                    dialogInterface.dismiss()

                })
            .create().show()


    }



    fun comprobarGrupos()
    {
        //ACTUALIZA EL RECYCLER SOLO SI EXISTEN GRUPOS
        if(CRUDGrupo.hayGrupos()) {




            actualizarRecycleView()
            //INDICAMOS EL ESCUCHADOR DEL BUSCADOR, ESTE FRAGMENTO
            buscador.setOnQueryTextListener(this)
            buscador.setOnCloseListener(this)

            //HACEMOS VISIBLE EL RECYCLER Y QUITAMOS EL LAYOUT QUE INDICA QUE NO HAY ELEMENTOS EN LA BD
            listaCarpetas.visibility= View.VISIBLE
            layout_no_almacen_folder.visibility= View.GONE
        }
        else
        {
            //SI NO HAY GRUPOS HACEMOS INVISIBLE EL RECYCLER Y VISIBLE AL LAYOUT QUE INDICA QUE NO HAY NADA EN LA BD
            listaCarpetas.visibility= View.GONE
            layout_no_almacen_folder.visibility= View.VISIBLE

            //Y INSTACIAMOS EL ADAPTER CON UNA LISTA VACÍA
            this.adaptador = this.activity?.let{AdapterFolder(mutableListOf(),this)}!!

        }







    }


    private fun actualizarRecycleView() {



        listaCarpetas.layoutManager = GridLayoutManager(this.context, 2)
        this.adaptador = this.activity?.let{AdapterFolder(CRUDGrupo.obtenerTodosLosGrupos() as MutableList<Grupo>,this)}!!
        listaCarpetas.adapter = this.adaptador



        //ASIGNAMOS LA CONFIGURACIÓN DEL ItemTouchHelper AL NUESTRO RECYCLER
       // ItemTouchHelper(getConfiguracionSwiped()).attachToRecyclerView(listaDiccionario)



        //ESTA IMPLEMENTACIÓN PERMITE QUE EL MENU DE LA ACTIVIDAD DESAPAREZCA AL HACER SCROLL HACIA ABAJO EN EL RECYCLER
        //Y VUELVA A APARECER AL HACER SCROLL HACIA ARRIBA
        listaCarpetas.addOnScrollListener(object : RecyclerView.OnScrollListener() {

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

    //MÉTODOS IMPLEMENTADOS AL IMPLEMENTAR LAS INTERFACES DEL SEARCHVIEW (BUSCADOR)

    override fun onQueryTextSubmit(query: String?): Boolean {
        Log.e("SEARCH", ""+query)
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        Log.e("SEARCH", ""+newText)
        println(""+newText)



        if(this.adaptador?.listaTotalItems?.isNotEmpty()!!)
      {
            var listaFiltrada = this.adaptador?.listaTotalItems?.filter{
                it.nombreGrupo?.toLowerCase()!!.contains(newText.toString())
            }
            println("LISTA FILTRADA: "+listaFiltrada)

            //SI LA LISTA FILTRADA ESTA VACIA QUITAMOS EL RECYCLER, Y MOSTRAMOS LA CAPA DE ABAJO EL  "layout_nothing"
            //SI ESTA LLENA LLAMAMOS AL MÉTODO DEL ADAPTADOR, QUE ACTUALIZA LA LISTA.
            if (!(listaFiltrada!!.isEmpty())) {
                listaCarpetas.setVisibility(View.VISIBLE)
                layout_nothing_folder.setVisibility(View.GONE)
                adaptador!!.actualizar(listaFiltrada as MutableList<Grupo>)

                println("lista buscada LLena")

            } else {
                listaCarpetas.setVisibility(View.GONE)
                layout_nothing_folder.setVisibility(View.VISIBLE)
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
        //MOSTRAMOS EL MENÚ POR SI SE CONGELA EN INVISIBLE AL CERRAR EL BUSCADOR
        activity?.navigation?.visibility= View.VISIBLE

        return true
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_folder, container, false)
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
