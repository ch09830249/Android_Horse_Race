package com.example.myapplication


import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import java.lang.Exception
import android.content.Intent
import android.util.Log
import android.view.KeyEvent
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException



var winner: String? = null                     //獲勝馬匹
var betmoney: Int? = null                      //下注金
var capital: Int = 10000                       //賭金
var bethorsename: String? = null               //下注馬匹
var ratio_apple: Double = 2.0
var ratio_banana: Double = 2.0
var ratio_orange: Double = 2.0
var ratio_pineapple: Double = 2.0
var earn: Int? = null                           //獎金 (這裡已經乘上匯率所以是台幣)
var mDBHelper: Record? = null                   //資料庫的參照(Record class)
var flag_apple_finish: Boolean = true
var flag_banana_finish: Boolean = true
var flag_orange_finish: Boolean = true
var flag_pineapple_finish: Boolean = true
var currency: Double = 0.0                      //當天匯率



class MainActivity : AppCompatActivity() {

    private lateinit var mBtnButton: Button
    private lateinit var mBtn_historyButton: Button
    private lateinit var mBtn_resetButton: Button
    private lateinit var mBtn_Game_Over_Button: Button
    private lateinit var Text1: EditText
    private lateinit var Text2: TextView
    private lateinit var ratio1: TextView
    private lateinit var ratio2: TextView
    private lateinit var ratio3: TextView
    private lateinit var ratio4: TextView
    private lateinit var RadioB1: RadioGroup
    private lateinit var Progbar1: ProgressBar
    private lateinit var Progbar2: ProgressBar
    private lateinit var Progbar3: ProgressBar
    private lateinit var Progbar4: ProgressBar

    //DB global variables
    private val DB_NAME: String = "MyRecord.db"
    private val TABLE_NAME = "History"
    private val DB_VERSION = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        //建立資料庫
        mDBHelper = Record(this, name = DB_NAME, null, version = DB_VERSION, table_name = TABLE_NAME) //初始化資料庫
        //兩種跳轉回mainActivity:    1.開App(資料庫重置)     2.從history跳回mainActivity(資料庫無須重置)
        if(betmoney ==null)
            mDBHelper!!.drop_table()
        mDBHelper!!.checkTable()//確認是否存在資料表，沒有則新增


        //4隻馬的Progress Bar
        Progbar1 = findViewById(R.id.progressBar1)
        Progbar2 = findViewById(R.id.progressBar2)
        Progbar3 = findViewById(R.id.progressBar3)
        Progbar4 = findViewById(R.id.progressBar4)

        //4隻馬的ratio  (TextView)
        ratio1 = findViewById(R.id.ratio1)
        ratio2 = findViewById(R.id.ratio2)
        ratio3 = findViewById(R.id.ratio3)
        ratio4 = findViewById(R.id.ratio4)

