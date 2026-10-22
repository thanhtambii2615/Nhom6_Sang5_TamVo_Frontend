package com.vothanhtam.vothanhtam_1990.utils

import android.media.MediaRecorder
import android.os.Build
import kotlin.math.log10

class NoiseMeasurementHelper {
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false

    fun startMeasuring(onNoiseMeasured: (Double) -> Unit) {
        try {
            mediaRecorder = (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder()
            } else {
                MediaRecorder()
            }).apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile("/dev/null")
                prepare()
                start()
            }
            isRecording = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getAmplitude(): Double {
        if (!isRecording || mediaRecorder == null) return 40.0 // Default ambient noise dB
        return try {
            val amplitude = mediaRecorder?.maxAmplitude ?: 0
            if (amplitude > 0) {
                20 * log10(amplitude.toDouble())
            } else {
                40.0
            }
        } catch (e: Exception) {
            40.0
        }
    }

    fun stopMeasuring() {
        try {
            if (isRecording) {
                mediaRecorder?.stop()
                mediaRecorder?.release()
                mediaRecorder = null
                isRecording = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
