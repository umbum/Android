package com.example.umbum.openweatherexp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import android.widget.TextView
import com.example.umbum.openweatherexp.data.CityArray
import com.example.umbum.openweatherexp.data.CityData
import com.example.umbum.openweatherexp.db.DBHandler
import com.google.gson.Gson
import java.io.InputStreamReader

class AddCityActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_city)

        val reader = InputStreamReader(assets.open("city_list.json"))
        val cityData = Gson().fromJson(reader, CityArray::class.java)

        val adapter = CityListAdapter(this, cityData.city)
        val city_list = findViewById(R.id.city_list) as ListView
        city_list.adapter = adapter
        city_list.setOnItemClickListener { adapterView, view, i, l ->
            val text = view.findViewById(R.id.city_name) as TextView
            saveData(view.tag as String, text.text as String)
            setResult(ADD_CITY)
            finish()
        }
    }

    fun saveData(api_id: String, name: String) {
        log("saveData : " + api_id)
        log("saveData : " + name)
        val db = DBHandler.getInstance(this)
        db.saveCity(CityData(api_id, name))
    }
}