        //上網抓最新的美金比台幣匯率
        //匯率open api 資料網址
        val CurrencyURL ="https://tw.rter.info/capi.php"
        //Part 1: 宣告 OkHttpClient
        val client = OkHttpClient()
        //Part 2: 宣告 Request，要求要連到指定網址
        val request = Request.Builder().url(CurrencyURL).build()
        //Part 3: 宣告 Call  執行 Call 連線後，採用 enqueue 非同步方式，獲取到回應的結果資料
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.d("HKT", e.toString())
            }
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val whole_exchange_result = response.body?.string()
                //抓出屬於USDTWD
                val list1 = whole_exchange_result?.split("}, ")
                var result = ""
                for (item in list1!!){
                    if(item.startsWith("\"USDTWD\":")){  //找到開頭有"USDTWD"開頭的字串
                        result = item
                    }
                }
                result = result.substring(10)+"}"   //切字串 {"Exrate": 27.885501, "UTC": "2021-10-21 05:59:59"} 的樣式(USDTWD的)
                //Log.d("Kenny", "string: " + result)
                val posts: Post? = Gson().fromJson(result, Post::class.java)
                currency = posts!!.Exrate
                val tv_currency: TextView = findViewById(R.id.txt_ratio4)
                tv_currency.text = currency.toString()
                //Log.d("Kenny", "string: " + posts!!.toString())
                //Log.d("Kenny", "string: " + posts!!.Exrate)
                //Log.d("Kenny", "string: " + posts!!.UTC)
            }
        })

        //Start Game Button
        mBtnButton = findViewById(R.id.button1)
        mBtnButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {

                //遊戲開始前(check: 1.有沒有輸入金額  2.金額有沒有大於10 3.遊戲是否進行中  4.錢夠不夠)

                val previous_betmonney: Int? = betmoney //先把錢一把的賭金存起來, 因為下面會改到

                //取下注金額: betmoney
                try{
                    Text1 = findViewById(R.id.edittext)
                    betmoney = Text1.text.toString().trim().toInt()
                }catch (e: Exception){
                    betmoney = previous_betmonney
                    ToastUtil().showToast(this@MainActivity, "請輸入下注金額!!!")
                    //Toast.makeText(this@MainActivity, "請輸入下注金額!!!", Toast.LENGTH_SHORT).show()
                    return
                }

                //避免按好幾次
                if(flag_apple_finish!=true || flag_banana_finish!=true || flag_orange_finish!=true || flag_pineapple_finish!=true || betmoney!! > 10 || capital < betmoney!! || betmoney==0){
                    if(flag_apple_finish!=true || flag_banana_finish!=true || flag_orange_finish!=true || flag_pineapple_finish!=true)
                        ToastUtil().showToast(this@MainActivity, "遊戲進行中!!!")
                        //Toast.makeText(this@MainActivity, "遊戲進行中!!!", Toast.LENGTH_SHORT).show()
                    if(capital < betmoney!!)
                        ToastUtil().showToast(this@MainActivity, "錢不夠完!!!")
                    if(betmoney!!>10)
                        ToastUtil().showToast(this@MainActivity, "超過10元!!!")
                    if(betmoney==0)
                        ToastUtil().showToast(this@MainActivity, "請輸入下注金額!!!")
                    betmoney = previous_betmonney //還原成前一次的賭金
                    return
                }

                //把上一把的遊戲資訊存入DB  winner==null: APP剛開沒有上一次資料或是資料已經放到資料庫了
                if(winner!=null){
                    mDBHelper!!.addData(bethorse = bethorsename, betmoney = previous_betmonney, winner = winner, earn = earn, capital = capital)
                    winner = null
                }

                //取下注馬匹: bethorse
                RadioB1 = findViewById(R.id.rg1)
                val RadioBtm: RadioButton = findViewById(RadioB1.getCheckedRadioButtonId())
                bethorsename = RadioBtm.text.toString()

                //Start Game
                //比賽馬匹
                start_game()


            }
        })


        //History Record Button
        mBtn_historyButton = findViewById(R.id.button2)
        mBtn_historyButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {

                //避免按好幾次
                if(flag_apple_finish!=true || flag_banana_finish!=true || flag_orange_finish!=true || flag_pineapple_finish!=true){
                    ToastUtil().showToast(this@MainActivity, "遊戲進行中!!!")
                    return
                }

                //把上一把的遊戲資訊存入DB
                if(winner!=null){
                    mDBHelper!!.addData(bethorse = bethorsename, betmoney = betmoney, winner = winner, earn = earn, capital = capital)
                    winner = null
                }
                ToastUtil().showToast(this@MainActivity, "查詢歷史紀錄!!!")

                //跳轉到History_record頁面
                val intent = Intent()
                intent.setClass(this@MainActivity, History_record::class.java)
                startActivity(intent)
            }
        })


        //Reset Button
        mBtn_resetButton = findViewById(R.id.button3)
        mBtn_resetButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {

                //避免按好幾次
                if(flag_apple_finish!=true || flag_banana_finish!=true || flag_orange_finish!=true || flag_pineapple_finish!=true){
                    ToastUtil().showToast(this@MainActivity, "遊戲進行中!!!")
                    return
                }
                ToastUtil().showToast(this@MainActivity, "重置遊戲!!!")
                capital = 10000
                winner = null
                betmoney = null
                bethorsename = null
                ratio_apple = 2.0
                ratio_banana = 2.0
                ratio_orange = 2.0
                ratio_pineapple = 2.0
                //重置table
                mDBHelper!!.drop_table()
                mDBHelper!!.checkTable()
            }
        })

        //update information continuously
        Text2 = findViewById(R.id.txt3)
        val t: Thread = Thread(runnable)
        t.start()

        mBtn_Game_Over_Button = findViewById(R.id.button4)
        mBtn_Game_Over_Button.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                //避免按好幾次
                if(flag_apple_finish!=true || flag_banana_finish!=true || flag_orange_finish!=true || flag_pineapple_finish!=true){
                    ToastUtil().showToast(this@MainActivity, "遊戲進行中!!!")
                    return
                }
                ToastUtil().showToast(this@MainActivity, "遊戲結束!!!")
                capital = 10000
                winner = null
                betmoney = null
                bethorsename = null
                ratio_apple = 2.0
                ratio_banana = 2.0
                ratio_orange = 2.0
                ratio_pineapple = 2.0
                //重置table
                mDBHelper!!.drop_table()
                mDBHelper!!.checkTable()
                finish()
            }
        })

    }


    //不斷更新資訊的thread
    private val runnable = Runnable {
        try {
            while (true) {
                Thread.sleep(500)
                runOnUiThread {
                    Text2.text = "新台幣: "+capital.toString()
                    ratio1.text = String.format("%.1f", ratio_apple)
                    ratio2.text = String.format("%.1f", ratio_banana)
                    ratio3.text = String.format("%.1f", ratio_orange)
                    ratio4.text = String.format("%.1f", ratio_pineapple)
                }
            }
        } catch (e: InterruptedException) {
        }
    }


    //遊戲開始的thread
    fun start_game(){
        flag_apple_finish = false
        flag_banana_finish = false
        flag_orange_finish = false
        flag_pineapple_finish = false
        val horse1: horse = horse("apple", Progbar1)
        val horse2: horse = horse("banana", Progbar2)
        val horse3: horse = horse("orange", Progbar3)
        val horse4: horse = horse("pineapple", Progbar4)
        horse1.start()
        horse2.start()
        horse3.start()
        horse4.start()
    }


    //Class used in the game
    class horse(var horsename: String, var Pgrbar: ProgressBar, var mile: Int = 0) : Thread(){
        var ratio = when(horsename){
            "apple"-> ratio_apple
            "banana" -> ratio_banana
            "orange" -> ratio_orange
            else -> ratio_pineapple
        }
        val gain: Double = 0.1
        fun horsewin(){
            if((ratio-gain)>=2) ratio = ratio-gain
            //println("$horsename: 是贏家")
            //println("${horsename}賠率: $ratio\n")
        }

        fun horselose(){
            if((ratio+gain)<=5) ratio = ratio+gain
            //println("$horsename: 是輸家")
            //println("${horsename}賠率: $ratio\n")
        }

        fun update_ratio_flag(){
            when (horsename) {
                "apple" -> {ratio_apple = ratio
                            flag_apple_finish = true}
                "banana" -> {ratio_banana = ratio
                            flag_banana_finish = true}
                "orange" -> {ratio_orange = ratio
                            flag_orange_finish = true}
                else -> {ratio_pineapple = ratio
                        flag_pineapple_finish = true}
            }
        }

        //比賽之後結果結算
        @Synchronized fun aftergame(){
            mile++
            Pgrbar.progress = mile
            if(winner==null){
                //winner
                winner = horsename
                if(winner == bethorsename){
                    earn = ((betmoney!!.toDouble())*ratio* currency).toInt()
                    capital = capital + earn!!  //到時候要乘上匯率
                }
                else{
                    earn = 0
                    capital = capital - (betmoney!!.toDouble()* currency).toInt()  //到時候要乘上匯率
                }
                horsewin()
            }
            else{
                //Loser
                horselose()
            }
            update_ratio_flag()
        }
        //比賽開始
        override fun run() {
            //初始化比賽數據
            winner = null
            Pgrbar.progress = 0
            mile = 0

            //max等於20
            while(mile<19){
                sleep((0..2000).random().toLong())
                mile++
                Pgrbar.progress = mile
            }
            sleep((0..2000).random().toLong())
            aftergame()
        }
    }
}