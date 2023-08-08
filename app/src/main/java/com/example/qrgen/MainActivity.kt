package com.example.qrgen

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.example.qrgen.databinding.ActivityMainBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter

class MainActivity : AppCompatActivity() {
    //binding
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //full screen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        //InitViews
        binding.apply {
            //generate qr code click listener
            btnGenerate.setOnClickListener {
                val text = etText.text.toString().trim()
                if (text.isNotEmpty()) {
                    val bitmap = generateQrCode(text)
                    ivQRCode.setImageBitmap(bitmap)
                    btnShare.isEnabled = true
                }
            }
            //set up the share click listener
            btnShare.setOnClickListener {
                shareQrCode()
            }
        }
    }

    //share QR Code
    private fun shareQrCode() {
        val bitmap = (binding.ivQRCode.drawable).toBitmap()
        val uri = getImageUri(bitmap)
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(intent, "Share QrCode"))
    }

    private fun getImageUri(bitmap: Bitmap): Uri {
        val path = MediaStore.Images.Media.insertImage(
            contentResolver,
            bitmap,
            "QR Code",
            null
        )
        return Uri.parse(path)
    }

    //generate qr code
    private fun generateQrCode(text: String): Bitmap {
        val writer = QRCodeWriter()
        try {
            val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(
                        x,
                        y,
                        if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                    )
                }
            }
            return bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            return Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565)
        }
    }
}

