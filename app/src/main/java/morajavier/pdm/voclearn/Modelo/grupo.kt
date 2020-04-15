package morajavier.pdm.voclearn.Modelo

import io.realm.RealmList
import io.realm.annotations.*
import java.util.*

open class grupo (

    @PrimaryKey var idGrupo : Int=0,
    var nombreGrupo :String="",
    var palabras :RealmList<Entrada>?=RealmList(),
    var listaGrupos:RealmList<grupo>?=null,
    var fechaCreacion : Date =Date()
)

