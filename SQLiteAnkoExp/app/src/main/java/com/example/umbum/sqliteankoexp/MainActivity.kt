/** 다음 항목을 포함하는 예제
 * anko-sqlite (+ native sqlite) / CursorAdapter
 * ListView & ViewHolder 패턴 ( RecyclerView를 사용하는게 더 낫다. )
 * ActionBar 비활성화, ToolBar 사용.
 * MediaStore 접근 시 EXTERNAL_STORAGE 권한 요청, 권한 체크, 암시적 인텐트
 */


package com.example.umbum.sqliteankoexp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import com.example.umbum.sqliteankoexp.DB.DBHandler
import android.util.Log

inline fun log(s: String) {
    Log.d("UMBUM_DEBUG", s)
}

class MainActivity : AppCompatActivity() {
    companion object{
        const val REQUEST_ADD_USER = 1001
    }
    lateinit private var mAdapter: CursorAdapterExp
    var mDBHandler = DBHandler(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 5.0부터 제공되는 ActionBar 대신, 하위 호환성을 위해 직접 만든 ToolBar를 사용하겠다고 선언.
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val cursor = mDBHandler.getUserAllWithCursor()
        mAdapter = CursorAdapterExp(this, cursor)
        val listView = findViewById(R.id.user_list) as ListView
        // 이렇게 view의 adapter에 달아주기만 해도, mAdapter의 newView/bindView 메소드를 자동으로 실행하며 뷰를 그린다.
        listView.adapter = mAdapter
    }

    /* item이 선택됐을 때 호출되는 함수. */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.add_user -> {
                val intent = Intent(this, AddUserActivity::class.java)
                /* 다음 메소드를 사용하면 위에 쌓이는 Activity에서 실행 결과를 받아볼 수 있다.
                * 실행 결과는 onActivityResult로 자동으로 전송됨. */
                startActivityForResult(intent, REQUEST_ADD_USER)
            }
            R.id.anko -> {
                val layout = Intent(this, AnkoDSLActivity::class.java)
                startActivity(layout)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /** startActivityForResult를 실행하면서 REQUEST_ADD_USER가 onActivityResult로 전송됨.
     * 데이터를 다시 가져와 뷰를 refresh하는 작업을 처리한다. */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_ADD_USER -> {
                val cursor = mDBHandler.getUserAllWithCursor()
                mAdapter.changeCursor(cursor)
                /* 데이터가 변경되면, Adapter가 ListView에 데이터가 변경되었으니 refresh하라고 알려야 한다.
                아래 두 함수 중 하나를 호출해주면 된다. */
                mAdapter.notifyDataSetInvalidated()
//                mAdapter.notifyDataSetChanged()
            }
        }
    }

    fun onClickDelete(v: View) {
        mDBHandler.deleteUser(v.tag as Long)
        val cursor = mDBHandler.getUserAllWithCursor()
        mAdapter.changeCursor(cursor)
    }

    /* menu를 만드는 함수 */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // menuInflater는 AppCompatActivity에 정의되어 있음.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    /* 화면 갱신 시 호출되는 함수. 옵션에 따라 특정 메뉴를 보일지 말지 등을 넣으면 좋다. */
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onDestroy() {
        super.onDestroy()
        mAdapter.cursor?.close()
        mDBHandler.close()
    }

}
