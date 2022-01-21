package com.midea.cabinet.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.midea.cabinet.R
import com.midea.cabinet.business.control.NoCabinetFaceControl
import com.midea.cabinet.utils.ClickProxy
import kotlinx.android.synthetic.main.activity_launcher.*

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)
        iv_shopping_go.setOnClickListener(ClickProxy(View.OnClickListener {
            startActivity(Intent(this@LauncherActivity, MainActivity::class.java))
        }))
        NoCabinetFaceControl.checkDeviceBinded()
    }

}