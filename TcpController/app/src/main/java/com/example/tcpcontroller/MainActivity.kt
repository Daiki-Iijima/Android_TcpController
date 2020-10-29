package com.example.tcpcontroller

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ConnectBtn.setOnClickListener {

            StartQRRead()


        }

    }
    internal var qrScanIntegrator: IntentIntegrator? = null
    fun StartQRRead()
    {
        qrScanIntegrator = IntentIntegrator(this)

        // 画面の回転をさせない
        qrScanIntegrator?.setOrientationLocked(false)

        // QR 読み取り後にビープ音がなるのを止める
        qrScanIntegrator?.setBeepEnabled(false)

        // スキャン開始 (QR アクティビティ生成)
        qrScanIntegrator?.initiateScan()
    }

    // 読み取り後に呼ばれるメソッド
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // 結果の取得
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (result != null) {
            // result.contents で取得した値を参照できる
            Toast.makeText(this, result.contents, Toast.LENGTH_LONG).show()
            Log.d("読み込み結果",result.contents)

            val getValue = result.contents
            val getValueSplit = getValue.split(',')
            
            val runnable = Runnable {
                StartClient(getValueSplit[0],getValueSplit[1])

            }
            val thread = Thread(runnable)
            thread.start()

        }

        else {
            super.onActivityResult(requestCode, resultCode, data)
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

            //  情報取得コマンド送信
            SendData("GET_VOLUME");

            //  接続成功
            //  UIを操作するのでUIスレッドで実行
            runOnUiThread {
                //StateTextView.text = "${socket.remoteSocketAddress.toString()}に接続されました"
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

                            //  ボリューム情報の場合
                            try {
                                val jsonObj: JSONObject = JSONObject(message)
                                val deviceName = jsonObj.getString("DeviceName")
                                val deviceVolume = jsonObj.getDouble("DeviceVolume")

                                Log.d("", "======" + deviceName + ":" + deviceVolume + "======")

                                runOnUiThread {
                                    val linearLayout =
                                        findViewById(R.id.MainLinearLayout) as LinearLayout    // レイアウトファイルにあるレイアウトのidを指定して読み込みます
                                    linearLayout.gravity = Gravity.CENTER   // 画面中央寄せ

                                    val seek = SeekBar(this)
                                    val text = TextView(this)

                                    text.setPadding(80, 10, 0, 0)
                                    text.text = deviceName

                                    seek.setPadding(100, 10, 50, 0)
                                    seek.setProgress((deviceVolume * 100).toInt(), true)

                                    seek.setOnSeekBarChangeListener(
                                        object : SeekBar.OnSeekBarChangeListener {
                                            //ツマミがドラッグされると呼ばれる
                                            override fun onProgressChanged(
                                                seekBar: SeekBar, progress: Int, fromUser: Boolean
                                            ) {
                                                val nowValue: Float = progress / 100f

                                                //  値の送信
                                                val runnable =
                                                    Runnable { SendData(deviceName + "," + nowValue.toString()) }
                                                val thread = Thread(runnable)
                                                thread.start()
                                            }

                                            override fun onStartTrackingTouch(seekBar: SeekBar) {
                                                // ツマミがタッチされた時に呼ばれる
                                            }

                                            override fun onStopTrackingTouch(seekBar: SeekBar) {
                                                // ツマミがリリースされた時に呼ばれる
                                            }

                                        })

                                    linearLayout.addView(text)  //  テキストを追加
                                    linearLayout.addView(seek)  // レイアウトファイルにテキストビューを追加します
                                }

                                val volumeChannel = jsonObj.getJSONArray("VolumeChannel")
                                for (j in 0..volumeChannel.length() - 1) {
                                    val channelObj = volumeChannel.getJSONObject(j)
                                    val id = channelObj.getString("ID")
                                    val channelName = channelObj.getString("ChannelName")
                                    val channelVolume = channelObj.getDouble("ChannelVolume")

                                    Log.d("", id + ":" + channelName + ":" + channelVolume)

                                    runOnUiThread {
                                        val linearLayout =
                                            findViewById(R.id.MainLinearLayout) as LinearLayout    // レイアウトファイルにあるレイアウトのidを指定して読み込みます
                                        linearLayout.gravity = Gravity.CENTER   // 画面中央寄せ

                                        val seek = SeekBar(this)
                                        val text = TextView(this)

                                        text.setPadding(180, 10, 0, 0)
                                        text.text = channelName

                                        seek.setPadding(200, 10, 50, 0)
                                        seek.setProgress((channelVolume * 100).toInt(), true)
                                        seek.setOnSeekBarChangeListener(
                                            object : SeekBar.OnSeekBarChangeListener {
                                                //ツマミがドラッグされると呼ばれる
                                                override fun onProgressChanged(
                                                    seekBar: SeekBar,
                                                    progress: Int,
                                                    fromUser: Boolean
                                                ) {
                                                    val nowValue: Float = progress / 100f

                                                    //  値の送信
                                                    val runnable =
                                                        Runnable { SendData(channelName + "," + nowValue.toString()) }
                                                    val thread = Thread(runnable)
                                                    thread.start()
                                                }

                                                override fun onStartTrackingTouch(seekBar: SeekBar) {
                                                    // ツマミがタッチされた時に呼ばれる
                                                }

                                                override fun onStopTrackingTouch(seekBar: SeekBar) {
                                                    // ツマミがリリースされた時に呼ばれる
                                                }

                                            })

                                        linearLayout.addView(text)  //  テキストを追加
                                        linearLayout.addView(seek)  // レイアウトファイルにテキストビューを追加します
                                    }
                                }

                            } catch (e: java.lang.Exception) {

                            }

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