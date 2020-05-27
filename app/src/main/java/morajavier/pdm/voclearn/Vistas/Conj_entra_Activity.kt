package morajavier.pdm.voclearn.Vistas

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.DragEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_conj_entra_.*
import kotlinx.android.synthetic.main.barra_atras.*
import kotlinx.android.synthetic.main.layout_add_conjuntos.*
import kotlinx.android.synthetic.main.layout_quitar_entrada.*
import kotlinx.android.synthetic.main.nuevo_grupo.view.*
import morajavier.pdm.voclearn.*
import morajavier.pdm.voclearn.Adapter.AdapterConjuntos
import morajavier.pdm.voclearn.Adapter.AdapterDiccionario
import morajavier.pdm.voclearn.BaseDatos.CRUDConjuntos
import morajavier.pdm.voclearn.BaseDatos.CRUDEntradas
import morajavier.pdm.voclearn.BaseDatos.CRUDGrupo
import morajavier.pdm.voclearn.Modelo.Conjunto
import morajavier.pdm.voclearn.Modelo.Entrada
import morajavier.pdm.voclearn.Modelo.Grupo
import java.util.*
import kotlin.collections.ArrayList


class Conj_entra_Activity : AppCompatActivity() {

    private lateinit var adaptadorCon: AdapterConjuntos
    lateinit var carpeta:Any
    private lateinit var adaptadorEntr: AdapterDiccionario
    companion object{
        //CONSTANTE PARA LA RESPUESTA DE LA ACTIVIDAD QUE AÑADE UNA PALABRA
        //QUE SE AÑADIRÁ A EL GRUPO O CONJUNTO ACTUAL
        const val RESPUESTA_PALABRA_NUEVA = 1
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conj_entra_)

        var tipoDato=intent.getStringExtra("tipo")
        obtenerConjuntoOGrupo(tipoDato)
        actualizarRecycleConjuntos()
        actualizarRecyclerEntradas()


        btn_nuevo_conj.setOnClickListener{

            alertaNuevoEditaConjunto(it, -1, null )
        }

        btn_atras.setOnClickListener{
            finish()
        }

        btn_add_entr.setOnClickListener{

            seleccionarFuenteEntradas()
        }

