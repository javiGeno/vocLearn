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
        if(porcentaje>30)
        {
            var l=lista.filter{it.probAcierto ==1}
            if(l.isNotEmpty())
            {
                var index=generacionNumeroAleatorio(0,l.size-1)
                var p=l[index]
                println("Devuelve "+p.escrituraIngles)
                println("Lista filtro rojo "+l)
                return p
            }
            else
                return obtenerPalabra(lista)
        }
        //SI HA SALIDO UNA AMARILLA
        if(porcentaje>11 && porcentaje<=30)
        {
            var l=lista.filter{it.probAcierto ==2}
            if(l.isNotEmpty())
            {
                var index=generacionNumeroAleatorio(0,l.size-1)
                var p=l[index]
                println("Devuelve "+p.escrituraIngles)
                println("Lista filtro amarillo "+l)
                return p
            }
            else
                return obtenerPalabra(lista)
        }
        //SI HA SALIDO UNA VERDE
        if(porcentaje<=11)
        {

            var l=lista.filter{it.probAcierto ==3}
            if(l.isNotEmpty())
            {
                var index=generacionNumeroAleatorio(0,l.size-1)
                var p=l[index]
                println("Devuelve "+p.escrituraIngles)
                println("Lista filtro verde "+l)
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

}