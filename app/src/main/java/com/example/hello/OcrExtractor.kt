package com.example.hello

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await

object OcrExtractor {
    // 支援格式：HKD 4.9, HK$4.9, ¥120, JPY 2400
    // 正則解釋：匹配貨幣符號，可以有空格，然後匹配數字（支援小數點）
    private val currencyRegex = Regex("""(?:HKD|HK\$|¥|JPY|HKD\$)\s*(\d+(?:\.\d{1,2})?)""", RegexOption.IGNORE_CASE)

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    suspend fun extractAmountFromBitmap(bitmap: Bitmap): List<Double> {
        return try {
            val image = InputImage.fromBitmap(bitmap, 0)
            val result = recognizer.process(image).await()
            val text = result.text
            
            // 找出所有匹配嘅金額
            val matches = currencyRegex.findAll(text)
            matches.mapNotNull { it.groupValues.getOrNull(1)?.toDoubleOrNull() }
                .distinct()
                .toList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
