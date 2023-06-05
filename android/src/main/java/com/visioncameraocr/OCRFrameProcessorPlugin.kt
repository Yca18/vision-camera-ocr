package com.visioncameraocr

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.YuvImage
import android.graphics.Point
import android.graphics.Rect
import android.media.Image
import androidx.camera.core.ImageProxy
import com.facebook.react.bridge.ReadableNativeMap
import com.facebook.react.bridge.WritableNativeArray
import com.facebook.react.bridge.WritableNativeMap
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizerOptionsInterface
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import com.mrousavy.camera.frameprocessor.FrameProcessorPlugin
import java.nio.ByteBuffer
import java.io.ByteArrayOutputStream
import kotlin.math.roundToInt

class OCRFrameProcessorPlugin: FrameProcessorPlugin("scanOCR") {

    private fun getBlockArray(blocks: MutableList<Text.TextBlock>): WritableNativeArray {
        val blockArray = WritableNativeArray()

        for (block in blocks) {
            val blockMap = WritableNativeMap()

            blockMap.putString("text", block.text)
            blockMap.putArray("recognizedLanguages", getRecognizedLanguages(block.recognizedLanguage))
            blockMap.putArray("cornerPoints", block.cornerPoints?.let { getCornerPoints(it) })
            blockMap.putMap("frame", getFrame(block.boundingBox))
            blockMap.putArray("lines", getLineArray(block.lines))

            blockArray.pushMap(blockMap)
        }
        return blockArray
    }

    private fun getLineArray(lines: MutableList<Text.Line>): WritableNativeArray {
        val lineArray = WritableNativeArray()

        for (line in lines) {
            val lineMap = WritableNativeMap()

            lineMap.putString("text", line.text)
            lineMap.putArray("recognizedLanguages", getRecognizedLanguages(line.recognizedLanguage))
            lineMap.putArray("cornerPoints", line.cornerPoints?.let { getCornerPoints(it) })
            lineMap.putMap("frame", getFrame(line.boundingBox))
            lineMap.putArray("elements", getElementArray(line.elements))

            lineArray.pushMap(lineMap)
        }
        return lineArray
    }

    private fun getElementArray(elements: MutableList<Text.Element>): WritableNativeArray {
        val elementArray = WritableNativeArray()

        for (element in elements) {
            val elementMap = WritableNativeMap()

            elementMap.putString("text", element.text)
            elementMap.putArray("cornerPoints", element.cornerPoints?.let { getCornerPoints(it) })
            elementMap.putMap("frame", getFrame(element.boundingBox))
        }
        return elementArray
    }

    private fun getRecognizedLanguages(recognizedLanguage: String): WritableNativeArray {
        val recognizedLanguages = WritableNativeArray()
        recognizedLanguages.pushString(recognizedLanguage)
        return recognizedLanguages
    }

    private fun getCornerPoints(points: Array<Point>): WritableNativeArray {
        val cornerPoints = WritableNativeArray()

        for (point in points) {
            val pointMap = WritableNativeMap()
            pointMap.putInt("x", point.x)
            pointMap.putInt("y", point.y)
            cornerPoints.pushMap(pointMap)
        }
        return cornerPoints
    }

    private fun getFrame(boundingBox: Rect?): WritableNativeMap {
        val frame = WritableNativeMap()

        if (boundingBox != null) {
            frame.putDouble("x", boundingBox.exactCenterX().toDouble())
            frame.putDouble("y", boundingBox.exactCenterY().toDouble())
            frame.putInt("width", boundingBox.width())
            frame.putInt("height", boundingBox.height())
            frame.putInt("boundingCenterX", boundingBox.centerX())
            frame.putInt("boundingCenterY", boundingBox.centerY())
        }
        return frame
    }

    override fun callback(frame: ImageProxy, params: Array<Any>): Any? {
        val result = WritableNativeMap()
        val languageCode: String = params[0] as String
        val cropData: ReadableNativeMap? = params[1] as ReadableNativeMap?
        val cropX: Int? = cropData?.getDouble("x")?.roundToInt();
        val cropY: Int? = cropData?.getDouble("y")?.roundToInt();
        val cropWidth: Int? = cropData?.getDouble("width")?.roundToInt();
        val cropHeight: Int? = cropData?.getDouble("height")?.roundToInt();
        val recognizerOptions: TextRecognizerOptionsInterface = getTextRecognizerOptionsForCode(languageCode)
        val recognizer = TextRecognition.getClient(recognizerOptions)

        @SuppressLint("UnsafeOptInUsageError")
        var imageBitmap: Bitmap? = null;
        val mediaImage: Image? = frame.getImage()

        if(mediaImage != null && cropX != null && cropY != null && cropWidth != null && cropHeight != null){
            val bitmap: Bitmap? = imageToBitmap(mediaImage)
            if(bitmap != null){
                imageBitmap = Bitmap.createBitmap(bitmap, cropX, cropY, cropHeight, cropWidth)
            }
        }

        var image: InputImage? = null;
        if(imageBitmap != null) {
            image = InputImage.fromBitmap(imageBitmap, frame.imageInfo.rotationDegrees)
        } else if(mediaImage != null){
            image = InputImage.fromMediaImage(mediaImage, frame.imageInfo.rotationDegrees)
        }

        frame.close()

        if(image != null){
            val task: Task<Text> = recognizer.process(image)
            try {
                val text: Text = Tasks.await<Text>(task)
                result.putString("text", text.text)
                result.putArray("blocks", getBlockArray(text.textBlocks))
            } catch (e: Exception) {
                return null
            }

            val data = WritableNativeMap()
            data.putMap("result", result)
            return data
        } else {
            return WritableNativeMap()
        }
    }

    private fun getTextRecognizerOptionsForCode(languageCode: String): TextRecognizerOptionsInterface {

        val recognizerOptions = when(languageCode) {
            "chi" -> ChineseTextRecognizerOptions.Builder().build()
            "hin", "san", "pra" -> DevanagariTextRecognizerOptions.Builder().build()
            "jpn" -> JapaneseTextRecognizerOptions.Builder().build()
            "kor" -> KoreanTextRecognizerOptions.Builder().build()
            else -> TextRecognizerOptions.DEFAULT_OPTIONS
        }

        return recognizerOptions

    }

    private fun imageToBitmap(image: Image): Bitmap? {
        val nv21 = yuv420888ToNV21(image)
        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 100, out)
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    private fun yuv420888ToNV21(image: Image): ByteArray {
        val nv21: ByteArray
        val yBuffer = image.planes[0].buffer
        val uBuffer = image.planes[1].buffer
        val vBuffer = image.planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        nv21 = ByteArray(ySize + uSize + vSize)

        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        return nv21
    }
}