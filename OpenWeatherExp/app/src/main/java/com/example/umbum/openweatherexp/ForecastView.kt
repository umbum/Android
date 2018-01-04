package com.example.umbum.openweatherexp

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.example.umbum.openweatherexp.data.WeekData
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_week_weather_item.view.*
import java.text.SimpleDateFormat
import java.util.*

/** Custom View **/
class ForecastView: LinearLayout {
    constructor(context: Context): super(context)
    constructor(context: Context, attributes: AttributeSet): super(context, attributes)

    val forecastItemView = LinearLayout(context)
    val scrollView = HorizontalScrollView(context)

    init {
        scrollView.scrollBarSize = 2
        scrollView.addView(forecastItemView)
        this.addView(scrollView)
    }

    fun setView(data: ArrayList<WeekData>, icon_url: String) {
        forecastItemView.removeAllViews()
        val start = getStartDataIndex(data)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        for (i in start..(start+15)) {
            val scrollItemView = inflater.inflate(R.layout.layout_week_weather_item, null, false)
            // kotlinx
            with (scrollItemView) {
                week.text = SimpleDateFormat("dd일 HH시", Locale.KOREA).format(data[i].dt.toLong() * 1000L)
                Picasso.with(context).load(icon_url+data[i].weather.icon+".png").into(weather_icon)
                avg_temp.text = "${data[i].main.temp} \u2103"
            }
            forecastItemView.addView(scrollItemView)
        }
    }

    private fun getStartDataIndex(data: ArrayList<WeekData>): Int {
        val current = Date().time
        for (i in 0 until data.size) {
            if (current < data[i].dt.toLong()) return i
        }
        return 0
    }

}