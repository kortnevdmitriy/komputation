package shape.komputation.optimization.adaptive

import shape.komputation.optimization.UpdateRule

fun adagrad(learningRate: Double, epsilon: Double = 1e-6): (Int, Int) -> UpdateRule {

    return { numberRows : Int, numberColumns : Int ->

        Adagrad(learningRate, epsilon, numberRows * numberColumns)

    }

}

class Adagrad(private val learningRate: Double, private val epsilon : Double, size : Int) : UpdateRule {

    private val sumOfSquaredDerivatives = DoubleArray(size)

    override fun updateSparsely(start : Int, parameters: DoubleArray, gradient: DoubleArray, gradientSize : Int) {

        for(index in 0..gradientSize-1) {

            val historyIndex = start + index
            val derivative = gradient[index]

            this.updateHistory(historyIndex, derivative)

            val adaptiveLearningRate = this.adaptLearningRate(historyIndex)

            val update = -adaptiveLearningRate * derivative

            parameters[index] += update

        }

    }

    private fun updateHistory(historyIndex: Int, derivative: Double) {

        this.sumOfSquaredDerivatives[historyIndex] += Math.pow(derivative, 2.0)

    }

    private fun adaptLearningRate(historyIndex: Int) =

        this.learningRate / (Math.sqrt(this.sumOfSquaredDerivatives[historyIndex]) + this.epsilon)

}