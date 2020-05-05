package morajavier.pdm.voclearn.Adapter

import android.media.MediaPlayer
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_diccionario.view.*
import morajavier.pdm.voclearn.FuncionesExtension.cambioImagen
import morajavier.pdm.voclearn.Modelo.Entrada
import morajavier.pdm.voclearn.R
import java.io.IOException

//CREAMOS UNA CLASE ADAPTER, PARA LA LISTA DE ENTRADAS QUE INGRESE EL USUARIO
//IMPLEMENTAMOS LOS MÉTODOS QUE HACEN FALTA AL EXTENDER DE RecyclerView.Adapter<"clase creada interna que extiende de RecyclerView.ViewHolder">
class AdapterDiccionario(val items: List<Entrada>): RecyclerView.Adapter<AdapterDiccionario.ViewHolderDatosDic>() {


     var listaItems:List<Entrada> = items

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

            reproducirAudio.setOnCompletionListener {
                holder.mediaAudio.setBackgroundResource(android.R.drawable.ic_media_play)
            }

        }

        holder.

    }

    //CLASE DE LAS VISTAS DEL LAYOUT UTILIZADO PARA ESTE ADAPTADOR
    class ViewHolderDatosDic(itemView: View): RecyclerView.ViewHolder(itemView) {

        val palabra=itemView.textView_palabra_ingles
        val traduccion=itemView.textView_traduccion
        val circuloColor=itemView.probabilidad
        val mediaAudio=itemView.btn_audio
        val editar=itemView.btn
        val borrar=

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

        listaItems=listanueva
        notifyDataSetChanged()
    }


}