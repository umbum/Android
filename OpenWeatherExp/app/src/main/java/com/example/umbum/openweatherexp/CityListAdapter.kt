package com.example.umbum.openweatherexp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.umbum.openweatherexp.data.CityData

class CityListAdapter(context: Context, cityData: ArrayList<CityData>)
    : ArrayAdapter<CityData>(context, R.layout.layout_city_list, cityData) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = convertView ?: inflater.inflate(R.layout.layout_city_list, parent, false)
        val data = getItem(position)
        (view.findViewById(R.id.city_name) as TextView).text = data.name
        view.tag = data.api_id

        return view
    }
}