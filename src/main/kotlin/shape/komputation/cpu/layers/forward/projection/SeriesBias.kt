package shape.komputation.cpu.layers.forward.projection

import shape.komputation.cpu.functions.add
import shape.komputation.cpu.functions.backwardProjectionWrtBias
import shape.komputation.cpu.optimization.DenseAccumulator
import shape.komputation.cpu.optimization.UpdateRule
import shape.komputation.cpu.optimization.updateDensely
import shape.komputation.initialization.InitializationStrategy
import shape.komputation.initialization.initializeColumnVector
import shape.komputation.matrix.FloatMatrix
import shape.komputation.optimization.OptimizationInstruction

class SeriesBias internal constructor(
    private val name : String?,
    private val bias: FloatArray,
    private val seriesAccumulator: DenseAccumulator,
    private val batchAccumulator: DenseAccumulator,
    private val updateRule: UpdateRule? = null) {

    fun forwardStep(input : FloatMatrix) =

        FloatMatrix(input.numberRows, input.numberColumns, add(input.entries, bias))

    fun backwardStep(chain: FloatMatrix) {

        val backwardWrtBias = backwardProjectionWrtBias(this.bias.size, chain.entries, chain.numberRows, chain.numberColumns)

        this.seriesAccumulator.accumulate(backwardWrtBias)

    }

    fun backwardSeries() {

        val seriesAccumulator = this.seriesAccumulator

        this.batchAccumulator.accumulate(seriesAccumulator.getAccumulation())

        seriesAccumulator.reset()

    }

    fun optimize(scalingFactor : Float) {

        if (this.updateRule != null) {

            updateDensely(this.bias, this.batchAccumulator.getAccumulation(), scalingFactor, this.updateRule)

        }

        this.batchAccumulator.reset()

    }

}

fun seriesBias(
    dimension: Int,
    initializationStrategy: InitializationStrategy,
    optimizationStrategy: OptimizationInstruction?) =

    seriesBias(null, dimension, initializationStrategy, optimizationStrategy)

fun seriesBias(
    name : String?,
    dimension: Int,
    initializationStrategy: InitializationStrategy,
    optimizationStrategy: OptimizationInstruction?) : SeriesBias {

    val bias = initializeColumnVector(initializationStrategy, dimension)

    val seriesAccumulator = DenseAccumulator(dimension)
    val batchAccumulator = DenseAccumulator(dimension)

    val updateRule = optimizationStrategy?.buildForCpu()?.invoke(dimension, 1)

    return SeriesBias(name, bias, seriesAccumulator, batchAccumulator, updateRule)

}