package morajavier.pdm.voclearn.Adapter


import android.content.Context
import android.media.MediaPlayer
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_dictionary.*
import kotlinx.android.synthetic.main.layout_diccionario.view.*
import morajavier.pdm.voclearn.BaseDatos.CRUDEntradas
import morajavier.pdm.voclearn.FuncionesExtension.cambioImagen
import morajavier.pdm.voclearn.Modelo.Entrada
import morajavier.pdm.voclearn.R
import morajavier.pdm.voclearn.Vistas.DictionaryFragment
import java.io.IOException

//CREAMOS UNA CLASE ADAPTER, PARA LA LISTA DE ENTRADAS QUE INGRESE EL USUARIO
//IMPLEMENTAMOS LOS MÉTODOS QUE HACEN FALTA AL EXTENDER DE RecyclerView.Adapter<"clase creada interna que extiende de RecyclerView.ViewHolder">
//RECIBE UN OBJETO ANY, COMO EL CONTENEDOR DE ESTE RECYCLER, QUE PUEDE SER UNA ACTIVITY, FRAGMENTS...ETC
class AdapterDiccionario(val items: List<Entrada>, contenedorPadre : Any): RecyclerView.Adapter<AdapterDiccionario.ViewHolderDatosDic>() {


     var listaItems = items
     var contenedorPadre=contenedorPadre as DictionaryFragment


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
        holder.palabra.text=entradaActual.escrituraIngles
        holder.traduccion.text=entradaActual.significado



        //SI LA ENTRADA DE AUDIO ES NULA O ES UNA CADENA VACÍA, HACEMOS DESAPARECER EL BOTON DEL PLAY,
        // POR LO CONTRARIO LO MOSTRAMOS
        entradaActual.audio?.let{
            if(!it.isEmpty()) {
                holder.mediaAudio.setVisibility(View.VISIBLE)
            }
            else
            {
                holder.mediaAudio.setVisibility( View.GONE)
            }
        }?:holder.mediaAudio.setVisibility(View.GONE)

        //CUANDO PINCHAMOS EN EL PLAY, PREPARAMOS EL AUDIO CORRESPONDIENTE A ESTA PALABRA, QUE SE ENCUENTRA EN LA
        //MEMORIA INTERNA DEL MÓVIL.
        holder.mediaAudio.setOnClickListener{
            val reproducirAudio=MediaPlayer()

            try {
                reproducirAudio.setDataSource(
                    Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_MUSIC
                    ).absolutePath + entradaActual.idEntrada + ".mp3"
                )

                //PREPARAMOS EL AUDIO
                reproducirAudio.prepare()

                //FUNCIÓN DE EXTENSIÓN QUE PONE EN PAUSA O EN PLAY SEGÚN SU ESTADO Y ADEMÁS
                //CAMBIA LA IMAGEN DEL BOTÓN REPRODUCCÍON
                it.cambioImagen(reproducirAudio)


            }
            catch (e: IOException){
                Log.e("ERROR AUDIO", "Ha ocurrido un error al reproducir el audio")

            }

            //CUANDO TERMINE DE REPRODUCIRSE EL AUDIO, CAMBIAMOS EL ICONO
            reproducirAudio.setOnCompletionListener {
                holder.mediaAudio.setBackgroundResource(android.R.drawable.ic_media_play)
            }

        }

        holder.borrar.setOnClickListener{

            borrarElemento(entradaActual, position)

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
            entradaActual.audio)
        CRUDEntradas.borrarEntradaId(entradaActual.idEntrada)
        this.listaItems= CRUDEntradas.obtenerTodasEntradas()
        notifyItemRemoved(position)

        //SI NO HAY ITMES, NOTIFICAMOS TAMBIÉN AL USUARIO
        if(itemCount==0)
        {
            contenedorPadre.listaDiccionario.visibility= View.GONE
            contenedorPadre.layout_no_almacen.visibility= View.VISIBLE

        }

        //ESTE SNACKBAR CANCELA EL BORRADO, REINSERTANDO DE NUEVO EL OBJETO EN LA BASE DE DATOS
        //LA FORMA DE BORRADO QUE HEMOS USADO NOS OBLIGA A REINSERTAR DE NUEVO EL OBJETO CON LOS DATOS QUE TENEMOS
        //BORRAR EN LA LISTA DEL RECYCLER ANTES QUE EN LA BASE DE DATOS NOS DABA PROBLEMAS
        Snackbar.make(contenedorPadre.listaDiccionario, R.string.borrado, 3000)
            .setAction(R.string.cancelar, {
                    _ -> CRUDEntradas.nuevaOActualizarEntrada(objetoRecovery)
                this.listaItems= CRUDEntradas.obtenerTodasEntradas()
                notifyItemInserted(position)
                contenedorPadre.listaDiccionario.visibility= View.VISIBLE
                contenedorPadre.layout_no_almacen.visibility= View.GONE
            }).show()

        //ESCONDEMOS EL TECLADO CUANDO SE MUESTRA EL SNACKBAR
        val imm = contenedorPadre.activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(contenedorPadre.listaDiccionario.getWindowToken(), 0)
    }



    //CLASE DE LAS VISTAS DEL LAYOUT UTILIZADO PARA ESTE ADAPTADOR
    class ViewHolderDatosDic(itemView: View): RecyclerView.ViewHolder(itemView) {

        val palabra=itemView.textView_palabra_ingles
        val traduccion=itemView.textView_traduccion
        val circuloColor=itemView.probabilidad
        val mediaAudio=itemView.btn_audio
        val editar=itemView.btn_editar
        val borrar=itemView.btn_borrar

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


}