package com.example.umbum.openweatherexp.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.umbum.openweatherexp.data.CityData
import com.example.umbum.openweatherexp.log
import org.jetbrains.anko.db.*

enum class CityColumns(val idx: Int) {
    API_ID(0),
    NAME(1)
}

const val DB_NAME = "user.db"
const val DB_VERSION = 1


//refac. 중복된 city 추가하면 중복 그대로 들어가버림. 그래서 api_id를 primary key로 사용하면 이럴 일이 없을 것 같은데;;
class DBHandler private constructor(context: Context)
    : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object {
        @Volatile private var INSTANCE: DBHandler? = null

        fun getInstance(context: Context): DBHandler =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: DBHandler(context).also { INSTANCE = it }
                }
    }

    val TABLE_NAME = "city"
    val ID = "_id"
    val API_ID = "api_id"
    val NAME = "name"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.createTable(TABLE_NAME, true,
                 ID to INTEGER+PRIMARY_KEY,
                API_ID to TEXT,
                NAME to TEXT)
    }

    fun getCityDataAll(): ArrayList<CityData> {
        val data = ArrayList<CityData>()
        log("before query")
        val cursor = readableDatabase.query(TABLE_NAME,
                arrayOf(API_ID, NAME), null, null, null, null, null)
        log("after query")
        if (cursor.count == 0) return data

        cursor.moveToFirst()
        do {
            val city = CityData(
                    cursor.getString(CityColumns.API_ID.ordinal),
                    cursor.getString(CityColumns.NAME.ordinal))
            data.add(city)
        } while (cursor.moveToNext())
        return data
    }

    fun saveCity(city: CityData) {
        writableDatabase.use {
            writableDatabase.insert(TABLE_NAME, null,
                    ContentValues().apply {
                        put(API_ID, city.api_id)
                        put(NAME, city.name)
                    })
        }
    }

    fun deleteCity(api_id: String) {
        writableDatabase.use {
            writableDatabase.execSQL(
                    "DELETE FROM ${TABLE_NAME} WHERE ${API_ID} = ${api_id};"
            )
        }
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
    }
}

