package com.komputation.cpu.optimization

interface UpdateRule {

    fun updateDensely(parameters : FloatArray, gradient : FloatArray, gradientSize : Int) {

        updateSparsely(0, parameters, gradient, gradientSize)

    }

    fun updateSparsely(start : Int, parameter: FloatArray, gradient : FloatArray, dimension: Int)

}