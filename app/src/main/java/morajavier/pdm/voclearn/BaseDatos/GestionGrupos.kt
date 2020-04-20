package morajavier.pdm.voclearn.BaseDatos

import android.util.Log
import morajavier.pdm.voclearn.App
import morajavier.pdm.voclearn.Modelo.Entrada
import morajavier.pdm.voclearn.Modelo.Grupo

class GestionGrupos {

    companion object{
        fun nuevoOActualizaGrupo(nuevo: Grupo)
        {
            //INSERTA O ACTUALIZA EN SEGUNDO PLANO UN NUEVO GRUPO
            App.gestorBD.r.executeTransaction({
                it.insertOrUpdate( nuevo)
            })
            /*   {Log.i("SUCCESS NUEVO GRUPO", "Un nuevo grupo ha sido introducido o actualizado satifactoriamente")},
               {error-> Log.e("ERROR NUEVO GRUPO", error.message)})*/

        }

        fun obtenerTodosLosGrupos(grupoPadre :String): List<Grupo>
        {
            return App.gestorBD.r.where(Grupo::class.java).findAll().sort("fechaCreacion").toList()
        }

        //OBTIENE TODOS LOS GRUPOS DIFERENTES AL PASADO POR PARAMETROS
        fun obtenerTodosLosGruposDistintos(grupoPadre :String): List<Grupo>
        {
            return App.gestorBD.r.where(Grupo::class.java).notEqualTo("nombreGrupo", grupoPadre).findAll().toList()
        }

        //AL BORRAR UN GRUPO SE REALIZA UN BORRADO EN CASCADA, YA QUE REALM AÚN NO CONTEMPLA ESA POSIBILIDAD
        //LO PRIMERO QUE HACE ES OBTENER TODOS LOS GRUPOS DISTINTOS AL QUE SE QUIERE BORRAR
        //ANTES DE BORRARLO DE LA BASE DE DATOS, SE BORRA LAS POSIBLES COPIAS QUE HAYA DE ÉL EN  LAS DIFERENTES LISTAS DE LOS GRUPOS
        fun borrarUnGrupo(borrarGrupo : Grupo)
        {
            var listaTotalGrupos= obtenerTodosLosGruposDistintos(borrarGrupo.nombreGrupo)

            borradoEnCascada(listaTotalGrupos, borrarGrupo)

            App.gestorBD.r.beginTransaction()
            borrarGrupo.deleteFromRealm()
            App.gestorBD.r.commitTransaction()
        }

        //RECORRE TODA LA LISTA DE GRUPOS DISTINTOS A ÉL, Y SI LA LISTA DE GRUPOS QUE TIENE ESTA LLENA,
        //COMPRUEBA SI HAY ALGUNO CON CON EL MISMO NOMBRE DEL QUE SE QUIERE ELIMINAR, Y SE INCLUYE A UNA LISTA "mismoGrupos"
        //LA LISTA "distintoGrupos" SE LLENARAN CON LOS QUE NO COINCIDEN CON EL QUE SE QUIERE ELIMINAR, Y SE VOLVERA A RECORRER
        //BUSCANDO OTRA POSIBLE COPIA.
        fun borradoEnCascada(listaTotalGrupos:List<Grupo>, grupoAQuitar:Grupo)
        {
            for(grupo in listaTotalGrupos)
            {
                if(!grupo.listaGrupos!!.isEmpty())
                {
                    //probar filtro doble de listas
                    val (mismoGrupos, distintoGrupos)= grupo.listaGrupos!!.partition { it.nombreGrupo==grupoAQuitar.nombreGrupo}

                    for(gruDelet in mismoGrupos)
                        borrarUnGrupoDeUnGrupo(grupo.nombreGrupo, gruDelet.nombreGrupo )

                    borradoEnCascada(distintoGrupos, grupoAQuitar)
                }
            }
        }

        fun obtenerGrupoPorNombre(nombreGrupo:String): Grupo? {

            return  App.gestorBD.r.where(Grupo::class.java)
                .equalTo("nombreGrupo", nombreGrupo)
                .findFirst()
        }

        //EL MÉTODO COMPRUEBA SI HAY ALGÚN GRUPO CON EL NUEVO NOMBRE, PARA NO MACHACARLO EN ESE CASO
        //SI LO QUE DEVUELVE EL MÉTODO DE BÚSQUEDA ES NULO ENTONCES SE REALIZA LA OPERACIÓN DE MODIFICACIÓN
        //DEL NUEVO NOMBRE (ID)
        /*fun modificarNombreGrupo(grupoAntiguo : String,nuevoNombre:String)
        {
            App.gestorBD.r.executeTransaction {
                if (obtenerGrupoPorNombre(nuevoNombre) == null)
                    obtenerGrupoPorNombre(grupoAntiguo)?.let {
                        val nuevoObjeto = Grupo(nuevoNombre, it.palabras, it.listaGrupos)
                        App.gestorBD.r.copyFromRealm(nuevoObjeto)
                        nuevoOActualizaGrupo(it)
                        println("******" + it.nombreGrupo)
                    }
            }

//asldfkhaskdjfhsalfikhdsalkdshflñsk.jf//
            val teamRealmObj = realm.where(Team::class.java)?.equalTo("name", oldTeamName)?.findFirst()
            val newTeamObj = realm.copyFromRealm(teamRealmObj)
            newTeamObj?.name = newTeamName
            realm.copyToRealmOrUpdate(newTeamObj)
            teamRealmObj?.deleteFromRealm()
        }*/



        fun insertarEntradaEnGrupo(nombreGrupo:String, idEntrada:Int) {

            //INSERTA UNA PALABRA EN UN GRUPO CREADO, SI LA LISTA DE PALABRAS NO ES NULA,
            //BUSCA UN GRUPO  Y LE INSERTA UNA PALABRA A SU LISTA
            //OBTENEMOS EL GRUPO Y SI NO ES NULO LO ACTUALIZAMOS CON LOS CAMBIOS REALIZADOS
            App.gestorBD.r.where(Grupo::class.java)
                .equalTo("nombreGrupo", nombreGrupo)
                .findFirst()?.let{it.palabras?.let {
                                App.gestorBD.r.beginTransaction()
                                it.add(GestionEntradas.obtenerEntradaPorId(idEntrada))
                                obtenerGrupoPorNombre(nombreGrupo)?.let{App.gestorBD.r.insertOrUpdate(it)}
                                App.gestorBD.r.commitTransaction()
                            }}
                            ?:Log.e("ERROR ", "El grupo "+nombreGrupo+" no existe")


        }



        fun insertarGrupoEnGrupo(nombreGrupoPadre:String, nombreGrupoHijo:String ){

            //INSERTA UN GRUPO EN UN GRUPO CREADO, SI LA LISTA DE GRUPO NO ES NULA,
            //BUSCA UN GRUPO  Y LE INSERTA UN GRUPO A SU LISTA
            //OBTENEMOS EL GRUPO Y SI NO ES NULO LO ACTUALIZAMOS CON LOS CAMBIOS REALIZADOS
            App.gestorBD.r.where(Grupo::class.java)
            .equalTo("nombreGrupo", nombreGrupoPadre)
            .findFirst()?.let{
                                App.gestorBD.r.beginTransaction()
                                it.listaGrupos?.let { it.add(obtenerGrupoPorNombre(nombreGrupoHijo))
                                obtenerGrupoPorNombre(nombreGrupoPadre)?.let{App.gestorBD.r.insertOrUpdate(it)}
                                App.gestorBD.r.commitTransaction()
                            }}
                        ?:Log.e("ERROR ", "El grupo "+nombreGrupoPadre+" no existe")


        }

        fun insertarEntradaEnGrupo(nombreGrupo:String, entrada: Entrada) {

            //INSERTA UNA PALABRA EN UN GRUPO CREADO, SI LA LISTA DE PALABRAS NO ES NULA,
            //BUSCA UN GRUPO  Y LE INSERTA UNA PALABRA A SU LISTA
            //OBTENEMOS EL GRUPO Y SI NO ES NULO LO ACTUALIZAMOS CON LOS CAMBIOS REALIZADOS
            App.gestorBD.r.where(Grupo::class.java)
                .equalTo("nombreGrupo", nombreGrupo)
                .findFirst()?.let{it.palabras?.let {
                    App.gestorBD.r.beginTransaction()
                    it.add(entrada)
                    obtenerGrupoPorNombre(nombreGrupo)?.let{App.gestorBD.r.insertOrUpdate(it)}
                    App.gestorBD.r.commitTransaction()
                }}
                ?: Log.e("ERR INSERT LIST ENTRA ", "El grupo "+nombreGrupo+" no existe")


        }



        fun insertarGrupoEnGrupo(nombreGrupoPadre:String, GrupoHijo: Grupo){

            //INSERTA UN GRUPO EN UN GRUPO CREADO, SI LA LISTA DE GRUPO NO ES NULA,
            //BUSCA UN GRUPO  Y LE INSERTA UN GRUPO A SU LISTA
            //OBTENEMOS EL GRUPO Y SI NO ES NULO LO ACTUALIZAMOS CON LOS CAMBIOS REALIZADOS
            App.gestorBD.r.where(Grupo::class.java)
                .equalTo("nombreGrupo", nombreGrupoPadre)
                .findFirst()?.let{
                    App.gestorBD.r.beginTransaction()
                    it.listaGrupos?.let { it.add(GrupoHijo)
                    obtenerGrupoPorNombre(nombreGrupoPadre)?.let{App.gestorBD.r.insertOrUpdate(it)}
                    App.gestorBD.r.commitTransaction()
                }}
                ?: Log.e("ERR INSERT LIST GRUPO ", "El grupo "+nombreGrupoPadre+" no existe")


        }

        fun borrarUnaPalabraDeGrupo(nombreGrupo:String, idEntrada:Int)
        {
            //BORRA UNA PALABRA EN UN GRUPO CREADO, SI LA LISTA DE PALABRAS NO ES NULA,
            //BUSCA UN GRUPO Y LE BORRA LA REFERENCIA A ESA PALABRA DE SU LISTA
            App.gestorBD.r.where(Grupo::class.java)
                .equalTo("nombreGrupo", nombreGrupo)
                .findFirst()?.let{
                    App.gestorBD.r.beginTransaction()
                    it.palabras?.let { it.remove(GestionEntradas.obtenerEntradaPorId(idEntrada))
                    obtenerGrupoPorNombre(nombreGrupo)?.let{App.gestorBD.r.insertOrUpdate(it)}
                    App.gestorBD.r.commitTransaction()
                    }}?: Log.e("ERR QUITAR LIST ENTRA ", "El grupo "+nombreGrupo+" no existe")
        }

        fun borrarUnGrupoDeUnGrupo(nombreGrupoPadre:String, nombreGrupoHijo:String )
        {
            //BORRA UN GRUPO EN UN GRUPO CREADO, SI LA LISTA DE GRUPO NO ES NULA,
            //BUSCA UN GRUPO Y LE BORRRA UN GRUPO DE SU LISTA
            App.gestorBD.r.where(Grupo::class.java)
                .equalTo("nombreGrupo", nombreGrupoPadre)
                .findFirst()?.let{
                    App.gestorBD.r.beginTransaction()
                    it.listaGrupos?.let { it.remove(obtenerGrupoPorNombre(nombreGrupoHijo))
                    obtenerGrupoPorNombre(nombreGrupoPadre)?.let{App.gestorBD.r.insertOrUpdate(it)}
                    App.gestorBD.r.commitTransaction()
                    }}?: Log.e("ERR QUITAR LIST GRUPO", "El grupo "+nombreGrupoPadre+" no existe")

        }

        //RECORRE UNA LISTA DE GRUPOS SI ESTA LLENA, YA QUE AL INSTANCIARSE SE HACE COMO UNA LISTA VACÍA
        //EN SEGUNDO LUGAR, RECORRE LA LISTA DE PALABRAS
        //POR ÚLTIMO RECORRE LA LISTA DE GRUPOS QUE TENGA, VOLVIENDO LLAMAR A ESTE MÉTODO
        fun recorrerListaGrupo(lista : List<Grupo>)
        {

            Log.i("COUNT LIST GrupoGrupo", ""+lista?.count() + " grupos hay en esta Lista")

            for(i in lista?.let{it}){

                if(!lista.isEmpty()) {

                    i.palabras?.let { GestionEntradas.recorrerListaEntrada(it) }

                    i.listaGrupos?.let{recorrerListaGrupo(it)}

                    Log.i(
                        "" + i.nombreGrupo, "nº Grupos "+i.listaGrupos?.let{it.count()} +" nº Palabras "+i.palabras?.count()
                    )
                }
            }
        }
    }
}