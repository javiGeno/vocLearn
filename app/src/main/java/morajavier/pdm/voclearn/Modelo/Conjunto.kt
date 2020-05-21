package morajavier.pdm.voclearn.Modelo

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.LinkingObjects
import io.realm.annotations.PrimaryKey
import java.util.*

open class Conjunto (
    @PrimaryKey
    var idConjunto:Int=0,
    var nombreConjunto :String="",
    var listaPalabras : RealmList<Entrada>?= RealmList(),
    var listaConjuntos : RealmList<Conjunto>?=RealmList(),
    var fechaCreacion : Date = Date()
    ): RealmObject(){
    @LinkingObjects("listaConjuntos")
    val fkGrupo: RealmResults<Grupo>? = null
    @LinkingObjects("listaConjuntos")
    val fkConjunto: RealmResults<Conjunto>? = null
}
