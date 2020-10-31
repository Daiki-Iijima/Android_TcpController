package com.example.tcpcontroller


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment


class SettingFragment : Fragment()
{
    private var listener: SettingFragmentListener? = null

    interface SettingFragmentListener
    {
        fun onClickBackBtn()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.setting,
            container, false
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // ここでActivityのインスタンスではなくActivityに実装されたイベントリスナを取得
        listener = context as SettingFragmentListener
    }

    override fun onStart() {
        super.onStart()

        view?.findViewById<Button>(R.id.BackBtn)?.setOnClickListener { listener?.onClickBackBtn() }

    }
}