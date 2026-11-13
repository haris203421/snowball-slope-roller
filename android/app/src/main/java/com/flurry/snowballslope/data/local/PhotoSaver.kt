package com.flurry.snowballslope.data.local

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.flurry.snowballslope.R
import com.flurry.snowballslope.domain.model.DecorationKind
import com.flurry.snowballslope.domain.model.Snowman
import com.flurry.snowballslope.data.sample.SampleData
import java.io.File
import java.io.FileOutputStream

class PhotoSaver(private val context: Context) {

    fun savePostcard(snowman: Snowman, caption: String, fileName: String): Boolean {
        val bitmap = runCatching { render(snowman, caption) }.getOrNull() ?: return false
        val saved = write(bitmap, fileName)
        bitmap.recycle()
        return saved
    }

    private fun render(snowman: Snowman, caption: String): Bitmap {
        val bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.parseColor("#EDF6FB"))

        val background = decode(R.drawable.bg_game)
        if (background != null) {
            canvas.drawBitmap(background, cropRect(background), Rect(0, 0, WIDTH, HEIGHT), null)
            background.recycle()
        }

        val veil = Paint().apply { color = Color.argb(70, 237, 246, 251) }
        canvas.drawRect(0f, 0f, WIDTH.toFloat(), HEIGHT.toFloat(), veil)

        val frame = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = FRAME.toFloat()
            isAntiAlias = true
        }
        val inset = FRAME / 2f
        canvas.drawRoundRect(
            RectF(inset, inset, WIDTH - inset, HEIGHT - inset),
            48f,
            48f,
            frame
        )

        val mound = Paint().apply {
            color = Color.WHITE
            isAntiAlias = true
        }
        canvas.drawOval(
            RectF(WIDTH * 0.16f, HEIGHT * 0.80f, WIDTH * 0.84f, HEIGHT * 0.90f),
            mound
        )

        val ball = decode(R.drawable.sprite_1)
        val scale = WIDTH * 0.0072f
        val baseR = snowman.baseDp * scale
        val middleR = snowman.middleDp * scale
        val headR = snowman.headDp * scale
        val groundY = HEIGHT * 0.845f
        val centerX = WIDTH / 2f
        val baseY = groundY - baseR / 2f
        val middleY = baseY - baseR / 2f - middleR / 2f
        val headY = middleY - middleR / 2f - headR / 2f
        if (ball != null) {
            drawCentered(canvas, ball, centerX, baseY, baseR)
            drawCentered(canvas, ball, centerX, middleY, middleR)
            drawCentered(canvas, ball, centerX, headY, headR)
            ball.recycle()
        }

        val stageTop = headY - headR / 2f
        val stageHeight = (groundY - stageTop).coerceAtLeast(1f)
        val scarf = decode(R.drawable.sprite_4)
        val hat = decode(R.drawable.sprite_5)
        val button = Paint().apply {
            color = Color.parseColor("#F25F5C")
            isAntiAlias = true
        }
        snowman.decorations.forEach { item ->
            val anchor = SampleData.anchorOf(item.anchorId) ?: return@forEach
            val x = centerX + (anchor.x - 0.5f) * baseR * 1.8f
            val y = stageTop + anchor.y * stageHeight
            when (item.kind) {
                DecorationKind.Scarf -> scarf?.let { drawCentered(canvas, it, x, y, middleR * 0.9f) }
                DecorationKind.Hat -> hat?.let { drawCentered(canvas, it, x, y, headR * 0.9f) }
                DecorationKind.Button -> canvas.drawCircle(x, y, headR * 0.09f, button)
            }
        }
        scarf?.recycle()
        hat?.recycle()

        val label = Paint().apply {
            color = Color.parseColor("#5A7A8E")
            textSize = WIDTH * 0.038f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText(caption, centerX, HEIGHT - FRAME * 1.4f, label)
        return bitmap
    }

    private fun drawCentered(canvas: Canvas, source: Bitmap, cx: Float, cy: Float, size: Float) {
        val half = size / 2f
        canvas.drawBitmap(
            source,
            null,
            RectF(cx - half, cy - half, cx + half, cy + half),
            null
        )
    }

    private fun decode(resId: Int): Bitmap? =
        runCatching { BitmapFactory.decodeResource(context.resources, resId) }.getOrNull()

    private fun cropRect(source: Bitmap): Rect {
        val target = WIDTH.toFloat() / HEIGHT
        val ratio = source.width.toFloat() / source.height
        return if (ratio > target) {
            val w = (source.height * target).toInt()
            val left = (source.width - w) / 2
            Rect(left, 0, left + w, source.height)
        } else {
            val h = (source.width / target).toInt()
            val top = (source.height - h) / 2
            Rect(0, top, source.width, top + h)
        }
    }

    private fun write(bitmap: Bitmap, fileName: String): Boolean = runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH, ALBUM)
            }
            val uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
            ) ?: return false
            val stream = context.contentResolver.openOutputStream(uri) ?: return false
            stream.use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
            true
        } else {
            val dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: return false
            if (!dir.exists()) dir.mkdirs()
            FileOutputStream(File(dir, fileName)).use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
            true
        }
    }.getOrDefault(false)

    private companion object {
        const val WIDTH = 1080
        const val HEIGHT = 1350
        const val FRAME = 40
        val ALBUM = Environment.DIRECTORY_PICTURES + "/Snowball Slope Roller"
    }
}
