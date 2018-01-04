/**
 * 다음 항목을 포함하는 예제.
 * RecyclerView : RecyclerView.Adapter<ViewHolder>와 ViewHolder
 * XML nested TypedArray : MainActivity의 resources는 이 방식으로 처리.
 * JSON -> GSON parsing  : DetailActivity의 resources는 이 방식으로 처리.
 * kotlin-android-extensions(kotlinx) : findViewById() 대체
 */
package com.example.umbum.recyclerviewexp

import android.content.Intent
import android.content.res.TypedArray
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.TextView


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*** decompose nested TypedArray ***/
        val cocktailArr = resources.obtainTypedArray(R.array.cocktails)
        var cock: TypedArray
        val ItemDataList = mutableListOf<ItemData>()
        for (i in 0 until cocktailArr.length()) {
            val resId = cocktailArr.getResourceId(i, -1)

            cock = resources.obtainTypedArray(resId)
            ItemDataList.add(
                    ItemData(
                            cock.getResourceId(0, R.drawable.img_null),
                            cock.getString(1),
                            cock.getString(2)
                    )
            )
        }
        val adapter = RecyclerAdapter(this, ItemDataList)
        /* 아래 방식은 리소스를 분리해놓지 않아 유지보수에 불리하다.  */
//        val adapter = RecyclerAdapter(this, listOf(
//                ItemData(R.drawable.img_faust, "파우스트", "럼"),
//                ItemData(R.drawable.img_old_fashioned, "올드 패션드", "위스키"),
//                ...
//                ))

        /* 이건 예제니까 람다를 여기다가 적었지만,
        Adapter에 연결된 리스너가 하나이니까 Adapter 클래스에서 람다로 바로 만들어 붙이는게 더 깔끔하다.
        상황에 따라 람다 이외에도 객체 식이나, onClick을 직접 implements하는 방법을 생각해볼 수 있다.*/
        adapter.setOnItemClickListener { v ->
            val textView = v?.findViewById(R.id.name) as TextView?
            val name = textView?.text ?: "None"
            // click하면 DetailActivity를 시작하도록 Intent를 만들어 보낸다.
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_COCK_NAME, name)
            startActivity(intent)
        }

        val recycleListView = findViewById(R.id.cocktail_list) as RecyclerView
        recycleListView.layoutManager = LinearLayoutManager(this)
        recycleListView.adapter = adapter
    }

}
