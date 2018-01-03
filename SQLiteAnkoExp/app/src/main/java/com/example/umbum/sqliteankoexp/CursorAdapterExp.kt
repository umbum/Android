package com.example.umbum.sqliteankoexp

import android.content.Context
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.provider.MediaStore
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.content.res.ResourcesCompat.getDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.umbum.sqliteankoexp.DB.UserColumns


class ViewHolder(v: View) {
    val pic = v.findViewById(R.id.profile) as ImageView
    val name = v.findViewById(R.id.name) as TextView
    val tel = v.findViewById(R.id.tel_num) as TextView
    val del = v.findViewById(R.id.del_item) as ImageView
}

/* Cursor를 받아서 Cursor를 이용해 데이터를 얻어내고 이를 View에 넣어 그린다.
!! CursorAdapter 사용시 커서에 반드시 _id 컬럼이 포함되어 있어야 한다.
   단, 테이블 자체의 컬럼 이름이 _id여야 할 필요는 없고 쿼리 시 AS _id로 이름만 변경해주거나, CursorWrapper를 사용하면 된다. */
class CursorAdapterExp(val mCtx: Context, cursor: Cursor?)
    : CursorAdapter(mCtx, cursor, FLAG_REGISTER_CONTENT_OBSERVER) {

    override fun newView(context: Context, cursor: Cursor?, parent: ViewGroup?): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val elementView = inflater.inflate(R.layout.layout_list_element, parent, false)

        var vHolder = ViewHolder(elementView)

        elementView.tag = vHolder
        return elementView
    }

    override fun bindView(convertView: View, context: Context, cursor: Cursor) {
        val holder = convertView.tag as ViewHolder
        holder.name.text = String.format("%s (%d)",
                cursor.getString(UserColumns.NAME.ordinal), cursor.getInt(UserColumns.AGE.ordinal))
        holder.tel.text = cursor.getString(UserColumns.TELNUM.ordinal)
        holder.pic.background = getPicture(cursor.getString(UserColumns.PIC_PATH.ordinal)) ?:
                        getDrawable(context.resources, android.R.drawable.ic_menu_gallery, null)
        holder.del.tag = cursor.getLong(UserColumns._id.ordinal)
    }

    private fun getPicture(path: String): Drawable? {
        val img_id = path.toLong()
        if (img_id == 0L) return null

        val bitmap: Bitmap =
                MediaStore.Images.Thumbnails.getThumbnail(mCtx.contentResolver,
                        img_id, MediaStore.Images.Thumbnails.MICRO_KIND, null) ?: return null
        return BitmapDrawable(mCtx.resources, bitmap)
    }

}
