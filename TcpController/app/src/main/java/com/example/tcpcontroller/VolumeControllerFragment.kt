package com.example.tcpcontroller

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import kotlin.math.log

//  通信処理をおこなう
class VolumeControllerFragment:Fragment() {
    private var listener: VolumeControllerListener? = null
    interface VolumeControllerListener
    {
        fun Disconnected()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.volume_page,
            container, false
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onStart() {
        super.onStart()

        listener = context as VolumeControllerListener

        val ip = arguments?.getString("IP")
        val port = arguments?.getString("PORT")
        if(ip==null || port==null){return}
        activity?.let { StartClient(ip,port, it) }

        view?.findViewById<Button>(R.id.DisconnectButton)?.setOnClickListener {

            writer.close()
            reader.close()
            socket.close()

            //  画面遷移
            listener?.Disconnected()
        }

    }

    private lateinit var socket: Socket
    private lateinit var writer: PrintWriter
    private lateinit var reader: BufferedReader
    fun StartClient(ip: String, port: String,act:Activity) {

        val runnable = Runnable {
        try {

            //  接続開始
            //  接続されるまでここで止まる
            socket = Socket(ip, port.toInt())

            //  情報取得コマンド送信
            SendData("GET_VOLUME");

            //  接続成功

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

                                Log.d("View1",view.toString())

                                act.runOnUiThread {
                                    Log.d("View2",view.toString())

                                    val linearLayout =
                                        view?.findViewById<LinearLayout>(R.id.MainLinearLayout)    // レイアウトファイルにあるレイアウトのidを指定して読み込みます
                                    if (linearLayout != null) {
                                        linearLayout.gravity = Gravity.CENTER
                                    }   // 画面中央寄せ

                                    val seek = SeekBar(view?.context)
                                    val text = TextView(view?.context)

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

                                    linearLayout?.addView(text)  //  テキストを追加
                                    linearLayout?.addView(seek)  // レイアウトファイルにテキストビューを追加します
                                }

                                val volumeChannel = jsonObj.getJSONArray("VolumeChannel")
                                for (j in 0..volumeChannel.length() - 1) {
                                    val channelObj = volumeChannel.getJSONObject(j)
                                    val id = channelObj.getString("ID")
                                    val channelName = channelObj.getString("ChannelName")
                                    val channelVolume = channelObj.getDouble("ChannelVolume")

                                    Log.d("", id + ":" + channelName + ":" + channelVolume)



                                    act.runOnUiThread {
                                        val linearLayout =
                                            view?.findViewById(R.id.MainLinearLayout) as LinearLayout    // レイアウトファイルにあるレイアウトのidを指定して読み込みます
                                        linearLayout.gravity = Gravity.CENTER   // 画面中央寄せ

                                        val seek = SeekBar(view?.context)
                                        val text = TextView(view?.context)

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
                Log.e("1", "$e")
            }

        } catch (e: Exception) {
            Log.e("2", "$e")


        }

            listener?.Disconnected()

            }
            val thread = Thread(runnable)
            thread.start()
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

}