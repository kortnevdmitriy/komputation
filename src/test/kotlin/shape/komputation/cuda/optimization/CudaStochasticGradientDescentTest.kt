package shape.komputation.cuda.optimization

import jcuda.Pointer
import jcuda.runtime.JCuda.cudaFree
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Test
import shape.komputation.cuda.getVector
import shape.komputation.cuda.setUpCudaContext
import shape.komputation.cuda.setVector

class CudaStochasticGradientDescentTest {

    @Test
    fun testOneDimension() {

        val cudaContext = setUpCudaContext()

        val numberRows = 1
        val numberColumns = 1
        val size = numberRows * numberColumns

        val stochasticGradientDescent = CudaStochasticGradientDescent(cudaContext.kernelFactory.stochasticGradientDescent(), cudaContext.maximumNumberThreadsPerBlock, numberRows * numberColumns, 0.1f)

        stochasticGradientDescent.acquire()

        val deviceParameter = Pointer()
        setVector(floatArrayOf(2.0f), size, deviceParameter)
        val deviceGradient = Pointer()
        setVector(floatArrayOf(0.1f), size, deviceGradient)

        stochasticGradientDescent.update(Pointer.to(deviceParameter), 1.0f, Pointer.to(deviceGradient))

        val hostParameter = getVector(deviceParameter, size)

        cudaFree(deviceParameter)
        cudaFree(deviceGradient)

        stochasticGradientDescent.release()

        cudaContext.destroy()

        assertArrayEquals(floatArrayOf(1.99f), hostParameter)

    }

}