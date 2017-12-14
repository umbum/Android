package com.example.umbum.recyclerviewexp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_detail.*
import java.io.InputStreamReader


data class DetailData(val name: String,
                            val base: String,
                            val recipe: String,
                            val tip: String)

data class GsonData(val data: ArrayList<DetailData>)

class DetailActivity : AppCompatActivity() {
    // intent.putExtra()로 데이터를 넘겨받을 경우 클래스 내부의 static 변수가 필요.
    companion object {
        val EXTRA_COCK_NAME = "cocktail_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

//         intent로 받은 EXTRA_COCK_NAME을 가져와서 그를 기준으로 detail 데이터를 가져온다.
        val cock_name = intent.getStringExtra(EXTRA_COCK_NAME)
        /* GSON */
        val data = getDataFromName(cock_name)
        initView(data, cock_name)
    }

    private fun getDataFromName(selected: String): DetailData? {
        val gson = GsonBuilder().create()
        val reader = InputStreamReader(assets.open("cocktail_data.json"))
//         아예 여기서 객체들로 이루어진 컬렉션을 반환해준다.
        val detailDatas = gson.fromJson(reader, GsonData::class.java)

        for (data in detailDatas.data) {
            if (data.name == selected) return data
        }
        return null
    }

    private fun initView(data: DetailData?, name: String) {
//         view에 데이터를 채워넣기.
        detail_name.text = data?.name
        detail_base.text = data?.base
        detail_recipe.text = data?.recipe
        detail_tip.text = data?.tip
//         img_flag는 drawable인데, drawable에 접근할 수 있는 방법이 R 또는 XML 둘 밖에 없기 때문에
//         따로 채워 넣어야 한다.
        detail_img.setImageResource(getResourceId(name))
    }

    private fun getResourceId(selected: String): Int = when (selected) {
        "올드 패션드" -> {
            R.drawable.img_old_fashioned
        }
        else -> {
            R.drawable.img_null
        }
    }

}