        cont_trat_entradas.setOnDragListener{ v, event->
            eventosEscuchaMovimiento(v, event)
        }

    }

    override fun onStart(){
        super.onStart()

        //SI MODIFICA ALGO DE ALGUNA ENTRADA CUANDO VUELVA DE LA ACTIVIDAD DETALLES, QUE ACTUALICE
        //LA LISTA DE ENTRADAS
        actualizarListaEntradas()

    }

    fun actualizarListaEntradas()
    {
        if (carpeta is Grupo) {

            adaptadorEntr.listaItems= (carpeta as Grupo).palabras!!

        } else if (carpeta is Conjunto) {

            adaptadorEntr.listaItems=(carpeta as Conjunto).listaPalabras!!
        }

        adaptadorEntr.notifyDataSetChanged()

    }

    //DEPENDIENDO DE SI VIENE DE UN GRUPO O DE UN CONJUNTO DE UN GRUPO, REALIZA UNA MANERA DE EXTRAER LA LISTA
    //DE CONNJUNTOS Y PALABRAS, U OTRA, AL SER DIFERENTES TIPOS DE OBJETOS
    //Y EL TEXTO DEL NOMBRE DE EL CONJUNTO O GRUPO TAMBIÉN SE ESTABLECE
    fun obtenerConjuntoOGrupo(tipo:String)
    {

        when(tipo){
            "conjunto"->{
                val idConjunto=intent.getIntExtra("idConjunto", -1)
                carpeta=CRUDConjuntos.obtenerConjunto(idConjunto)as Conjunto
                adaptadorCon= (carpeta as Conjunto)?.listaConjuntos?.let { AdapterConjuntos(it,this)}!!
                adaptadorEntr=(carpeta as Conjunto)?.listaPalabras?.let{AdapterDiccionario(it, this, R.layout.layout_entradas)}!!
                texto_nombre_carpeta.setText((carpeta as Conjunto)?.nombreConjunto)
            }
            "grupo"->{
                val nombreGrupo=intent.getStringExtra("nombreGrupo")
                carpeta= CRUDGrupo.obtenerGrupoPorNombre(nombreGrupo)as Grupo
                adaptadorCon= (carpeta as Grupo)?.listaConjuntos?.let { AdapterConjuntos(it,this)}!!
                adaptadorEntr=(carpeta as Grupo)?.palabras?.let{AdapterDiccionario(it, this, R.layout.layout_entradas)}!!
                texto_nombre_carpeta.setText((carpeta as Grupo)?.nombreGrupo)

            }
        }

    }

    fun actualizarRecycleConjuntos()
    {
        listaConjuntos.layoutManager=LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true)
        listaConjuntos.adapter=adaptadorCon
    }

    fun actualizarRecyclerEntradas(){

        listaEntradasGrupo.addItemDecoration(DividerItemDecoration(listaEntradasGrupo.context, DividerItemDecoration.VERTICAL))
        listaEntradasGrupo.layoutManager=LinearLayoutManager(this)
        listaEntradasGrupo.adapter=adaptadorEntr
    }


    fun alertaNuevoEditaConjunto(vista: View, posicionEditar: Int, conjuntoEdit: Conjunto?) {

        val inflater= layoutInflater
        val vistaDialogo=inflater.inflate(R.layout.nuevo_grupo, null)

        //SI SE VA A AÑADIR NUEVA CARPETA SE MANTIENE EN FALSO, EN CASO DE QUE CARPETA
        //NO SEA NULO, QUIERE DECIR QUE LA CARPETA SE VA A EDITAR O BORRAR
        //POR TANTO SE LEVANTA LA BANDERA
        var carpetaEditable=false

        //SI conjuntoEdit ES NULO QUIERE DECIR QUE VA A INSERTAR UNA NUEVA CARPETA, NO EDITAR SU NOMBRE
        //SI NO ES NULO LLENAMOS EL CAMPO CON EL NOMBRE ACTUAL, ADEMÁS VISIBILIZAMOS EL CHECKBOx QUE PERMITE BORRAR
        //LA CARPETA
        conjuntoEdit?.let{vistaDialogo.nombre_carp_nuevo.setText(it.nombreConjunto)
            vistaDialogo.check_borrar.visibility= View.VISIBLE
            carpetaEditable=true}?:let{carpetaEditable=false}



        AlertDialog.Builder(ContextThemeWrapper(vista.context, R.style.AlertDialog))
            .setView(vistaDialogo)
            .setPositiveButton(
                R.string.aceptar,
                { dialogInterface: DialogInterface, i: Int ->

                    if(vistaDialogo.check_borrar.isChecked) {

                        adaptadorCon.borradoConjunto(conjuntoEdit!!, carpeta, posicionEditar)

                    }
                    else {
                        val nombreNuevo = vistaDialogo.nombre_carp_nuevo.text.toString()


                        //SI EL MÉTODO SE HA LLAMADO PARA EDITAR, COMO INDICA LA VARIABLE carpetaEditable,
                        //SE MODIFICA EL NOMBRE, EN CASO CONTRARIO SE INSERTA
                        if(carpetaEditable) {

                            adaptadorCon.editarNombreConjunto(posicionEditar, conjuntoEdit!!, nombreNuevo)
                        }
                        else {


                            adaptadorCon.inserccionConjunto(carpeta, nombreNuevo)

                        }

                    }

                })
            .setNegativeButton(R.string.cancelar,
                { dialogInterface: DialogInterface, i: Int ->

                    dialogInterface.dismiss()

                })
            .create().show()


    }

    //MÉTODO QUE ABRE UN CUADRO DE DIÁLOGO CON EL USUARO PARA AÑADIR PALABRAS,
    //OBTENIENDO UNA SELECCIÓN DEL DICCIONARIO O UNA NUEVA PALABRA
    fun seleccionarFuenteEntradas()
    {
        //ARRAY CON LAS TRES OPCIONES QUE LE PASAREMOS AL AlertDialog
        val opciones =arrayOf<CharSequence>(resources.getString(R.string.obtenerDeDicc),
            resources.getString(R.string.añadirNuevaPalabra),
            resources.getString(R.string.cancelar))


        val ventanDialogo= androidx.appcompat.app.AlertDialog.Builder(this)
        ventanDialogo.setTitle(resources.getString(R.string.opciones))
        ventanDialogo.setItems(opciones,{ dialogInterface: DialogInterface, i: Int ->

            //SI ELIGE LA OPCION DE GALERIA
            if(opciones[i].equals(getString(R.string.obtenerDeDicc)))
            {

                println("OBTENER DE DICCIONARIO")
            }
            //SI ELIGE LA OPCION DE CÁMARA
            else if(opciones[i].equals(resources.getString(R.string.añadirNuevaPalabra)))
            {

                val intent = Intent(this, AddActivity::class.java)
                intent.putExtra("soloInsertar", false)
                startActivityForResult(intent, RESPUESTA_PALABRA_NUEVA)

                println("NUEVA ENTRADA")
            }
            else
            {
                dialogInterface.dismiss()
                println("CANCELAR")
            }

        })

        ventanDialogo.show()


    }

    //RESPUESTA PARA NUEVA PALABRA A AÑADIR
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //SI EL RESULTADO ES AFIRMTIVO Y LA RESPUESTA COINCIDE CON LA LLAMADA,
        //OBTENEMOS EL ID NUEVO QUE SE HA INSERTADO EN EL DICCIONARIO
        //Y SI NO ES -1, QUE SERÍA EL RESULTADO POR DEFECTO DE LA RESPUESTA DEL INTENT,
        //OBTENEMOS DE LA BASE DE DATOS LA ENTRADA CORRESPONDIENTE A ESE ID.
        //POR ÚLTIMO INSRTAMOS EN LA LISTA DE ENTRADAS DEL GRUPO O CONJUNTO
        if(resultCode== Activity.RESULT_OK)
        {

            if(requestCode== RESPUESTA_PALABRA_NUEVA)
            {
                val idEntrada=data?.getIntExtra("idPalabra", -1)

                if(idEntrada!=-1) {

                    val entradaParaAddEnLista=CRUDEntradas.obtenerEntradaPorId(idEntrada!!)

                    if (carpeta is Grupo) {

                        CRUDGrupo.insertarEntradaEnEntradas((carpeta as Grupo), entradaParaAddEnLista!!)
                        adaptadorEntr.listaItems= (carpeta as Grupo).palabras!!

                    } else if (carpeta is Conjunto) {

                        CRUDConjuntos.insertarEntradaEnEntradas((carpeta as Conjunto), entradaParaAddEnLista!!)
                        adaptadorEntr.listaItems=(carpeta as Conjunto).listaPalabras!!
                    }

                    adaptadorEntr.notifyItemInserted(adaptadorEntr.itemCount)
                    adaptadorEntr.notifyItemChanged(adaptadorEntr.itemCount)

                }

            }
        }
    }

    //CÓDIGO OBTENIDO Y MODIFICADO DE LA PÁGINA DE ANDROID
    fun eventosEscuchaMovimiento(v:View, event: DragEvent):Boolean
    {
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {

                //CAMBIAMOS BOTONES, AHORA EN VEZ DE AÑADIR, QUITA DE LA LISTA
                btn_add_entr.visibility=GONE
                btn_quit_entr.visibility=VISIBLE

                return true
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                //CAMBIAMOS EL COLOR DE TEXTO Y BORDES A ROJO CUANDO ESTA ENCIMA
                val color= ContextCompat.getColor(this, R.color.dif_center_1)
                quitar.setTextColor(color)
                v.setBackgroundResource(R.drawable.borde1quitar_entrada)
                //Invalidar la vista para forzar un nuevo dibujo en el nuevo tinte
                v.invalidate()
                return true
            }

            DragEvent.ACTION_DRAG_LOCATION ->
                // IGNORAMOS ESTE EVENTO
                return true

            DragEvent.ACTION_DRAG_EXITED -> {
                //CAMBIAMOS EL COLOR DE TEXTO Y BORDES A AZUL, CUANDO SE SALE DEL CUADRO DEL QUE ESCUCHA
                val color= ContextCompat.getColor(this, R.color.design_default_color_primary)
                quitar.setTextColor(color)
                v.setBackgroundResource(R.drawable.borde1)
                //Invalidar la vista para forzar un nuevo dibujo en el nuevo tinte
                v.invalidate()
                return true
            }
            DragEvent.ACTION_DROP -> {
                //DATOS QUE PASAMOS EN EL STARTDRAG DEL ADAPTER DICCIONARIO
                val arrayValores=event.localState as ArrayList<Objects>
                val objetoMovido=arrayValores[0] as Entrada
                val posicionEntrada=arrayValores[1] as Int
                val adapter=arrayValores[2] as AdapterDiccionario

                //QUITAMOS DE LA LISTA DE LA CARPETA, LA ENTRADA MOVIDA "objetoMovido"
                //contenedorPadre.carpeta PUEDE SER UN Grupo O UN Conjunto
                adapter.quitarEntradaLista( objetoMovido, carpeta, posicionEntrada)

                //CAMBIAMOS BOTONES, AHORA EN VEZ DE QUITAR, AÑADE A LA LISTA
                btn_add_entr.visibility= VISIBLE
                btn_quit_entr.visibility= GONE

                //CAMBIAMOS EL COLOR DE TEXTO Y BORDES A AZUL, CUANDO SE SALE DEL CUADRO DEL QUE ESCUCHA
                val color= ContextCompat.getColor(this, R.color.design_default_color_primary)
                quitar.setTextColor(color)
                v.setBackgroundResource(R.drawable.borde1)
                //Invalidar la vista para forzar un nuevo dibujo en el nuevo tinte
                v.invalidate()

                // Returns true. DragEvent.getResult() will return true.
                return true
            }

            DragEvent.ACTION_DRAG_ENDED -> {

                //CAMBIAMOS BOTONES, AHORA EN VEZ DE QUITAR, AÑADE A LA LISTA
                btn_add_entr.visibility= VISIBLE
                btn_quit_entr.visibility= GONE


                // Invalidates the view to force a redraw
                v.invalidate()

                // Does a getResult(), and displays what happened.
                when(event.result) {
                    true ->
                        Log.e("ERROR MOV", "El Movimiento de entrada se ha efectuado")
                    false->
                        Log.e("ERROR MOV", "El Movimiento de entrada no se ha efectuado")
                }

                // returns true; the value is ignored.
                return true
            }
            else -> {
                // An unknown action type was received.
                Log.e("DragDrop Example", "Unknown action type received by OnDragListener.")
                return false
            }
        }

        return false
    }


}
