package com.example.umbum.sqliteankoexp

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import org.jetbrains.anko.button
import org.jetbrains.anko.editText
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.toast
import org.jetbrains.anko.verticalLayout


class AnkoDSLActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verticalLayout {
            val name = editText {
                hint = "이름"
                textSize = 20f
            }

            button ("Show") {
                onClick { toast("Hello! ${name.text}!")}
            }
        }
    }
}