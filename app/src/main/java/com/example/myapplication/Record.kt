package com.example.myapplication

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor




class Record(var context: Context?, var name: String?, var factory: SQLiteDatabase.CursorFactory?, var version: Int, var table_name: String
) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(p0: SQLiteDatabase?) {
        var SQL: String = "CREATE TABLE IF NOT EXISTS " + table_name + "( " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "BETHORSE TEXT, " +
                "BETMONEY INTEGER, "+
                "WINNER TEXT," +
                "EARN INTEGER," +
                "CAPITAL INTEGER" +
                ");"
            p0!!.execSQL(SQL)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        var SQL: String = "DROP TABLE "+ table_name
        p0!!.execSQL(SQL)
    }

    fun checkTable() {
        val cursor: Cursor? = writableDatabase.rawQuery(
            "select DISTINCT tbl_name from sqlite_master where tbl_name = '" + table_name + "'",
            null
        )
        if (cursor != null) {
            if (cursor.getCount() === 0) writableDatabase.execSQL(
                "CREATE TABLE IF NOT EXISTS " + table_name + "( " +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "BETHORSE TEXT, " +
                        "BETMONEY INTEGER, "+
                        "WINNER TEXT," +
                        "EARN INTEGER," +
                        "CAPITAL INTEGER" +
                        ");"
            )
            cursor.close()
        }
    }

    //取得有多少資料表
    fun getTables(): ArrayList<String>? {
        val cursor: Cursor = writableDatabase.rawQuery(
            "select DISTINCT tbl_name from sqlite_master", null
        )
        val tables: ArrayList<String> = ArrayList()
        while (cursor.moveToNext()) {
            val getTab: String = String(cursor.getBlob(0))
            if (getTab.contains("android_metadata")) {
            } else if (getTab.contains("sqlite_sequence")) {
            } else tables.add(getTab.substring(0, getTab.length - 1))
        }
        return tables
    }

    //新增資料 ok
    fun addData(bethorse: String?, betmoney: Int?, winner: String?, earn: Int?, capital: Int?) {
        val db = writableDatabase
        val values = ContentValues()
        values.put("BETHORSE", bethorse)
        values.put("BETMONEY", betmoney.toString())
        values.put("WINNER", winner)
        values.put("EARN", earn.toString())
        values.put("CAPITAL", capital.toString())
        db.insert(table_name, null, values)
    }

    //顯示所有資料 ok
    fun showAll(): ArrayList<HashMap<String, String>>? {
        val db = readableDatabase
        val c: Cursor = db.rawQuery(" SELECT * FROM $table_name", null)
        val arrayList: ArrayList<HashMap<String, String>> = ArrayList()
        while (c.moveToNext()) {
            val hashMap: HashMap<String, String> = HashMap()
            val _id: String = c.getString(0)
            val bethorse: String = c.getString(1)
            val betmoney: String = c.getString(2)
            val winner: String = c.getString(3)
            val earn: String = c.getString(4)
            val capital: String = c.getString(5)
            hashMap["ID"] = _id
            hashMap["BETHORSE"] = bethorse
            hashMap["BETMONEY"] = betmoney
            hashMap["WINNER"] = winner
            hashMap["EARN"] = earn
            hashMap["CAPITAL"] = capital
            arrayList.add(hashMap)
        }
        return arrayList
    }

    //以id搜尋特定資料 ok
    fun searchById(getId: Int): ArrayList<HashMap<String, String>>? {
        val db = readableDatabase
        val c: Cursor = db.rawQuery((" SELECT * FROM " + table_name.toString() + " WHERE _id =" + "'" + getId.toString() + "'"), null)
        val arrayList: ArrayList<HashMap<String, String>> = ArrayList()
        while (c.moveToNext()) {
            val hashMap: HashMap<String, String> = HashMap()
            val _id: String = c.getString(0)
            val bethorse: String = c.getString(1)
            val betmoney: String = c.getString(2)
            val winner: String = c.getString(3)
            val earn: String = c.getString(4)
            val capital: String = c.getString(5)
            hashMap["ID"] = _id
            hashMap["BETHORSE"] = bethorse
            hashMap["BETMONEY"] = betmoney
            hashMap["WINNER"] = winner
            hashMap["EARN"] = earn
            hashMap["CAPITAL"] = capital
            arrayList.add(hashMap)
        }
        return arrayList
    }

    //以興趣篩選資料
    fun searchByHobby(getHobby: String): ArrayList<HashMap<String, String>>? {
        val db = readableDatabase
        val c: Cursor = db.rawQuery(
            (" SELECT * FROM " + table_name
                .toString() + " WHERE Hobby =" + "'" + getHobby + "'"), null
        )
        val arrayList: ArrayList<HashMap<String, String>> = ArrayList()
        while (c.moveToNext()) {
            val hashMap: HashMap<String, String> = HashMap()
            val id: String = c.getString(0)
            val name: String = c.getString(1)
            val phone: String = c.getString(2)
            val hobby: String = c.getString(3)
            val elseInfo: String = c.getString(4)
            hashMap["id"] = id
            hashMap["name"] = name
            hashMap["phone"] = phone
            hashMap["hobby"] = hobby
            hashMap["elseInfo"] = elseInfo
            arrayList.add(hashMap)
        }
        return arrayList
    }

    //修改資料(麻煩)
    fun modify(id: String, name: String, phone: String, hobby: String, elseInfo: String) {
        val db = writableDatabase
        db.execSQL(
            (" UPDATE " + table_name
                .toString() + " SET Name=" + "'" + name + "'," + "Phone=" + "'" + phone + "'," + "Hobby=" + "'" + hobby + "'," + "ElseInfo=" + "'" + elseInfo + "'" + " WHERE _id=" + "'" + id + "'")
        )
    }

    //修改資料(簡單)
    fun modifyEZ(id: String, name: String?, phone: String?, hobby: String?, elseInfo: String?) {
        val db = writableDatabase
        val values = ContentValues()
        values.put("Name", name)
        values.put("Phone", phone)
        values.put("Hobby", hobby)
        values.put("ElseInfo", elseInfo)
        db.update(table_name, values, "_id = $id", null)
    }

    //刪除全部資料 table保留只刪除資料 ok
    fun deleteAll() {
        val db = writableDatabase
        db.execSQL("DELETE FROM $table_name")
    }

    //Drop talbe ok
    fun drop_table() {
        val db = writableDatabase
        db.execSQL("DROP TABLE "+ table_name)
    }

    //以id刪除資料(簡單) ok
    fun deleteByIdEZ(id: Int) {
        val db = writableDatabase
        db.delete(table_name, "_id = $id", null)
    }
}