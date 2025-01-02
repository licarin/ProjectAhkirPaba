package com.paba.project

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class adapterGuides (private val listGuides : ArrayList<tour_guide_detail>, private val email: String?) : RecyclerView.Adapter<adapterGuides.ListViewHolder>() {
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
        holder._tvLokasi.text = "${tourGuideDetail.location}, "
        holder._tvCity.text = tourGuideDetail.city
        holder._tvLanguages.text = tourGuideDetail.language
        holder._tvPrice.text = "Rp ${f_guide_search.formatPrice(tourGuideDetail.price)}"
        holder._tvRating.text = tourGuideDetail.rating
        holder._tvReviewers.text = "(${tourGuideDetail.reviews} reviews)"


        holder.itemView.setOnClickListener {
            Log.d("Email", "Email sent: $email")
            val context = holder.itemView.context
            val intent = Intent(context, tourGuide_profile::class.java)
            intent.putExtra("name", tourGuideDetail.name)
            intent.putExtra("location", tourGuideDetail.location)
            intent.putExtra("city", tourGuideDetail.city)
            intent.putExtra("language", tourGuideDetail.language)
            intent.putExtra("price", "Rp ${tourGuideDetail.price}")
            intent.putExtra("rating", tourGuideDetail.rating)
            intent.putExtra("reviews", tourGuideDetail.reviews)
            intent.putExtra("profile_pic", tourGuideDetail.profile_pic)
            intent.putExtra("aboutMe", tourGuideDetail.aboutMe)
            if (email != null) {
                intent.putExtra("email", email)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listGuides.size
    }
}