package com.paba.project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class adapterGuides (private val listGuides : ArrayList<tour_guide_detail>) : RecyclerView.Adapter<adapterGuides.ListViewHolder>() {
    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var _ivFoto = itemView.findViewById<ImageView>(R.id.guideImage)
        var _tvNama = itemView.findViewById<TextView>(R.id.guideName)
        var _tvLokasi = itemView.findViewById<TextView>(R.id.tv_location)
        var _tvCity = itemView.findViewById<TextView>(R.id.tv_city)
        var _tvLanguages = itemView.findViewById<TextView>(R.id.tv_languages)
        var _tvPrice = itemView.findViewById<TextView>(R.id.guide_price)
        var _tvRating = itemView.findViewById<TextView>(R.id.tv_rating)
        var _tvReviewers = itemView.findViewById<TextView>(R.id.tv_reviewers)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.guide_item_recycler, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var tourGuideDetail = listGuides[position]
        //image belum
        val imageResId = holder.itemView.context.resources.getIdentifier(tourGuideDetail.profile_pic, "drawable", holder.itemView.context.packageName)
        if (imageResId != 0) {
            holder._ivFoto.setImageResource(imageResId)
        }
        holder._tvNama.text = tourGuideDetail.name
        holder._tvLokasi.text = tourGuideDetail.location
        holder._tvCity.text = ", ${tourGuideDetail.city}"
        holder._tvLanguages.text = tourGuideDetail.language
        holder._tvPrice.text = "Rp ${tourGuideDetail.price}"
        holder._tvRating.text = tourGuideDetail.rating
        holder._tvReviewers.text = "(${tourGuideDetail.reviews} reviews)"
    }

    override fun getItemCount(): Int {
        return listGuides.size
    }
}