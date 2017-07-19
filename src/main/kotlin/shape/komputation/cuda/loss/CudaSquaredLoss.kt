package shape.komputation.cuda.loss

import jcuda.Pointer
import jcuda.Sizeof
import jcuda.runtime.JCuda.cudaFree
import shape.komputation.cuda.Kernel
import shape.komputation.cuda.allocateDeviceMemory
import shape.komputation.cuda.getVector
import shape.komputation.cuda.setVectorToZero
import shape.komputation.layers.Resourceful

class CudaSquaredLoss(private val forwardKernel: Kernel, private val backwardKernel: Kernel, private val targetDimension : Int) : CudaLossFunction, Resourceful {

    private val deviceForwardResults = Pointer()
    private val pointerToDeviceForwardResults = Pointer.to(this.deviceForwardResults)

    private val deviceBackwardResults = Pointer()
    private val pointerToDeviceBackwardResults = Pointer.to(this.deviceBackwardResults)

    private val deviceLoss = Pointer()
    private val pointerToDeviceSum = Pointer.to(this.deviceLoss)

    private val deviceTargetDimension = Pointer.to(intArrayOf(this.targetDimension))

    override fun acquire() {

        this.forwardKernel.acquire()

        allocateDeviceMemory(this.deviceForwardResults, this.targetDimension)
        allocateDeviceMemory(this.deviceLoss, 1)

        this.backwardKernel.acquire()

        allocateDeviceMemory(this.deviceBackwardResults, this.targetDimension)

    }

    override fun release() {

        cudaFree(this.deviceBackwardResults)

        this.backwardKernel.release()

        cudaFree(this.deviceLoss)
        cudaFree(this.deviceForwardResults)

        this.forwardKernel.release()

    }

    private val accumulationSharedMemoryBytes = this.targetDimension * Sizeof.DOUBLE

    override fun accumulate(pointerToPredictions: Pointer, pointerToTargets: Pointer) {

        val parameters = Pointer.to(
            this.deviceTargetDimension,
            pointerToPredictions,
            pointerToTargets,
            this.pointerToDeviceForwardResults,
            this.pointerToDeviceSum)

        this.forwardKernel.launch(
            parameters,
            1,
            this.targetDimension,
            this.accumulationSharedMemoryBytes)

    }

    override fun accessAccumulation() =

        getVector(this.deviceLoss, 1)[0]

    override fun reset() {

        setVectorToZero(this.deviceLoss, 1)

    }

    override fun backward(pointerToPredictions: Pointer, pointerToTargets: Pointer): Pointer {

        val parameters = Pointer.to(
            this.deviceTargetDimension,
            pointerToPredictions,
            pointerToTargets,
            this.pointerToDeviceBackwardResults)

        this.backwardKernel.launch(
            parameters,
            1,
            this.targetDimension,
            0)

        return this.deviceBackwardResults

    }

}