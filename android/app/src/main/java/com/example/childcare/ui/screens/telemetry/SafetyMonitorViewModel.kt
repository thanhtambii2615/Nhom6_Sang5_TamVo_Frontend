package com.example.childcare.ui.screens.telemetry

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaRecorder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.childcare.data.repository.LocationRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sqrt

class SafetyMonitorViewModel(application: Application) : AndroidViewModel(application), SensorEventListener {
    private val sensorManager = application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val locationRepository = LocationRepository(application)

    private val _noiseLevel = MutableStateFlow(40.0)
    val noiseLevel: StateFlow<Double> = _noiseLevel

    private val _isFallDetected = MutableStateFlow(false)
    val isFallDetected: StateFlow<Boolean> = _isFallDetected

    private val _isSOSActive = MutableStateFlow(false)
    val isSOSActive: StateFlow<Boolean> = _isSOSActive

    private val _isOutOfBounds = MutableStateFlow(false)
    val isOutOfBounds: StateFlow<Boolean> = _isOutOfBounds

    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false

    init {
        startAudioMeasurement()
        startSensorListening()
    }

    fun startAudioMeasurement() {
        try {
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile("/dev/null")
                prepare()
                start()
            }
            isRecording = true
            viewModelScope.launch {
                while (isActive && isRecording) {
                    try {
                        val amplitude = mediaRecorder?.maxAmplitude ?: 0
                        val db = if (amplitude > 0) 20 * kotlin.math.log10(amplitude.toDouble()) else 40.0
                        _noiseLevel.value = db
                    } catch (e: Exception) {
                        _noiseLevel.value = 40.0
                    }
                    delay(1000L)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopAudioMeasurement() {
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

    private fun startSensorListening() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stopSensorListening() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val gForce = sqrt((x * x + x * y + z * z).toDouble()) / 9.8
            if (gForce > 2.5) {
                _isFallDetected.value = true
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun triggerSOS(active: Boolean, deviceId: String) {
        _isSOSActive.value = active
        viewModelScope.launch {
            locationRepository.sendTelemetry(
                deviceId = deviceId,
                lat = 10.762622,
                lng = 106.660172,
                noise = _noiseLevel.value,
                fall = _isFallDetected.value,
                sos = active
            )
        }
    }

    fun resetFall() {
        _isFallDetected.value = false
    }

    override fun onCleared() {
        super.onCleared()
        stopAudioMeasurement()
        stopSensorListening()
    }
}
