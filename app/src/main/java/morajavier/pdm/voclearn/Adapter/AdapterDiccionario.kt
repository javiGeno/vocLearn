package morajavier.pdm.voclearn.Adapter



import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_dictionary.*
import kotlinx.android.synthetic.main.layout_diccionario.view.*
import morajavier.pdm.voclearn.BaseDatos.CRUDEntradas
import morajavier.pdm.voclearn.FuncionesExtension.*
import morajavier.pdm.voclearn.Modelo.Entrada
import morajavier.pdm.voclearn.R
import morajavier.pdm.voclearn.Sonido
import morajavier.pdm.voclearn.Vistas.DetailActivity


//CREAMOS UNA CLASE ADAPTER, PARA LA LISTA DE ENTRADAS QUE INGRESE EL USUARIO
//IMPLEMENTAMOS LOS MÉTODOS QUE HACEN FALTA AL EXTENDER DE RecyclerView.Adapter<"clase creada interna que extiende de RecyclerView.ViewHolder">
//RECIBE LA REFERENCIA AL CONTENEDOR PADRE
class AdapterDiccionario(val items: List<Entrada>, contenedorPadre : FragmentActivity): RecyclerView.Adapter<AdapterDiccionario.ViewHolderDatosDic>() {


     var listaItems = items
     var contenedorPadre=contenedorPadre


    //INFLAMOS EL LAYOUT CREADO PARA ESTE RECYCLER "layout_diccionario"
    //GUARDAMOS EN UNA VARIABLE QUE PASAREMOS A LA INSTANCIA DE NUESTRA CLASE VIEWHOLDER INTERNA
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderDatosDic {

        val vistaRecycle=LayoutInflater.from(parent.context).inflate(R.layout.layout_diccionario, null, false)

        return ViewHolderDatosDic(vistaRecycle)
    }

    //NECESITA IMPLEMENTARSE PARA SABER EL NUMERO DE ELEMENTOS QUE HAY EN LA LISTA
    override fun getItemCount(): Int {

        return listaItems.size
    }



    //CONECTA LOS DATOS DE CADA ELEMENTO DE LA LISTA, CON CADA CAJITA DE MUESTRA DE DATOS INDIVIDUAL
    override fun onBindViewHolder(holder: ViewHolderDatosDic, position: Int) {

        val entradaActual=listaItems.get(position)
        holder.cambiarColor(entradaActual.probAcierto)
        entradaActual.imagen?.let {
            if(!(it.isEmpty())) {
                println("RUTA IMAGEN ADAPTER " + it)
                holder.circuloColor.cargarImagenCircleNoCache(it, contenedorPadre.crearSpinnerCarga(5f, 30f))
            }
        }


        holder.palabra.text=entradaActual.escrituraIngles
        holder.traduccion.text=entradaActual.significado
        var reproducirAudio: MediaPlayer? =null


        //SI LA ENTRADA DE AUDIO ES NULA O ES UNA CADENA VACÍA, HACEMOS DESAPARECER EL BOTON DEL PLAY,
        // POR LO CONTRARIO LO MOSTRAMOS
        entradaActual.audio?.let{
            if(!it.isEmpty()) {
                holder.mediaAudio.setVisibility(View.VISIBLE)

                //PREPARAMOS EL AUDIO CORRESPONDIENTE A ESTA PALABRA, QUE SE ENCUENTRA EN LA
                //MEMORIA INTERNA DEL MÓVIL.
                reproducirAudio=MediaPlayer()
                Sonido.preparacionAudio(reproducirAudio!!, entradaActual.idEntrada)

                //CUANDO TERMINE DE REPRODUCIRSE EL AUDIO, CAMBIAMOS EL ICONO
                reproducirAudio!!.setOnCompletionListener {
                    holder.mediaAudio.setBackgroundResource(android.R.drawable.ic_media_play)

                }
            }
            else
            {
                holder.mediaAudio.setVisibility( View.GONE)
            }


        }?:holder.mediaAudio.setVisibility(View.GONE)




        holder.mediaAudio.setOnClickListener{

            //FUNCIÓN DE EXTENSIÓN QUE PONE EN PAUSA O EN PLAY SEGÚN SU ESTADO Y ADEMÁS
            //CAMBIA LA IMAGEN DEL BOTÓN REPRODUCCÍON
            it.cambioImagen(reproducirAudio!!)


        }

        //CUANDO PULSAMOS EN EL LAYOUT CON EL ID click_detalle NOS LLEVA A LA ACTIVIDAD DONDE SE MOSTRARÁN
        //LOS DATOS DE LA ENTRADA EN LA QUE SE HA PINCHADO
        holder.itemView.click_detalle.setOnClickListener {

            val intent=Intent(contenedorPadre, DetailActivity::class.java)
            intent.putExtra("idEntrada", entradaActual.idEntrada)
            contenedorPadre.startActivity(intent)
        }

    }



