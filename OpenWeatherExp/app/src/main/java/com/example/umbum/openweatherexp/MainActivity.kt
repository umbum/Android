/** 다음을 포함하는 예제
 * RecyclerView & ArrayAdapter
 * GSON + @SerializedName
 * anko-sqlite
 * openweather API
 * AsyncTaskLoader ( background task ) support.v4에 있는 거니까 다른거 import하지 않게 주의.
 * Custom View
 **/
package com.example.umbum.openweatherexp

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.umbum.openweatherexp.data.CityData
import com.example.umbum.openweatherexp.data.WeatherForecast
import com.example.umbum.openweatherexp.db.DBHandler
import kotlinx.android.synthetic.main.activity_main.*


const val LOADER_ID = 101010
const val SELECTED_CITY = 1100
const val REQUEST_CITY = 1101

inline fun log(s: String) {
    Log.d("UMBUM_DBG", s)
}

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<ArrayList<WeatherForecast>>{

    // refac.
    var mAdapter: WeatherListViewAdapter? = null
    var mWeatherData: ArrayList<WeatherForecast>? = null
    val mCityArray = ArrayList<CityData>()
    val mDBHandler = DBHandler.getInstance(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        log("MainActivity.onCreate()")
        mCityArray.addAll(mDBHandler.getCityDataAll())
        log(mCityArray.toString())

        // Loader를 생성하면서 onCreateLoader를 실행. + 결과 callback을 받을 클래스를 지정.(onLoadFinished)
        supportLoaderManager.initLoader(LOADER_ID, null, this)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<ArrayList<WeatherForecast>> {
        log("MainActivity:AsyncTaskLoader.onCreateLoader start")
        val loader = ForecastDataLoader(this, mCityArray)
        loader.forceLoad()
        progress_bar.visibility = View.VISIBLE
        // 여기 뭐야???? refac.
        log("MainActivity:AsyncTaskLoader.onCreateLoader return")
        return loader
    }


    // Loader의 실행이 끝나면 실행되는 callback method.
    override fun onLoadFinished(loader: Loader<ArrayList<WeatherForecast>>?, data: ArrayList<WeatherForecast>) {
        log("MainActivity:AsyncTaskLoader.onLoadFinished start")
        if (mAdapter == null) {
            log("MainActivity.mAdapter init")
            mAdapter = WeatherListViewAdapter(this, data)
            mAdapter!!.setDeleteClickListener() { view ->
                mDBHandler.deleteCity(view.tag as String)
                mAdapter!!.removeData(view.tag as String)
            }
            weather_list.adapter = mAdapter
            weather_list.layoutManager = LinearLayoutManager(this)
            mWeatherData = data
        }
//        refac. 여기서 addAll을 왜해??
//        mWeatherData?.addAll(data)
        progress_bar.visibility = View.GONE
        mAdapter?.updateData(data)
        log("MainActivity:AsyncTaskLoader.onLoadFinished done")
    }

    override fun onLoaderReset(p0: Loader<ArrayList<WeatherForecast>>?) { }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        // refac. 책에는 여기가 return true로 되어 있ㄴ느데 뭐냐?
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_city -> {
                val intent = Intent(this, SelectCityActivity::class.java)
                startActivityForResult(intent, REQUEST_CITY)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            SELECTED_CITY -> {
                mCityArray.clear()
                mCityArray.addAll(mDBHandler.getCityDataAll())
                log("restartLoader")
                supportLoaderManager.restartLoader(LOADER_ID, null, this)
            }
        }
    }

}

