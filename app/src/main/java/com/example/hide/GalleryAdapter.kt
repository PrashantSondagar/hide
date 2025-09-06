package com.example.hide

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class GalleryAdapter(
    private val context: Context,
    private val items: List<String>
) : BaseAdapter() {

    private val selectedItems = mutableListOf<String>()

    override fun getCount(): Int = items.size
    override fun getItem(position: Int): Any = items[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val item = items[position]
        val gridItemView = convertView ?: LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false)

        val itemImageView: ImageView = gridItemView.findViewById(R.id.itemImage)
        val itemNameTextView: TextView = gridItemView.findViewById(R.id.itemName)

        itemNameTextView.text = item.substringAfterLast("/")

        Glide.with(context)
            .load(item)
            .into(itemImageView)

        gridItemView.setOnClickListener {
            if (selectedItems.contains(item)) {
                selectedItems.remove(item)
                gridItemView.alpha = 1.0f
            } else {
                selectedItems.add(item)
                gridItemView.alpha = 0.5f
            }
        }

        return gridItemView
    }

    fun getSelectedItems(): List<String> = selectedItems
}
