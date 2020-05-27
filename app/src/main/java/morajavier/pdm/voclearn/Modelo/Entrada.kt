package morajavier.pdm.voclearn.Modelo

import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.*
import java.util.*

open class Entrada (
    @PrimaryKey var idEntrada : Int=0,
    var significado: String="",
    var descripcion: String?=null,
    var probAcierto:Int=0,
    var escrituraIngles:String?=null,
    var imagen: String?=null,
    var audio: String?=null,
    var fechaCreacion : Date= Date()


):RealmObject(){
    @LinkingObjects("listaPalabras")
    val fkConjunto: RealmResults<Conjunto>? = null
    @LinkingObjects("palabras")
    val fkGrupo: RealmResults<Grupo>? = null

}