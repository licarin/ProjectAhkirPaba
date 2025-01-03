package com.paba.project

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView

class adapterTG (private val listTG : ArrayList<tourGuide>) : RecyclerView.Adapter<adapterTG.ListViewGolder>(){

    inner class ListViewGolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        var _ivTG = itemView.findViewById<ImageView>(R.id.ivTG)
        var _tvNama = itemView.findViewById<TextView>(R.id.tvNamaTG)
        var _tvLokasi = itemView.findViewById<TextView>(R.id.tvLokasiTG)
        var _tvRating = itemView.findViewById<TextView>(R.id.tvRatingTG)
        var _tvReviews = itemView.findViewById<TextView>(R.id.tvReviewsTG)
    }

    private lateinit var onItemClickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemClicked(data: tourGuide)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewGolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.star_guide_layout, parent, false)
        return ListViewGolder(view)
    }

    override fun getItemCount(): Int {
        return listTG.size
    }
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onBindViewHolder(holder: ListViewGolder, position: Int) {
        var tourGuide = listTG[position]
        val context = holder.itemView.context
        val imageResId = context.resources.getIdentifier(tourGuide.image, "drawable", context.packageName)
        if (imageResId != 0) {
            holder._ivTG.setImageResource(imageResId)
        }
        holder._tvNama.text = tourGuide.name
        holder._tvLokasi.text = tourGuide.lokasi
        holder._tvRating.text = tourGuide.rating.toString()
        holder._tvReviews.text = " (" + tourGuide.reviews.toString() + " reviews)"

        holder.itemView.setOnClickListener {
            val intent = Intent(context, tourGuide_profile::class.java)
            intent.putExtra("name", tourGuide.name)
            intent.putExtra("location", tourGuide.lokasi)
            intent.putExtra("city", tourGuide.kota)
            intent.putExtra("language", tourGuide.bahasa)
            intent.putExtra("price", tourGuide.harga)
            intent.putExtra("rating", tourGuide.rating.toString())
            intent.putExtra("reviews", tourGuide.reviews)
            intent.putExtra("profile_pic", tourGuide.image)
            intent.putExtra("aboutMe", tourGuide.aboutMe)
            context.startActivity(intent)
        }
       }
}