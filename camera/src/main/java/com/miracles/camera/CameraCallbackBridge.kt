package com.miracles.camera

/**
 * Created by lxw
 */
internal class CameraCallbackBridge(val cameraView: CameraView) : CallbackBridge<CameraView.Callback>(), CameraFunctions.Callback {
    private val mBackgroundThread = ThreadLoop("CameraCallbackBridge")

    internal fun startBackgroundThread() {
        mBackgroundThread.start()
    }

    internal fun stopBackgroundThread() {
        mBackgroundThread.quit()
    }

    override fun onCameraOpened() {
        callback { onCameraOpened(cameraView) }
    }

    override fun onCameraClosed() {
        callback { onCameraClosed(cameraView) }
    }

    override fun onStartCapturePicture() {
        callback { onStartCapturePicture(cameraView) }
    }

    override fun onPictureTaken(data: ByteArray) {
        callback { onPictureTaken(cameraView, data) }
    }

    override fun onStartRecordingFrame(timeStampInNs: Long) {
        callback { onStartRecordingFrame(cameraView, timeStampInNs) }
    }

    override fun onFrameRecording(data: ByteArray, len: Int, bytesPool: ByteArrayPool, width: Int, height: Int, format: Int,
                                  orientation: Int, facing: Int, timeStampInNs: Long) {
        callback { onFrameRecording(cameraView, data, len, bytesPool, width, height, format, orientation, facing, timeStampInNs) }
        //recycle...
        mBackgroundThread.enqueue(Runnable { bytesPool.releaseBytes(data) })
    }

    override fun onStopRecordingFrame(timeStampInNs: Long) {
        callback { onStopRecordingFrame(cameraView, timeStampInNs) }
    }

    override fun callback(methods: CameraView.Callback.() -> Unit) {
        mBackgroundThread.enqueue(Runnable {
            super.callback(methods)
        })
    }

}