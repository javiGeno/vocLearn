package morajavier.pdm.voclearn.Adapter



import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_diccionario.view.*
import morajavier.pdm.voclearn.BaseDatos.CRUDConjuntos
import morajavier.pdm.voclearn.BaseDatos.CRUDEntradas
import morajavier.pdm.voclearn.FuncionesExtension.*
import morajavier.pdm.voclearn.Modelo.Conjunto
import morajavier.pdm.voclearn.Modelo.Entrada
import morajavier.pdm.voclearn.Modelo.Grupo
import morajavier.pdm.voclearn.R
import morajavier.pdm.voclearn.Modelo.Sonido
import morajavier.pdm.voclearn.Vistas.DetailActivity


//CREAMOS UNA CLASE ADAPTER, PARA LA LISTA DE ENTRADAS QUE INGRESE EL USUARIO
//IMPLEMENTAMOS LOS MÉTODOS QUE HACEN FALTA AL EXTENDER DE RecyclerView.Adapter<"clase creada interna que extiende de RecyclerView.ViewHolder">
//RECIBE LA REFERENCIA AL CONTENEDOR PADRE
class AdapterDiccionario(val items: List<Entrada>, contenedorPadre : FragmentActivity, val layout:Int): RecyclerView.Adapter<AdapterDiccionario.ViewHolderDatosDic>() {



    var listaItems = items
     var contenedorPadre=contenedorPadre


    //INFLAMOS EL LAYOUT CREADO PARA ESTE RECYCLER "layout_diccionario"
    //GUARDAMOS EN UNA VARIABLE QUE PASAREMOS A LA INSTANCIA DE NUESTRA CLASE VIEWHOLDER INTERNA
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderDatosDic {


        val vistaRecycle=LayoutInflater.from(parent.context).inflate(layout, null, false)


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

            println("RUTA IMAGEN ADAPTER " + it)
            holder.circuloColor.cargarImagenCircleNoCache(it, contenedorPadre.crearSpinnerCarga(5f, 30f),entradaActual.fondoImg())

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

        //SI EL LAYOUT ES IGUAL AL DE LAS ENTRADAS, QUIERE DECIR QUE PUEDE ARRASTRARSE LOS ITEMS, PARA CLASIFICARLOS
        //EN CARPETAS SI EL USUARIO LO CREE OPORTUNO
        if(layout==R.layout.layout_entradas){
            holder.itemView.click_detalle.setOnLongClickListener{

                //SOMBRA POR DEFECTO, QUE ES LA PROPIA VISTA.
                //MI IDEA ERA HACER LA SOMBRA IGUAL QUE LA VISTA PERO REDUCIDA PERO NO LO CONSEGUÍ
                val myShadow = View.DragShadowBuilder(it)


                //DRAG AND DROP PARA DIFERENTES VERSIONES
                //ENCONTRADO EN STACKOVERFLOW
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    it.startDragAndDrop( null,
                        myShadow,
                        arrayListOf(entradaActual,position, this),
                        0   )
                } else {
                    it.startDrag( null,
                        myShadow,
                        arrayListOf(entradaActual,position, this),
                        0   )
                }


            }
        }

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

    fun quitarEntradaLista(entradaAQuitar:Entrada, carpetaPadre:Any, posicionQuitado: Int)
    {
        if(carpetaPadre is Grupo)
        {
            CRUDConjuntos.quitarEntradaDeLista((carpetaPadre as Grupo).palabras!!, entradaAQuitar!!)
        }
        else if(carpetaPadre is Conjunto)
        {
            CRUDConjuntos.quitarEntradaDeLista((carpetaPadre as Conjunto).listaPalabras!!, entradaAQuitar!!)
        }

        notifyItemRemoved(posicionQuitado)
        notifyItemChanged(posicionQuitado)
    }


}