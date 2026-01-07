package com.recipecraft.android.presentation.viewmodels

import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recipecraft.android.presentation.screens.CaptureState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * CameraViewModel with frame processing on background thread
 * 
 * ✅ P1 FIX: Uses Dispatchers.Default for frame processing
 * Prevents main thread blocking
 */
class CameraViewModel : ViewModel() {
    
    private val _captureState = MutableStateFlow<CaptureState>(CaptureState.Idle)
    val captureState: StateFlow<CaptureState> = _captureState
    
    /**
     * Process frame on IO dispatcher to avoid main thread blocking
     * ✅ P1 FIX: Background thread processing
     */
    fun processFrame(image: ImageProxy) {
        viewModelScope.launch(Dispatchers.Default) {  // ✅ KEY FIX: Background thread
            try {
                // TODO: Add ML Kit or other ingredient detection here
                // For now, just log frame info
                Log.d(
                    "CameraVM",
                    "Processing frame at ${image.imageInfo.timestamp}, size: ${image.width}x${image.height}"
                )
                
                // Example: ML Kit Vision API would go here
                // val inputImage = InputImage.fromMediaImage(image.image!!, rotationDegrees)
                // ingredientDetector.process(inputImage)
                //     .addOnSuccessListener { results ->
                //         // Update UI with detected ingredients
                //     }
                //     .addOnFailureListener { e ->
                //         Log.e("CameraVM", "Ingredient detection failed", e)
                //     }
                
            } catch (e: Exception) {
                Log.e("CameraVM", "Frame processing failed", e)
                _captureState.value = CaptureState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    fun capturePhoto() {
        _captureState.value = CaptureState.Capturing
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // TODO: Implement actual photo capture logic
                // For now, simulate success
                kotlinx.coroutines.delay(500)
                _captureState.value = CaptureState.Success("/path/to/photo.jpg")
                
                // Reset to idle after 2 seconds
                kotlinx.coroutines.delay(2000)
                _captureState.value = CaptureState.Idle
                
            } catch (e: Exception) {
                Log.e("CameraVM", "Photo capture failed", e)
                _captureState.value = CaptureState.Error(e.message ?: "Capture failed")
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        Log.d("CameraVM", "ViewModel cleared")
    }
}