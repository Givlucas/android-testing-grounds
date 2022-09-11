package com.example.myapplication

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var button1 : Button
    private lateinit var num2 : TextView
    private var sum : Int = 0
    private var myservice: Serv? = null
    private var isBound = false

     private val ConnectToBound = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
            val binder = service as Serv.LocalBinder
            myservice = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //binds a service
        val intent = Intent(this, Serv::class.java)
        bindService(intent, ConnectToBound, Context.BIND_AUTO_CREATE)

        /*
        //starts a service unbounded
        Intent(this, Serv::class.java).also { intent ->
            startService(intent)
        }
        */

        button1 = findViewById(R.id.etButton)
        num2 = findViewById(R.id.num1)

        button1.setOnClickListener {
            val time = myservice?.getCurrentTime()
            num2.text = time
            /*
            num2.text = sum.toString()
            sum += 1
            */
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        /*
        //stops the unbound service
        Intent(this, Serv::class.java).also { intent ->
            stopService(intent)
        }
         */
    }

}

class Serv : Service() {
    //Start a http server using ktor
    companion object {
        private const val PORT = 5001
    }
    private val server by lazy {
        embeddedServer(Netty, PORT, watchPaths = emptyList()) {
            install(WebSockets)
            install(CallLogging)
            routing {
                get("/") {
                    call.respondText(
                        text = "Hello!! You are here in ${Build.MODEL} with service!",
                        contentType = ContentType.Text.Plain
                    )
                }
            }
        }
    }
    fun getCurrentTime() : String {
        val dateFormat = SimpleDateFormat("HH:mm:ss MM/dd/yyyy", Locale.US)
        return dateFormat.format(Date())
    }

    override fun onCreate() {
        CoroutineScope(Dispatchers.IO).launch {
            server.start(wait = true)
        }
    }

    override fun onDestroy() {
        server.stop(1_000, 2_000)
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return LocalBinder()
    }

    inner class LocalBinder : Binder() {
        fun getService() : Serv {
            return this@Serv
        }
    }
}