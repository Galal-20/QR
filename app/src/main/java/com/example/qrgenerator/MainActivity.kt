package com.example.qrgenerator

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter

class MainActivity : AppCompatActivity() {

    private lateinit var idIvQrcode:ImageView
    private lateinit var edit:EditText
    private lateinit var buttonGenerator:Button


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        idIvQrcode = findViewById(R.id.idIvQrcode)
        edit = findViewById(R.id.edit)
        buttonGenerator = findViewById(R.id.buttonGenerator)

        buttonGenerator.setOnClickListener {
            val data = edit.text.toString()
            if (data.isEmpty()){
                Toast.makeText(this,"Enter some data ", Toast.LENGTH_SHORT).show()
            }else{
                val write = QRCodeWriter()
                try {
                    val bitmatrix = write.encode(data, BarcodeFormat.QR_CODE , 512,512)
                    val width = bitmatrix.width
                    val height = bitmatrix.height
                    val bmp = Bitmap.createBitmap(width,height,Bitmap.Config.RGB_565)
                    for (x in 0 until width){
                        for (y in 0 until height){
                            bmp.setPixel(x , y , if (bitmatrix[x,y]) Color.BLACK else Color.WHITE)
                        }
                    }
                    idIvQrcode.setImageBitmap(bmp)

                    saveQRCodeToGallery(bmp)

                }catch (_:WriterException){

                }
            }
        }
    }

    private fun saveQRCodeToGallery(qrCodeBitmap: Bitmap) {
        val filename = "QRCode_${System.currentTimeMillis()}.png"

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        }

        val resolver = contentResolver
        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        if (imageUri != null) {
            resolver.openOutputStream(imageUri).use { outputStream ->
                qrCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }

            // Notify the gallery app to scan for new media
            sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, imageUri))

            // Optional: Show a toast or provide feedback to the user
            Toast.makeText(this, "QR code saved to gallery", Toast.LENGTH_SHORT).show()
        } else {
            // Failed to create the image file
            // Handle the scenario accordingly
            Toast.makeText(this, "Check your permissions", Toast.LENGTH_SHORT).show()

        }
    }

    fun scanner(view: View) {
        val i = Intent(this , ScanActivity::class.java)
        startActivity(i)
    }
}