package com.example.umbum.recyclerviewexp

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

data class ItemData (
        var imgId: Int,
        var name: String,
        var base: String
)

/* ViewHolder의 itemN이 가지고 있을 view들을 정해준다. */
class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val thumb_img = view.findViewById(R.id.thumb_img) as ImageView
    val name = view.findViewById(R.id.name) as TextView
    val base = view.findViewById(R.id.base) as TextView
}

class RecyclerAdapter(private val context: Context, private val items: List<ItemData>)
    : RecyclerView.Adapter<ViewHolder>() {
    // 1. view를 inflate 하기 위해서는 일단 아래처럼 inflate를 먼저 얻어와야 한다.
    private val mInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    lateinit var onItemClick: (View?) -> Unit

    override fun getItemCount(): Int = items.size

    /* Bind가 필요할 때 마다 layout manager에 의해 호출됨.
    ViewHolder의 item이 가지고 있는 각각의 view에 데이터를 대입. */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // 생성자에서 받은 DataList의 요소를 하나 씩 가져와 view에 대입.
        // position은 좌표가 아니라 자동으로 0..n으로 넘어오는 듯.
        holder.name.tag = position
        holder.name.text = items[position].name
        holder.base.text = items[position].base
        holder.thumb_img.setImageResource(items[position].imgId)
    }

        /* viewType에 따라 딱 한 번만 호출되기 때문에 일반적인 경우 ViewHolder는 하나라고 보면 됨. */
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            // 2. inflate 할 view(xml)을 지정한다. 이 경우 ViewHolder에 끼워질 아이템들의 레이아웃을 지정.
            val mainView = mInflater.inflate(R.layout.layout_item, parent, false)
            // 3. 아이템을 클릭했을 때 어떻게 반응할 것인지 리스너 지정
            mainView.setOnClickListener(onItemClick)
        return ViewHolder(mainView)
    }

    /* 자동으로 setter가 설정되기는 하지만, 다른 Listener 함수처럼 = 대입이 아닌 함수 형태로 제공하기 위해서. */
    fun setOnItemClickListener(listener: (View?) -> Unit) {
        onItemClick = listener
    }
}