    fun borrarElemento(entradaActual:Entrada, position:Int)
    {
        //BORRAMOS DE LA BD, Y NOTIFICAMOS CAMBIOS AL VOLVER A OBTENER LA LISTA DE LA BD
        //HEMOS TENIDO QUE BORRAR ANTES EN LE BD Y VOLVER A OBTENER TODA LA LISTA
        //BORRAR EN LA BD, Y EN LA LISTA DEL RECYCLER PARALELAMENTE NOS DABA PROBLEMAS
        //CREAMOS UN OBJETO DE RECUPERACIÓN POR SI EL USUARIO SE ARREPIENTE DE BORRAR, VOLVER A REINSERTARLO
        val objetoRecovery=Entrada(entradaActual.idEntrada,
            entradaActual.significado,
            entradaActual.descripcion,
            entradaActual.probAcierto,
            entradaActual.escrituraIngles,
            entradaActual.imagen,
            entradaActual.audio)
        CRUDEntradas.borrarEntradaId(entradaActual.idEntrada)
        this.listaItems= CRUDEntradas.obtenerTodasEntradas()
        notifyItemRemoved(position)
        notifyItemChanged(position)

        //PRINT DE CONTROL
        println("LISLOC AFTERDELETE: "+ listaItems)
        println("LISIT AFTERDELETE: "+ items)
        println("LISLBD AFTERDELETE: "+CRUDEntradas.recorrerListaEntrada(CRUDEntradas.obtenerTodasEntradas()))

        //SI NO HAY ITMES, NOTIFICAMOS TAMBIÉN AL USUARIO
        if(itemCount==0)
        {
            contenedorPadre?.let{it.listaDiccionario.setVisibility(View.GONE)}
            contenedorPadre?.let{it.layout_no_almacen.setVisibility(View.VISIBLE)}

        }

        //MUESTRA EL SNACKBAR AL BORRAR UN ELEMENTO
        muestraSnack(position, objetoRecovery)

        //ESCONDEMOS EL TECLADO CUANDO SE MUESTRA EL SNACKBAR
        val imm = contenedorPadre.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(contenedorPadre.listaDiccionario.getWindowToken(), 0)

        //PRINT DE CONTROL
        println("LISLOC AFTERresultante: "+ listaItems)
        println("LISIT AFTERresultante: "+ items)
        println("LISLBD AFTERresultante: "+ CRUDEntradas.recorrerListaEntrada(CRUDEntradas.obtenerTodasEntradas()))
    }


    fun muestraSnack(position: Int, objetoRecovery:Entrada)
    {
        //ESTE SNACKBAR CANCELA EL BORRADO, REINSERTANDO DE NUEVO EL OBJETO EN LA BASE DE DATOS
        //LA FORMA DE BORRADO QUE HEMOS USADO NOS OBLIGA A REINSERTAR DE NUEVO EL OBJETO CON LOS DATOS QUE TENEMOS
        //BORRAR EN LA LISTA DEL RECYCLER ANTES QUE EN LA BASE DE DATOS NOS DABA PROBLEMAS
        Snackbar.make(contenedorPadre.listaDiccionario, R.string.borrado, 3000)
            .setAction(R.string.cancelar, {
                    _ -> CRUDEntradas.nuevaOActualizarEntrada(objetoRecovery)
                this.listaItems= CRUDEntradas.obtenerTodasEntradas()
                notifyItemInserted(position)
                notifyItemChanged(position)

                //PRINT DE CONTROL
                println("LISLOC AFTERreinserccion: "+ listaItems)
                println("LISIT AFTERreinserccion: "+ items)
                println("LISLBD AFTERreinserccion: "+ CRUDEntradas.recorrerListaEntrada(CRUDEntradas.obtenerTodasEntradas()))


                    contenedorPadre?.let { it.listaDiccionario.setVisibility(View.VISIBLE) }

            }).show()


    }

    //CLASE DE LAS VISTAS DEL LAYOUT UTILIZADO PARA ESTE ADAPTADOR
    class ViewHolderDatosDic(itemView: View): RecyclerView.ViewHolder(itemView) {

        val palabra=itemView.textView_palabra_ingles
        val traduccion=itemView.textView_traduccion
        val circuloColor=itemView.probabilidad
        val mediaAudio=itemView.btn_audio

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
    //AÑADIMOS LA LISTA FILTRADA A LA LISTA PRINCIPAL Y NOTIFICAMOS LOS CAMBIOS
    fun actualizaLista(listanueva :List<Entrada>)
    {
        listaItems= listanueva
        notifyDataSetChanged()
    }


    //UTILIZAMOS LA LISTA DEVUELTA POR ESTE MÉTODO, PARA FILTRAR EN EL BUSCADOR
    fun obtenerListaCompleta():List<Entrada>
    {
        return CRUDEntradas.obtenerTodasEntradas()
    }


}