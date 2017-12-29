package com.example.umbum.sqliteankoexp.DB

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.jetbrains.anko.db.*


enum class UserColumns(val idx: Int) {
    _id(0),
    NAME(1),
    AGE(2),
    TELNUM(3),
    PIC_PATH(4)
}

data class UserInfo(val name:String = "No Name",
                    val age:String = "0",
                    val TelNum:String = "No TelNum",
                      val pic_path:String)

/* Anko를 사용해 작성한 DBHandler */
class DBHandler(context: Context): SQLiteOpenHelper(context, DB_Name, null, DB_Version) {
    companion object {
        val DB_Name = "user.db"
        val DB_Version = 1
    }

    val TABLE_NAME = "USER"
    val ID = "_id"
    val NAME = "NAME"
    val AGE = "AGE"
    val TELNUM = "TELNUM"
    val PIC_PATH = "PIC_PATH"

    /* Anko DSL */
    override fun onCreate(db: SQLiteDatabase) {
        db.createTable(TABLE_NAME, true,
                ID to INTEGER+PRIMARY_KEY,
                NAME to TEXT,
                AGE to TEXT,
                TELNUM to TEXT,
                PIC_PATH to TEXT)
    }

    /* Cursor를 리턴한다. CursorAdapter에서 이 Cursor를 받아서 처리함. */
    fun getUserAllWithCursor(): Cursor {
        return readableDatabase.query(TABLE_NAME,
                arrayOf(ID, NAME, AGE, TELNUM, PIC_PATH),
                null, null, null, null, null)
    }

    /* Anko use 함수는 자원 반납을 자동으로 처리해주며, Thread-safety 하다. */
    fun addUser(user: UserInfo) {
        var info = ContentValues()
        info.put(NAME, user.name)
        info.put(AGE, user.age)
        info.put(TELNUM, user.TelNum)
        info.put(PIC_PATH, user.pic_path)
        writableDatabase.use {
            writableDatabase.insert(TABLE_NAME, null, info)
        }
    }

    fun deleteUser(id: Long) {
        writableDatabase.use {
            writableDatabase.execSQL("DELETE FROM ${TABLE_NAME} WHERE ${ID} = ${id};")
        }
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
    }
}

/* Anko를 사용하지 않는 방식 */
class DBHandler_native(context: Context): SQLiteOpenHelper(context, DB_Name, null, DB_Version) {
    companion object {
        val DB_Name = "user.db"
        val DB_Version = 1
    }
    val TABLE_NAME = "user"
    val ID = "_id"
    val NAME = "name"
    val AGE = "age"
    val TELNUM = "telnum"
    val PIC_PATH = "pic_path"

    val TABLE_CREATE = "CREATE TABLE if not exists " + TABLE_NAME + "(" +
            "${ID} integer PRIMARY KEY, " +
            "${NAME} text," +
            "${AGE} text, " +
            "${TELNUM} text, " +
            "${PIC_PATH} text" + ")"

    fun getUserAllWithCursor(): Cursor {
        return readableDatabase.query(TABLE_NAME, arrayOf(ID, NAME, AGE, TELNUM, PIC_PATH),
                null, null, null, null, null)
    }

    fun addUser(user: UserInfo) {
        var info = ContentValues()
        info.put(NAME, user.name)
        info.put(AGE, user.age)
        info.put(TELNUM, user.TelNum)
        info.put(PIC_PATH, user.pic_path)
        writableDatabase.insert(TABLE_NAME, null, info)
    }

    fun derleteUser(id: Long) {
        writableDatabase.execSQL("DELETE FROM ${TABLE_NAME} WHERE ${ID} = ${id};")
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(TABLE_CREATE)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
    }

}
