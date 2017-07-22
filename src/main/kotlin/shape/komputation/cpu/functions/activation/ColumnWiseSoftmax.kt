package shape.komputation.cpu.functions.activation

import shape.komputation.matrix.FloatMath
import java.util.*

fun columnWiseSoftmax(input: FloatArray, numberRows : Int, numberColumns : Int): FloatArray {

    val results = FloatArray(input.size)

    for (indexColumn in 0..numberColumns - 1) {

        val start = indexColumn * numberRows

        var sum = 0.0f
        for (indexRow in 0..numberRows - 1) {

            val indexEntry = start + indexRow

            val exponentiation = FloatMath.exp(input[indexEntry])
            sum += exponentiation

            results[indexEntry] = exponentiation

        }

        for (indexRow in 0..numberRows - 1) {

            results[start + indexRow] /= sum

        }

    }

    return results

}

fun backwardColumnWiseSoftmax(numberForwardRows: Int, numberForwardColumns: Int, forwardEntries: FloatArray, chainEntries: FloatArray): FloatArray {

    val gradient = FloatArray(numberForwardRows * numberForwardColumns)

    for (indexColumn in 0..numberForwardColumns - 1) {

        val start = indexColumn * numberForwardRows
        val end = start + numberForwardRows

        val forwardColumn = Arrays.copyOfRange(forwardEntries, start, end)
        val chainColumn = Arrays.copyOfRange(chainEntries, start, end)

        for (outerIndexRow in 0..numberForwardRows - 1) {

            var derivative = 0.0f

            val prediction = forwardColumn[outerIndexRow]

            for (innerIndexRow in 0..numberForwardRows - 1) {

                val chainEntry = chainColumn[innerIndexRow]

                // i == j
                if (outerIndexRow == innerIndexRow) {

                    derivative += chainEntry * prediction * (1 - prediction)

                }
                // i != j
                else {

                    val otherPrediction = forwardColumn[innerIndexRow]

                    derivative += chainEntry * (-prediction * otherPrediction)

                }

            }

            gradient[start + outerIndexRow] = derivative

        }

    }

    return gradient
}
