package com.sivara.locushoming

import com.bahadirarslan.jeodezi.Coordinate
import com.bahadirarslan.jeodezi.GreatCircle

object Bearing {

fun calculate(latitude : Double, longitude : Double , bearing : Double , vector_km : Double): Coordinate {
    val greatCircle = GreatCircle()
    return greatCircle.destination(Coordinate(latitude, longitude), vector_km, bearing)
}

}