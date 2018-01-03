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

    val mainView = LinearLayout(context)
    val scrollView = HorizontalScrollView(context)

    init {
        scrollView.scrollBarSize = 2
        scrollView.addView(mainView)
//        this.addView 맞지? 책에는 그냥 addView로 되어 있어서. refac.
        addView(scrollView)
    }

    private fun getForecastDate(time: Long): String {
        val format = SimpleDateFormat("dd일 HH시", Locale.KOREA)
        return format.format(time * 1000L)
    }


    private fun getDataIndex(data: ArrayList<WeekData>): Int {
        val current = Date().time
        for (i in 0 until data.size) {
            if (current < data[i].dt.toLong()) return i
        }
        return 0
    }

    fun setView(data: ArrayList<WeekData>, icon_url: String) {
        val start = getDataIndex(data)
        mainView.removeAllViews()
        for (i in start..(start+15)) {
            val layout = createItemLayout()
            // kotlinx
            with (layout) {
                week.text = getForecastDate(data[i].dt.toLong())
                Picasso.with(context).load(icon_url+data[i].weather.icon+".png").into(weather_icon)
                avg_temp.text = "${data[i].main.temp} \u2103"
            }
            mainView.addView(layout)
        }
    }

    private fun createItemLayout(): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return inflater.inflate(R.layout.layout_week_weather_item, null, false)
    }

}