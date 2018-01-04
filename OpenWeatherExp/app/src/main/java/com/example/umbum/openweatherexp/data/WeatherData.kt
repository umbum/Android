package com.example.umbum.openweatherexp.data

import com.google.gson.annotations.SerializedName

/* GSON을 이용해 불러온 json은 json에서 불러오고 싶은 데이터 이름과
동일한 이름을 프로퍼티로 하는 class 또는 Map에 담을 수 있다.
* json parsing library에는 jackson등도 있음. */

/* @SerializedName
json의 데이터 객체가 숫자로 시작한다던지 해서 코틀린 명명법에 어긋나는 경우, 해당 어노테이션을 사용한다.
그러나 주의해야 할 점이, 원래 GSON은 json에 없는 이름을 가져오도록 지정하면 그냥 null을 가져오게 되지만
이 어노테이션을 붙인 데이터가 json에 없는 경우 어플리케이션이 뻗어버린다는 점이다.
이럴 때는 그냥 Map으로 처리하면 된다. 그래서 "snow":{"3h":"0.3"}은 Map으로 처리했다. rain도 마찬가지.
 */
data class DayData(@SerializedName("weather") private val _weather: ArrayList<WeatherData>,
                   val main: MainData,
                   val wind: WindData,
                   val clouds: CloudData,
                   val id: String,
                   var name: String) {
    val weather
        get() = _weather[0]
}

data class WeatherData(val main: String, val description: String, val icon: String)
data class MainData(val temp: String, val temp_min: String, val temp_max: String, val humidity: String)
data class WindData(val speed: String)
data class CloudData(val all: String)


data class WeekList(val list: ArrayList<WeekData>)
data class WeekData(val dt: String,
                    val main: MainData,
                    @SerializedName("weather") private val _weather: ArrayList<WeatherData>,
                    val clouds: CloudData,
                    val snow: Map<String, String>?,
                    val rain: Map<String, String>?,
                    val dt_txt: String) {
    val weather
        get() = _weather[0]
}

data class WeatherForecast(val day: DayData, val week: WeekList, val iconUrl: String)
