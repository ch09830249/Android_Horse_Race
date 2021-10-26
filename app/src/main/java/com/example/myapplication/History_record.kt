package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.SimpleAdapter


class History_record : AppCompatActivity() {

    private lateinit var back_game: Button
    private lateinit var lv_main: ListView
    lateinit var arrayList: ArrayList<HashMap<String, String>> //取得所有資料

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_record)

        //Table所有資料抓出來
        arrayList = mDBHelper!!.showAll()!!


        //秀出表格(ListView)
        lv_main = findViewById(R.id.Lv_main)
        var from: Array<String> = arrayOf("ID", "BETHORSE", "BETMONEY", "WINNER", "EARN", "CAPITAL")
        var to: IntArray  = intArrayOf(R.id.tv_id, R.id.tv_horse, R.id.tv_betmoney, R.id.tv_winner, R.id.tv_earn, R.id.tv_capital)
        var adapter: SimpleAdapter = SimpleAdapter(this, arrayList, R.layout.item, from, to)
        lv_main.adapter = adapter


        //回遊戲主畫面
        back_game = findViewById(R.id.back_game)
        back_game.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                /*val intent = Intent()
                intent.setClass(this@History_record, MainActivity::class.java)
                startActivity(intent)*/
                finish()
            }
        })
    }
}