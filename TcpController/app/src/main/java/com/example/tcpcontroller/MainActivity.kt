package com.example.tcpcontroller

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.io.Reader
import java.net.Socket

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ConnectBtn.setOnClickListener {
            val runnable = Runnable {
                StartClient(
                    IPIInputTextField.text.toString(),
                    PortIInputTextField.text.toString()
                )
            }
            val thread = Thread(runnable)
            thread.start()
        }

        SendMessageBtn.setOnClickListener {
            val runnable = Runnable { SendData(SendInputField.text.toString()) }
            val thread = Thread(runnable)
            thread.start()
        }


    }

    private lateinit var socket: Socket
    private lateinit var writer: PrintWriter
    private lateinit var reader: BufferedReader
    fun StartClient(ip: String, port: String) {
        try {

            //  接続開始
            //  接続されるまでここで止まる
            socket = Socket(ip, port.toInt())

            //  接続成功
            //  UIを操作するのでUIスレッドで実行
            runOnUiThread {
                StateTextView.text = "${socket.remoteSocketAddress.toString()}に接続されました"
            }

            Log.d("", "connected socket")

            //  受信開始
            reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            try {
                reader.use {
                    while (true) {
                        val message = it.readLine()
                        if (message != null) {
                            Log.d("", message)
                        } else {
                            break
                        }
                        Thread.sleep(500L)
                    }
                }
            } catch (e: Exception) {
                Log.e("", "$e")
            }

        } catch (e: Exception) {
            Log.e("", "$e")
        }
    }

    fun SendData(message: String) {
        if (socket == null || !socket.isConnected) {
            Log.d("", "接続されていません")
            return
        }

        try {
            writer = PrintWriter(socket.getOutputStream(), true)

            writer.println(message)
        } catch (e: Exception) {
            Log.e("", "$e")
        }
    }

    fun ClickSend() {

    }
}