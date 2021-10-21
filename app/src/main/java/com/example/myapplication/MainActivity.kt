package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import java.lang.Exception
import android.content.Intent
import okhttp3.*



var winner: String? = null                     //獲勝馬匹
var betmoney: Int? = null                      //下注金
var capital: Int = 10000                       //賭金
var bethorsename: String? = null               //下注馬匹
var ratio_apple: Double = 2.0
var ratio_banana: Double = 2.0
var ratio_orange: Double = 2.0
var ratio_pineapple: Double = 2.0
var earn: Int? = null                           //獎金
var mDBHelper: Record? = null                   //資料庫的參照(Record class)
var flag_apple_finish: Boolean = true
var flag_banana_finish: Boolean = true
var flag_orange_finish: Boolean = true
var flag_pineapple_finish: Boolean = true



class MainActivity : AppCompatActivity() {

    private lateinit var mBtnButton: Button
    private lateinit var mBtn_historyButton: Button
    private lateinit var mBtn_resetButton: Button
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


        //Start Game Button
        mBtnButton = findViewById(R.id.button1)
        mBtnButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {

                //遊戲開始前(check: 1.有沒有輸入金額  2.金額有沒有大於10 3.遊戲是否進行中  4.錢夠不夠)

                var previous_betmonney: Int? = betmoney //先把錢一把的賭金存起來, 因為下面會改到

                //取下注金額: betmoney
                try{
                    Text1 = findViewById(R.id.edittext)
                    betmoney = Text1.text.toString().trim().toInt()
                }catch (e: Exception){
                    betmoney = previous_betmonney
                    Toast.makeText(this@MainActivity, "請輸入下注金額!!!", Toast.LENGTH_SHORT).show()
                    return
                }

                //避免按好幾次
                if(flag_apple_finish!=true || flag_banana_finish!=true || flag_orange_finish!=true || flag_pineapple_finish!=true || betmoney!! > 10 || capital < betmoney!! || betmoney==0){
                    if(flag_apple_finish!=true || flag_banana_finish!=true || flag_orange_finish!=true || flag_pineapple_finish!=true)
                        Toast.makeText(this@MainActivity, "遊戲進行中!!!", Toast.LENGTH_SHORT).show()
                    if(capital < betmoney!!)
                        Toast.makeText(this@MainActivity, "錢不夠玩!!!", Toast.LENGTH_SHORT).show()
                    if(betmoney!!>10)
                        Toast.makeText(this@MainActivity, "超過10元", Toast.LENGTH_SHORT).show()
                    if(betmoney==0)
                        Toast.makeText(this@MainActivity, "請輸入下注金額", Toast.LENGTH_SHORT).show()
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
                var RadioBtm: RadioButton = findViewById(RadioB1.getCheckedRadioButtonId())
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
                    Toast.makeText(this@MainActivity, "遊戲進行中!!!", Toast.LENGTH_SHORT).show()
                    return
                }

                //把上一把的遊戲資訊存入DB
                if(winner!=null){
                    mDBHelper!!.addData(bethorse = bethorsename, betmoney = betmoney, winner = winner, earn = earn, capital = capital)
                    winner = null
                }

                Toast.makeText(this@MainActivity, "查詢歷史紀錄!!!", Toast.LENGTH_SHORT).show()

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
                    Toast.makeText(this@MainActivity, "遊戲進行中!!!", Toast.LENGTH_SHORT).show()
                    return
                }

                Toast.makeText(this@MainActivity, "重置遊戲!!!", Toast.LENGTH_SHORT).show()
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
    }



    //不斷更新資訊的thread
    private val runnable = Runnable {
        try {
            while (true) {
                Thread.sleep(500)
                runOnUiThread {
                    Text2.text = capital.toString()
                    ratio1.text = ratio_apple.toString()
                    ratio2.text = ratio_banana.toString()
                    ratio3.text = ratio_orange.toString()
                    ratio4.text = ratio_pineapple.toString()
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
        var horse1: horse = horse("apple", Progbar1)
        var horse2: horse = horse("banana", Progbar2)
        var horse3: horse = horse("orange", Progbar3)
        var horse4: horse = horse("pineapple", Progbar4)
        horse1.start()
        horse2.start()
        horse3.start()
        horse4.start()
    }


    //Class used in the game
    class horse(var horsename: String, var Pgrbar: ProgressBar, var mile: Int = 0, var ratio: Double = 2.0) : Thread(){
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
                    earn = ((betmoney!!.toDouble())*ratio).toInt()
                    capital = capital + earn!!  //到時候要乘上匯率
                }
                else{
                    earn = 0
                    capital = capital - betmoney!!  //到時候要乘上匯率
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