package com.komputation.cpu.demos.total

import com.komputation.cpu.network.Network
import com.komputation.demos.total.TotalData
import com.komputation.initialization.zeroInitialization
import com.komputation.layers.entry.inputLayer
import com.komputation.layers.forward.activation.ActivationFunction
import com.komputation.layers.recurrent.ResultExtraction
import com.komputation.layers.recurrent.recurrentLayer
import com.komputation.loss.printLoss
import com.komputation.loss.squaredLoss
import com.komputation.optimization.stochasticGradientDescent
import java.util.*

fun main(args: Array<String>) {

    val random = Random(1)

    val initialization = zeroInitialization()
    val optimization = stochasticGradientDescent(0.001f)

    val minimumLength = 2
    val maximumLength = 5

    val input = TotalData.generateVariableLengthInput(random, minimumLength, maximumLength, 0, 10, 10_000)
    val targets = TotalData.generateTargets(input)

    val hasFixedLength = false

    val network = Network(
        1,
        inputLayer(1, maximumLength),
        recurrentLayer(maximumLength, hasFixedLength, 1, 1, ResultExtraction.LastStep, initialization, null, ActivationFunction.Identity, optimization)
    )
    network
        .training(
            input,
            targets,
            2,
            squaredLoss(1, maximumLength, hasFixedLength),
            printLoss
        )
        .run()

}