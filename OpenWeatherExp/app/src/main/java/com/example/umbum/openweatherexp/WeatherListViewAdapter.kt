package com.example.umbum.openweatherexp

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.example.umbum.openweatherexp.data.WeatherForecast
import com.squareup.picasso.Picasso

class ViewHolder(v: View): RecyclerView.ViewHolder(v) {
    val descript = v.findViewById(R.id.descript) as TextView
    val weatherIcon = v.findViewById(R.id.weather_icon) as ImageView
    val currentTemp = v.findViewById(R.id.current_temp) as TextView
    val highLowTemp = v.findViewById(R.id.high_low_temp) as TextView
    val cityName = v.findViewById(R.id.city_name) as TextView
    val humidity = v.findViewById(R.id.humidity) as TextView
    val cloud = v.findViewById(R.id.cloud) as TextView
    val wind = v.findViewById(R.id.wind) as TextView
    val forecast = v.findViewById(R.id.forecast) as ForecastView
    val delBtn = v.findViewById(R.id.del_btn) as ImageButton

    fun bindHolder(context: Context, data: WeatherForecast, delClick: (View) -> Unit) {
        with (data) {
            descript.text = day.weather.description
            Picasso.with(context).load(iconUrl+day.weather.icon+".png").into(weatherIcon)
            currentTemp.text = "${day.main.temp} \u2103"
            highLowTemp.text = "${day.main.temp_min} \u2103 / ${day.main.temp_max} \u2103"
            cityName.text = day.name
            cloud.text = "${day.clouds.all} %"
            humidity.text = "${day.main.humidity} %"
            wind.text = day.wind.speed

            forecast.setView(week.list, iconUrl)
            delBtn.setOnClickListener(delClick)
            delBtn.tag = day.id
        }
    }
}

/* 추후 data를 update하더라도, 일단 생성자로 data를 받는게 다른 Adapter 형식과의 일관성을 유지할 수 있는 듯. */
class WeatherListViewAdapter(private val context: Context, data: ArrayList<WeatherForecast>)
    : RecyclerView.Adapter<ViewHolder>() {
    lateinit var delBtnClickListener: (View) -> Unit
    val mItemList: ArrayList<WeatherForecast> = data

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val cardItemView = inflater.inflate(R.layout.layout_card, parent, false)
        return ViewHolder(cardItemView)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindHolder(context, mItemList[position], delBtnClickListener)
    }

    override fun getItemCount(): Int = mItemList.size

    // setter가 존재하기는 하지만, =으로 대입해야 하니까 다른 listener 함수 처럼 함수 형태로 제공하기 위해.
    fun setDeleteClickListener(listener: (View) -> Unit) {
        delBtnClickListener = listener
    }

    fun updateData(newData: ArrayList<WeatherForecast>) {
        mItemList.clear()
        mItemList.addAll(newData)
        notifyDataSetChanged()
    }

    fun removeData(api_id: String) {
        mItemList.remove(mItemList.find { it.day.id == api_id })
        notifyDataSetChanged()
    }

}