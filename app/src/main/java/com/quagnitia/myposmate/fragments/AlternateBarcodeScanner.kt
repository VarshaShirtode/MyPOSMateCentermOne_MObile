package com.quagnitia.myposmate.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager

import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Toast
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import java.io.IOException
import android.hardware.Camera.getCameraInfo
import android.hardware.camera2.CameraManager
import android.support.v4.app.ActivityCompat


class AlternateBarcodeScanner(context: Context, surfaceView: SurfaceView, listener: BarcodeListener) : SurfaceHolder.Callback {
    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        cameraSource?.stop()
//        cameraSource?.release()
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        try {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED) {
                cameraSource?.start(surfaceView.holder)
            } else {
                ActivityCompat.requestPermissions(mContext as Activity,
                        arrayOf(Manifest.permission.CAMERA),
                        REQUEST_CAMERA_PERMISSION)
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }catch (e1: Exception) {
            e1.printStackTrace()
        }


    }


    internal var surfaceView: SurfaceView = surfaceView
    internal var listener: BarcodeListener = listener
    private var barcodeDetector: BarcodeDetector? = null
    private var cameraSource: CameraSource? = null
    private val REQUEST_CAMERA_PERMISSION = 201
    internal var mContext: Context = context
    interface BarcodeListener{
        fun onBarcodeReceived(barcodeValue: String)
    }

    fun initialiseDetectorsAndSources() {
//        inf()
        barcodeDetector = BarcodeDetector.Builder(mContext)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build()
        cameraSource = CameraSource.Builder(mContext, barcodeDetector)
//                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .setFacing(inf())
                .build()

        surfaceView.holder.addCallback(this)



       /* surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_GRANTED) {
                            cameraSource?.start(surfaceView.holder)
                    } else {
                        ActivityCompat.requestPermissions(mContext as Activity,
                                arrayOf(Manifest.permission.CAMERA),
                                REQUEST_CAMERA_PERMISSION)
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                }catch (e1: Exception) {
                    e1.printStackTrace()
                }


            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource?.stop()
            }
        })*/


        barcodeDetector?.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
                /*Toast.makeText(mContext,
                        "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show()*/
            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.size() != 0) {
                    listener.onBarcodeReceived(barcodes.valueAt(0).displayValue)
                    Thread.sleep(8000)
                }
            }
        })
    }

    public fun getCameraInstance() : CameraSource? {
        return cameraSource;
    }

    fun removeCallBack(){
        surfaceView.holder.removeCallback(this)
        barcodeDetector?.release()
        cameraSource?.release()
    }

    fun inf() : Int{
        val pm = mContext.getPackageManager()
        var frontCam: Boolean
        var rearCam: Boolean
        frontCam = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
        rearCam = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
        if(rearCam) return 0 else return 1;

    }

}

