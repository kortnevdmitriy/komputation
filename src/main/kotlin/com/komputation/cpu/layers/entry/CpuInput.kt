package com.komputation.cpu.layers.entry

import com.komputation.cpu.layers.BaseCpuEntryPoint
import com.komputation.matrix.FloatMatrix
import com.komputation.matrix.Matrix

class CpuInput internal constructor(name : String? = null) : BaseCpuEntryPoint(name) {

    override var numberOutputColumns = -1
    override var forwardResult = FloatArray(0)

    override fun forward(input: Matrix): FloatArray {
        input as FloatMatrix

        this.numberOutputColumns = input.numberColumns
        this.forwardResult = input.entries

        return this.forwardResult
    }

    override fun backward(chain : FloatArray) =
        chain

}