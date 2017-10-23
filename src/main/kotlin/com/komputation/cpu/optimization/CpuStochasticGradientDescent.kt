package com.komputation.cpu.optimization

class CpuStochasticGradientDescent(private val learningRate: Float) : UpdateRule {

    override fun updateSparsely(start : Int, parameters: FloatArray, gradient: FloatArray, numberEntries: Int) {

        for(index in 0 until numberEntries) {

            parameters[index] -= this.learningRate * gradient[index]

        }

    }

}