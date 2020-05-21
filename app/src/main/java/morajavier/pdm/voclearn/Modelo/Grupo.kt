package morajavier.pdm.voclearn.Modelo

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.*
import java.util.*

open class Grupo (

    @PrimaryKey
    var nombreGrupo :String="",
    var palabras :RealmList<Entrada>?=RealmList(),
    var listaConjuntos:RealmList<Conjunto>?=RealmList(),
    var fechaCreacion : Date =Date()
): RealmObject()

