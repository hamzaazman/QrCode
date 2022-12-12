package com.hamzaazman.qrcode

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.hamzaazman.qrcode.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private lateinit var codeScanner: CodeScanner


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        with(binding!!) {
            codeScanner = CodeScanner(this@MainActivity, this.scannerView)

            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                val permissionList = arrayOf(Manifest.permission.CAMERA)
                requestPermissions(permissionList, 1)

                /*val remainingPermissions = java.util.ArrayList<String>()
                for (permission in permissionList) {
                    if (ContextCompat.checkSelfPermission(
                            this@MainActivity, permission
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        remainingPermissions.add(permission)
                    }
                }
                requestPermissions(remainingPermissions.toTypedArray(), 101)
*/
            }

            codeScanner.camera = CodeScanner.CAMERA_BACK
            codeScanner.autoFocusMode = AutoFocusMode.SAFE
            codeScanner.formats = CodeScanner.ALL_FORMATS
            codeScanner.isAutoFocusEnabled = true
            codeScanner.isFlashEnabled = false

            codeScanner.decodeCallback = DecodeCallback {
                runOnUiThread {

                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "${it}")
                        type = "text/plain"
                    }

                    val shareIntent = Intent.createChooser(sendIntent, null)
                    startActivity(shareIntent)
                    Toast.makeText(this@MainActivity, "$it.text", Toast.LENGTH_SHORT).show()
                }
            }

            codeScanner.errorCallback = ErrorCallback {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "$it.text", Toast.LENGTH_SHORT).show()
                }
            }

            scannerView.setOnClickListener {
                codeScanner.startPreview()
            }
        }


    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        super.onPause()
        codeScanner.releaseResources()
    }

}