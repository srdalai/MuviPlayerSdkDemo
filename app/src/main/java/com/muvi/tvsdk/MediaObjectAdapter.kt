package com.muvi.tvsdk

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class MediaObjectAdapter(private val items: List<MediaObject>): BaseAdapter() {

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(parent!!.context).inflate(R.layout.item_media_object, parent, false)
        }

        val textViewTitle: TextView = view!!.findViewById(R.id.textViewTitle)
        val textViewUrl: TextView = view.findViewById(R.id.textViewUrl)

        textViewTitle.text = items[position].name
        textViewUrl.text = items[position].uri

        return view
    }
}