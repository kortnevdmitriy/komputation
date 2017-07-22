package shape.komputation.cpu.functions

import shape.komputation.matrix.FloatMatrix

fun stackRows(numberColumns : Int, vararg matrices : FloatMatrix): FloatMatrix {

    val totalNumberRows = totalNumberRows(*matrices)

    val stack = FloatArray(totalNumberRows * numberColumns)

    var startAtRow = 0
    for(indexMatrix in 0..matrices.size - 1) {

        val matrix = matrices[indexMatrix]
        val matrixEntries = matrix.entries
        val numberMatrixRows = matrix.numberRows

        for (indexMatrixColumn in 0..numberColumns - 1) {

            val startAtIndex = indexMatrixColumn * totalNumberRows

            for (indexMatrixRow in 0..numberMatrixRows - 1) {

                stack[startAtIndex + startAtRow + indexMatrixRow] = matrixEntries[indexMatrixColumn * numberMatrixRows + indexMatrixRow]

            }

        }

        startAtRow += numberMatrixRows

    }

    return FloatMatrix(totalNumberRows, numberColumns, stack)

}

fun totalNumberRows(vararg matrices : FloatMatrix): Int {

    var totalNumberRows = 0

    for (matrix in matrices) {
        totalNumberRows += matrix.numberRows
    }

    return totalNumberRows

}

