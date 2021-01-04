/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.onurcan.exovideoreference.ui.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Size
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.core.impl.VideoCaptureConfig
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.textfield.TextInputEditText
import com.google.common.util.concurrent.ListenableFuture
import com.onurcan.exovideoreference.R
import com.onurcan.exovideoreference.appuser.AppUser
import com.onurcan.exovideoreference.databinding.ActivityRecordBinding
import com.onurcan.exovideoreference.helper.Constants
import com.onurcan.exovideoreference.ui.contracts.IRecord
import com.onurcan.exovideoreference.ui.presenters.RecordPresenter
import com.onurcan.exovideoreference.utils.showLogInfo
import com.onurcan.exovideoreference.utils.showToast
import com.r0adkll.slidr.Slidr
import java.io.File
import java.util.concurrent.Executors

class RecordActivity : AppCompatActivity(), IRecord.ViewRecord, LifecycleOwner {

    private lateinit var binding: ActivityRecordBinding
    private lateinit var outputDirectory: File

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraSelector: CameraSelector
    private lateinit var videoPreviewView: Preview
    private lateinit var cameraControl: CameraControl
    private lateinit var cameraInfo: CameraInfo

    private lateinit var dialog :Dialog

    private lateinit var dCancel: ImageButton
    private lateinit var dAccept: ImageButton
    private lateinit var dVidName: TextInputEditText

    //    private lateinit var videoCapture: VideoCapture
    private val executor = Executors.newSingleThreadExecutor()

    private lateinit var videoCapture: VideoCapture

    private var isRecording = false

    private var isFrontFacing = true

    private var camera: Camera? = null

    private val recPresenter: RecordPresenter by lazy {
        RecordPresenter()
    }

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_ExoVideoReference_TransStatusBar)
        super.onCreate(savedInstanceState)
        binding = ActivityRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        Slidr.attach(this)

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        showLogInfo(Constants.mRecordActivity, AppUser.getUserId())
        initVideoNameDialog()

        binding.cameraView.post { initFrontCamera() }
        binding.toggleCamera.setOnClickListener {
            toggleCamera()
        }

        binding.recordBtn.setOnClickListener {
            recordVideo()
        }
    }

    override fun toggleCamera() {
        isFrontFacing = if (isFrontFacing) {
            binding.cameraView.post { initFrontCamera() }
            false
        } else {
            binding.cameraView.post { initBackCamera() }
            true
        }
    }

    override fun initVideoNameDialog() {
        dialog = Dialog(this,R.style.BlurTheme)
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog.window!!.setLayout(width, height)
        dialog.window!!.attributes.windowAnimations = R.style.DialogSlide
        dialog.setContentView(R.layout.dialog_video_name)
        dialog.setCanceledOnTouchOutside(true)

        dCancel = dialog.findViewById(R.id.d_close_dialog)
        dAccept = dialog.findViewById(R.id.d_accept_name)
        dVidName = dialog.findViewById(R.id.d_vid_name)
    }

    override fun recordVideo() {
        val camStartFx = MediaPlayer.create(this, R.raw.camera_start_marimba)
        val camStopFx = MediaPlayer.create(this, R.raw.camera_stop_marimba)
        isRecording = if (!isRecording) {
            dAccept.setOnClickListener {
                dialog.dismiss()
                camStartFx.start()
                binding.recordImg.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_start_video
                    )
                )
                startRecording()
            }

            true
        } else {
            camStopFx.start()
            binding.recordImg.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_stop_video
                )
            )
            stopRecording()
            false
        }
        dCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    @SuppressLint("RestrictedApi")
    override fun startRecording() {
        val file = Constants.createFile(
            outputDirectory,
            Constants.FILENAME,
            Constants.VIDEO_EXTENSION
        )
        videoCapture.startRecording(
            file,
            executor,
            object : VideoCapture.OnVideoSavedCallback {
                override fun onVideoSaved(file: File) {
                    Handler(Looper.getMainLooper()).post {
                        showToast(this@RecordActivity, file.name)
                        recPresenter.getVideoTask(
                            file,
                            this@RecordActivity,
                            dVidName.text.toString(),
                            binding.recordedVideo
                        )
                    }
                }

                override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
                    Handler(Looper.getMainLooper()).post {
                        showToast(this@RecordActivity, file.name + " failed to save. / $message")
                    }
                }
            }
        )
    }

    @SuppressLint("RestrictedApi")
    override fun stopRecording() {
        videoCapture.stopRecording()
    }

    @SuppressLint("RestrictedApi")
    override fun initFrontCamera() {
        CameraX.unbindAll()
        outputDirectory = Constants.getOutputDirectory(this)
        videoPreviewView = Preview.Builder().apply {
            setTargetAspectRatio(AspectRatio.RATIO_16_9)
            setTargetRotation(binding.cameraView.display.rotation)
        }.build()

        cameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()

        val videoCaptureCfg = VideoCaptureConfig.Builder().apply {
            setTargetRotation(binding.cameraView.display.rotation)
            setCameraSelector(cameraSelector)
            setTargetAspectRatio(AspectRatio.RATIO_16_9)
        }
        videoCapture = VideoCapture(videoCaptureCfg.useCaseConfig)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            camera = cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                videoPreviewView,
                videoCapture
            )
            cameraInfo = camera?.cameraInfo!!
            cameraControl = camera?.cameraControl!!
            binding.cameraView.preferredImplementationMode =
                PreviewView.ImplementationMode.TEXTURE_VIEW
            videoPreviewView.setSurfaceProvider(binding.cameraView.createSurfaceProvider(cameraInfo))
        }, ContextCompat.getMainExecutor(this.applicationContext))
    }

    @SuppressLint("RestrictedApi")
    override fun initBackCamera() {
        CameraX.unbindAll()
        outputDirectory = Constants.getOutputDirectory(this)
        videoPreviewView = Preview.Builder().apply {
            setTargetAspectRatio(AspectRatio.RATIO_16_9)
            setTargetRotation(binding.cameraView.display.rotation)
        }.build()

        cameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

        val videoCaptureCfg = VideoCaptureConfig.Builder().apply {
            setTargetRotation(binding.cameraView.display.rotation)
            setCameraSelector(cameraSelector)
            setMaxResolution(Size(1080, 2310))
            setDefaultResolution(Size(1080, 2310))
        }

        videoCapture = VideoCapture(videoCaptureCfg.useCaseConfig)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            camera = cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                videoPreviewView,
                videoCapture
            )
            cameraInfo = camera?.cameraInfo!!
            cameraControl = camera?.cameraControl!!
            binding.cameraView.preferredImplementationMode =
                PreviewView.ImplementationMode.TEXTURE_VIEW
            videoPreviewView.setSurfaceProvider(binding.cameraView.createSurfaceProvider(cameraInfo))
        }, ContextCompat.getMainExecutor(this.applicationContext))
    }
}