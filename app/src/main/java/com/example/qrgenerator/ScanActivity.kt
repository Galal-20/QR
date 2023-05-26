package com.example.qrgenerator

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.klinker.android.link_builder.Link
import com.klinker.android.link_builder.applyLinks
import java.util.Objects
import java.util.jar.Manifest

class ScanActivity : AppCompatActivity() {

    private lateinit var codeScanner: CodeScanner
    private lateinit var infoText:TextView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        infoText = findViewById(R.id.infoText)

        if (ContextCompat.checkSelfPermission(this , android.Manifest.permission.CAMERA)==
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this , arrayOf(android.Manifest.permission.CAMERA),123)
        }else{
            startScanning()
        }
    }

    private fun startScanning() {
        val scannerView:CodeScannerView= findViewById(R.id.scannerView)
        codeScanner = CodeScanner(this, scannerView)
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS

        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false


        codeScanner.decodeCallback = DecodeCallback{
            runOnUiThread{
                /*val dialog = AlertDialog.Builder(this)
                    .setTitle("Result")
                    .setMessage(it.text)
                    .setPositiveButton("OK"){_,_ ->
                        return@setPositiveButton
                    }.create()
                dialog.show()*/
                val data = it.text
                infoText.text = data

                /*infoText.setOnClickListener {
                    val i = Intent(Intent.ACTION_VIEW , Uri.parse(data))
                    startActivity(i)
                }*/

                val link = Link(data)
                    .setTextColor(Color.BLUE)
                    .setTextColorOfHighlightedLink(Color.CYAN)
                    .setHighlightAlpha(.4f)
                    .setBold(false)
                    .setOnClickListener {
                        val b = Intent(Intent.ACTION_VIEW, Uri.parse(data))
                        startActivity(b)
                    }

                infoText.applyLinks(link)

            }
        }

        codeScanner.errorCallback= ErrorCallback {
            runOnUiThread{
                Toast.makeText(this, "Camera initialization error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123){
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show()
                startScanning()
            }else{
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::codeScanner.isInitialized){
            codeScanner?.startPreview()
        }
    }

    override fun onPause() {
        if (::codeScanner.isInitialized){
            codeScanner?.releaseResources()
        }
        super.onPause()

    }
}