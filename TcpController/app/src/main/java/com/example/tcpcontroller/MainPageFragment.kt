package com.example.tcpcontroller

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment


class MainPageFragment:Fragment() {
    private var listener: MainPageFragmentListener? = null

    interface MainPageFragmentListener {
        fun onClickConnect()
        fun onClickSetting()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(
            R.layout.main_page,
            container, false
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // ここでActivityのインスタンスではなくActivityに実装されたイベントリスナを取得
        listener = context as MainPageFragmentListener
    }

    override fun onStart() {
        super.onStart()

        view?.findViewById<Button>(R.id.ConnectBtn)?.setOnClickListener { listener?.onClickConnect() }
        view?.findViewById<Button>(R.id.SettingBtn)?.setOnClickListener { listener?.onClickSetting() }

    }

}