/** 다음을 포함하는 예제
 * openweather API
 * RecyclerView & ArrayAdapter
 * GSON + @SerializedName
 * Singleton anko-sqlite with applicationContext
 * AsyncTaskLoader ( background task ) support.v4에 있는 거니까 다른거 import하지 않게 주의.
 * Custom View
 * Picasso로 네트워크 이미지 가져오기
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
const val ADD_CITY = 1100
const val SHOW_CITY_LIST = 1101

inline fun log(s: String) {
    Log.d("UMBUM_DBG", s)
}

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<ArrayList<WeatherForecast>>{
    var mAdapter: WeatherListViewAdapter? = null
    val mCityArray = ArrayList<CityData>()
    /* onCreate 이전에는 Activity Context가 제대로 초기화되기 이전이므로,
    this를 사용해 멤버에 접근하면 NPE가 발생할 수 있다.
    따라서 이 시점에서는 this를 통해 접근해야 하는 applicationContext를 사용할 수 없다. */
    lateinit var mDBHandler: DBHandler


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mDBHandler = DBHandler.getInstance(this)
        mCityArray.addAll(mDBHandler.getCityDataAll())
        log(mCityArray.toString())

        // Loader를 생성하면서 onCreateLoader를 실행. + 결과 callback을 받을 클래스를 지정.(onLoadFinished)
        supportLoaderManager.initLoader(LOADER_ID, null, this)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<ArrayList<WeatherForecast>> {
        log("MainActivity:AsyncTaskLoader.onCreateLoader")
        progress_bar.visibility = View.VISIBLE
        return ForecastDataLoader(this, mCityArray).also { it.forceLoad() }
        // ForecastDataLoader.loadInBackground가 실행.
    }

    // Loader의 실행이 끝나면 실행되는 callback method.
    override fun onLoadFinished(loader: Loader<ArrayList<WeatherForecast>>?, data: ArrayList<WeatherForecast>) {
        log("MainActivity:AsyncTaskLoader.onLoadFinished")
        if (mAdapter == null) {
            log("MainActivity.mAdapter init")
            mAdapter = WeatherListViewAdapter(this, data)
            mAdapter!!.setDeleteClickListener() { view ->
                mDBHandler.deleteCity(view.tag as String)
                mAdapter!!.removeData(view.tag as String)
            }
            weather_list.adapter = mAdapter
            weather_list.layoutManager = LinearLayoutManager(this)
        }
        else {
            mAdapter?.updateData(data)
        }
        progress_bar.visibility = View.GONE
        log("MainActivity:AsyncTaskLoader.onLoadFinished done")
    }

    override fun onLoaderReset(p0: Loader<ArrayList<WeatherForecast>>?) { }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_city -> {
                val intent = Intent(this, AddCityActivity::class.java)
                startActivityForResult(intent, SHOW_CITY_LIST)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            ADD_CITY -> {
                mCityArray.clear()
                mCityArray.addAll(mDBHandler.getCityDataAll())
                log("city added. restartLoader")
                supportLoaderManager.restartLoader(LOADER_ID, null, this)
            }
        }
    }

}

