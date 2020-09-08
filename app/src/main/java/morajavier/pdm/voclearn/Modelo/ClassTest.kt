package morajavier.pdm.voclearn.Modelo

class ClassTest(listaPalabras:MutableList<Entrada>) {

    var listaTest=listaPalabras
    var listaMasFallos = IntArray(listaTest.size)
    var numeroAcertadas=0
    var numeroFallidas=0
    lateinit var entradaPregunta:Entrada
    var ultimaMostrada=""

    //CONSTANTES QUE EN EL NÚMERO ALEATORIO SALDRÁN, INDICANDO SI AL USUARIO SE LE PREGUNTA POR UNA IMAGEN
    //O POR UNA PALABRA
    companion object{
        const val IMAGEN=100
        const val PALABRA=101
        const val PROBABILIDADROJA=1
        const val PROBABILIDADAMARILLA=2
        const val PROBABILIDADVERDE=3
        const val MUCHOSFALLOS=3
        const val POCOSFALLOS=2

    }

    fun añadirFalloAPalabra(entrada:Entrada)
    {
        var posicion=listaTest.indexOf(entrada)
        println("POSICION ADD FALLO "+posicion)
        listaMasFallos[posicion]++

        println("LISTA FALLOS "+listaMasFallos)
        println("LIST ENTRADA FALLOS "+listaTest)

    }

    fun generacionNumeroAleatorio(desde:Int, hasta:Int):Int
    {
        val rnds = (desde..hasta).random()

        return rnds
    }


    fun obtenerPalabra(lista:MutableList<Entrada>):Entrada
    {
        var entradaObtenida:Entrada?=null
        var porcentaje=generacionNumeroAleatorio(1,100)
        //SI HA SALIDO UNA ROJA
        if(porcentaje>25)
        {
            var listaPalabrasDificiles=lista.filter{it.probAcierto == PROBABILIDADROJA}
            if(listaPalabrasDificiles.isNotEmpty())
            {
                var index=generacionNumeroAleatorio(0,listaPalabrasDificiles.size-1)
                var p=listaPalabrasDificiles[index]
                println("Devuelve "+p.escrituraIngles)
                println("Lista filtro rojo "+listaPalabrasDificiles)
                return p
            }
            else
                return obtenerPalabra(lista)
        }
        //SI HA SALIDO UNA AMARILLA
        if(porcentaje>10 && porcentaje<=25)
        {
            var listaPalabraMedias=lista.filter{it.probAcierto == PROBABILIDADAMARILLA}
            if(listaPalabraMedias.isNotEmpty())
            {
                var index=generacionNumeroAleatorio(0,listaPalabraMedias.size-1)
                var p=listaPalabraMedias[index]
                println("Devuelve "+p.escrituraIngles)
                println("Lista filtro amarillo "+listaPalabraMedias)
                return p
            }
            else
                return obtenerPalabra(lista)
        }
        //SI HA SALIDO UNA VERDE
        if(porcentaje<=10)
        {

            var listaPalabrasFaciles=lista.filter{it.probAcierto == PROBABILIDADVERDE}
            if(listaPalabrasFaciles.isNotEmpty())
            {
                var index=generacionNumeroAleatorio(0,listaPalabrasFaciles.size-1)
                var p=listaPalabrasFaciles[index]
                println("Devuelve "+p.escrituraIngles)
                println("Lista filtro verde "+listaPalabrasFaciles)
                return p
            }
            else
                return obtenerPalabra(lista)
        }

        return entradaObtenida!!
    }

    fun obtenerLaPalabraMasFallida(): MutableList<Entrada>{

        var mayorFallos=0
        //POSICIONES QUE UTILIZAREMOS PARA BUSCAR LAS ENTRADAS QUE DEVOLVEREMOS
        //LAS MÁS FALLIDAS
        var palabrasMasFallos= mutableListOf<Entrada>()
        //POSICIÓN listaMasFallos
        var posiciones=0

        for(i in listaMasFallos)
        {
            if(i>=mayorFallos && i!=0) {
                mayorFallos = i
                palabrasMasFallos.add(listaTest.get(posiciones))
            }

            posiciones++
        }

        return palabrasMasFallos
    }

    fun masDeUnaConElMismoSignificado(pregunta:String):Boolean
    {
        //BUSCAMOS LAS PALABRAS QUE PUEDAN REPETIRSE SU SIGNIFICADO, POR EJEMPLO "actualizar" SE ESCRIBE "update" Y "actualize"
        var listaPalabrasIguales=listaTest.filter{it.significado.toLowerCase().trim() == pregunta.toLowerCase().trim()}

        if(listaPalabrasIguales.size>1)
            return true
        else
            return false
    }

}