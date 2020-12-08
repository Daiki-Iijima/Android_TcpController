package com.example.tcpcontroller

import android.R.id
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_page.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket


class MainActivity : AppCompatActivity(),
                     MainPageFragment.MainPageFragmentListener,
                     SettingFragment.SettingFragmentListener,
                     VolumeControllerFragment.VolumeControllerListener
{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ChangeToolBarTitle("ホームメニュー")

        val fragment = MainPageFragment()
        replaceFragment(fragment)

        //トランザクションの後にexecutePendingTransactionを呼び出す必要があります。
//        fragmentManager.executePendingTransactions()
//
//        val getfragment: Fragment? = fragmentManager.findFragmentByTag("MainPage")

        toolbar.inflateMenu(R.menu.menu_item)
        toolbar.setOnMenuItemClickListener {
            if(it.itemId == R.id.star){
                ChangeToolBarTitle("ホームメニュー")
                val fragment = MainPageFragment()

                replaceFragment(fragment)
            }
            true
        }
    }

    fun ChangeToolBarTitle(setStr:String)
    {
        runOnUiThread {
            toolbar.title = setStr
        }
    }

    internal var qrScanIntegrator: IntentIntegrator? = null
    fun StartQRRead()
    {
        qrScanIntegrator = IntentIntegrator(this)

        //  タイムアウトの設定
        qrScanIntegrator?.setTimeout(5000)

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
            if(result.contents == null){

                return;
            }

            // result.contents で取得した値を参照できる
            Toast.makeText(this, result.contents, Toast.LENGTH_LONG).show()
            Log.d("読み込み結果", result.contents)

            val getValue = result.contents
            val getValueSplit = getValue.split(',')


            val fragment = VolumeControllerFragment()
            // Bundleインスタンスを作成
            val bundle = Bundle()
            // putXXXXで値をセットする
            bundle.putString("IP", getValueSplit[0])
            bundle.putString("PORT", getValueSplit[1])
            //  値を設定
            fragment.arguments = bundle
            ChangeToolBarTitle("ボリュームコントロール")
            //  画面切り替え
            replaceFragment(fragment)

        }

        else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }



    override fun onClickConnect() {
        Log.d("MainPageFragment","QRコード読み込みモード")
        StartQRRead()
    }

    override fun onClickSetting() {
        Log.d("MainPageFragment","設定モード")
        ChangeToolBarTitle("設定画面")
        val fragment = SettingFragment()

        replaceFragment(fragment)
    }

    override fun onClickBackBtn() {
        Log.d("SettingPageFragement","戻るクリック")
        ChangeToolBarTitle("ホームメニュー")
        val fragment = MainPageFragment()

        replaceFragment(fragment)
    }

    override fun Disconnected() {
        Log.d("VolumeControllerFragment","接続切断")

        ChangeToolBarTitle("ホームメニュー")
        val fragment = MainPageFragment()

        replaceFragment(fragment)
    }

    //  Fragmentの書き換えを行う
    fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, fragment)
        fragmentTransaction.commitAllowingStateLoss()
    }



}