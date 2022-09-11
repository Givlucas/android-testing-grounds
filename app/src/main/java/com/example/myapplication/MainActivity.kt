package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var button1 : Button
    private lateinit var num2 : TextView
    private var sum : Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button1 = findViewById(R.id.etButton)
        num2 = findViewById(R.id.num1)

        button1.setOnClickListener {
            num2.text = sum.toString()
            sum += 1
        }


    }
}