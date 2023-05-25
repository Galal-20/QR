package com.example.qrgenerator

import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter

class MainActivity : AppCompatActivity() {

    private lateinit var idIvQrcode:ImageView
    private lateinit var edit:EditText
    private lateinit var buttonGenerator:Button


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
                }catch (_:WriterException){

                }
            }
        }
    }
}