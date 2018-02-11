package com.k4kya.callback.configure

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.k4kya.callback.R

data class CallingApp(val icon: Drawable, val name: String)

class CallingAppAdapter(private val items: List<CallingApp>) : BaseAdapter() {
    
    override fun getItem(index: Int) = items[index]
    
    override fun getItemId(index: Int) = getItem(index).hashCode().toLong()
    
    override fun getCount() = items.size
    
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = getItem(position)
        val view = convertView ?: LayoutInflater.from(parent.context).inflate(R.layout.list_item_calling_app_row, parent, false)
    
        view.findViewById<ImageView>(R.id.img_app_icon).setImageDrawable(item.icon)
        view.findViewById<TextView>(R.id.txt_app_name).text = item.name
        return view
    }
    
}