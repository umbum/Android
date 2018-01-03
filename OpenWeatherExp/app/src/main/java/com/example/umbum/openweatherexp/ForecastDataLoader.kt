package com.example.umbum.openweatherexp

import android.content.Context
import android.support.v4.content.AsyncTaskLoader
import com.example.umbum.openweatherexp.data.CityData
import com.example.umbum.openweatherexp.data.DayData
import com.example.umbum.openweatherexp.data.WeatherForecast
import com.example.umbum.openweatherexp.data.WeekList
import com.google.gson.Gson
import java.net.URL

const val API_KEY = "dd95f58323b4f2fa7b0478a69c88dd01"
const val WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?id="
const val FORECAST_URL = "http://api.openweathermap.org/data/2.5/forecast?id="
const val ICON_URL = "http://openweathermap.org/img/w/"

class ForecastDataLoader(context: Context, val cities: ArrayList<CityData>)
    : AsyncTaskLoader<ArrayList<WeatherForecast>>(context) {

    override fun loadInBackground(): ArrayList<WeatherForecast> {
        log("ForecastDataLoader.loadInBackground start")
        val city_weather = ArrayList<WeatherForecast>()
        for (city in cities) {
            val weatherUrl = WEATHER_URL+city.api_id+"&units=metric&APPID=${API_KEY}"
            // 원래 InputStreamreader, openStream 해야 하지만 .readText() 확장 함수를 사용하면 간단히 처리 가능.
            val weatherJson = URL(weatherUrl).readText()
            val day = Gson().fromJson(weatherJson, DayData::class.java)
            day.name = city.name // day.name은 영문이니까.
            log("Daydata:"+day.weather.description)
//            day.id = city.id // 할 필요 없어 보이는데 refac.

            val forecastURL = FORECAST_URL+city.api_id+"&units=metric&APPID=${API_KEY}"
            val forecastJson = URL(forecastURL).readText()
            val week = Gson().fromJson(forecastJson, WeekList::class.java)
            log("WeekData: : " + week.list[0])
            val forecast = WeatherForecast(day, week, ICON_URL)
            city_weather.add(forecast)
        }
        log("ForecastDataLoader.loadInBackground return")
        return city_weather
    }
}
