package morajavier.pdm.voclearn.Vistas

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_conj_entra_.*
import kotlinx.android.synthetic.main.barra_atras.*
import kotlinx.android.synthetic.main.layout_add_conjuntos.*
import kotlinx.android.synthetic.main.nuevo_grupo.view.*
import morajavier.pdm.voclearn.*
import morajavier.pdm.voclearn.Adapter.AdapterConjuntos
import morajavier.pdm.voclearn.BaseDatos.CRUDConjuntos
import morajavier.pdm.voclearn.BaseDatos.CRUDGrupo
import morajavier.pdm.voclearn.Modelo.Conjunto
import morajavier.pdm.voclearn.Modelo.Grupo


class Conj_entra_Activity : AppCompatActivity() {

    private lateinit var adaptadorCon: AdapterConjuntos
    lateinit var carpeta:Any
    //private lateinit var adaptadorEntr: AdapterEntradas


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conj_entra_)

        var tipoDato=intent.getStringExtra("tipo")
        obtenerConjuntoOGrupo(tipoDato)
        actualizarRecycleConjuntos()


        btn_nuevo_conj.setOnClickListener{

            alertaNuevoEditaConjunto(it, -1, null )
        }

        btn_atras.setOnClickListener{
            finish()
        }

    }

    //DEPENDIENDO DE SI VIENE DE UN GRUPO O DE UN CONJUNTO DE UN GRUPO, REALIZA UNA MANERA DE EXTRAER LA LISTA
    //DE CONNJUNTOS, U OTRA, AL SER DIFERENTES TIPOS DE OBJETOS
    //Y EL TEXTO DEL NOMBRE DE EL CONJUNTO O GRUPO TAMBIÉN SE ESTABLECE
    fun obtenerConjuntoOGrupo(tipo:String)
    {

        when(tipo){
            "conjunto"->{
                val idConjunto=intent.getIntExtra("idConjunto", -1)
                carpeta=CRUDConjuntos.obtenerConjunto(idConjunto)as Conjunto
                adaptadorCon= (carpeta as Conjunto)?.listaConjuntos?.let { AdapterConjuntos(it,this)}!!
                texto_nombre_carpeta.setText((carpeta as Conjunto)?.nombreConjunto)
            }
            "grupo"->{
                val nombreGrupo=intent.getStringExtra("nombreGrupo")
                carpeta= CRUDGrupo.obtenerGrupoPorNombre(nombreGrupo)as Grupo
                adaptadorCon= (carpeta as Grupo)?.listaConjuntos?.let { AdapterConjuntos(it,this)}!!
                texto_nombre_carpeta.setText((carpeta as Grupo)?.nombreGrupo)

            }
        }

    }

    fun actualizarRecycleConjuntos()
    {
        listaConjuntos.layoutManager=LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true)
        listaConjuntos.adapter=adaptadorCon
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
                        conjuntoEdit?.let { CRUDConjuntos.borrarConjuntoId(conjuntoEdit.idConjunto) }


                    }
                    else {
                        val nombreNuevo = vistaDialogo.nombre_carp_nuevo.text.toString()


                        //SI EL MÉTODO SE HA LLAMADO PARA EDITAR, COMO INDICA LA VARIABLE carpetaEditable,
                        //SE MODIFICA EL NOMBRE, EN CASO CONTRARIO SE INSERTA
                        if(carpetaEditable) {

                            //EDITAR EL NOMBRE DE LA CARPETA
                        }
                        else {

                            val nuevoConjunto=Conjunto(CRUDConjuntos.nuevoId()!!, nombreNuevo)

                            if(carpeta is Grupo )
                            {
                                (carpeta as Grupo)?.let{CRUDGrupo.insertarConjuntoEnGrupo(it,nuevoConjunto)}
                                adaptadorCon.items=(carpeta as Grupo)?.listaConjuntos!!
                                adaptadorCon.notifyItemInserted(adaptadorCon.itemCount-1)
                                adaptadorCon.notifyItemChanged(adaptadorCon.itemCount-1)

                            }
                            else if( carpeta is Conjunto)
                            {
                                (carpeta as Conjunto)?.let{CRUDConjuntos.insertarConjuntoEnConjuntos(it,nuevoConjunto)}
                                adaptadorCon.items= (carpeta as Conjunto)?.listaConjuntos!!
                                adaptadorCon.notifyItemInserted(adaptadorCon.itemCount-1)
                                adaptadorCon.notifyItemChanged(adaptadorCon.itemCount-1)
                            }



                        }

                    }

                })
            .setNegativeButton(R.string.cancelar,
                { dialogInterface: DialogInterface, i: Int ->

                    dialogInterface.dismiss()

                })
            .create().show()


    }



}
