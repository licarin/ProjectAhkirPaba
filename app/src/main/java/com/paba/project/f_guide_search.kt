package com.paba.project

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.paba.project.adapterGuides
import com.paba.project.tour_guide_detail
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class f_guide_search : Fragment() {

    private var db = FirebaseFirestore.getInstance()
    private lateinit var recyclerView: RecyclerView
    private lateinit var guideAdapter: adapterGuides
    private var guideList = ArrayList<tour_guide_detail>()
    private var filteredList = ArrayList<tour_guide_detail>()

    private lateinit var llSortOptionStar: LinearLayout
    private lateinit var llSortOptionPrice: LinearLayout
    private lateinit var tvSortStar: TextView
    private lateinit var tvSortPrice: TextView
    private lateinit var ivArrowStar: ImageView
    private lateinit var ivArrowPrice: ImageView

    private var isStarAscending = true
    private var isPriceAscending = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_f_guide_search, container, false)

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.rvTourGuide)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val email = arguments?.getString("email")
        // Initialize Adapter
        guideAdapter = adapterGuides(filteredList, email, parentFragmentManager)
        recyclerView.adapter = guideAdapter

        // Retrieve search query from arguments
        arguments?.getString(ARG_LOCATION)?.let { location ->
            loadGuidesFromFirebase(location)
        }

        // Initialize Sorting Views
        llSortOptionStar = view.findViewById(R.id.llSortOption)
        llSortOptionPrice = view.findViewById(R.id.llSortOptionPrice)
        tvSortStar = view.findViewById(R.id.tvFilterOption)
        tvSortPrice = view.findViewById(R.id.tvFilterOptionPrice)
        ivArrowStar = view.findViewById(R.id.ivArrow)
        ivArrowPrice = view.findViewById(R.id.ivArrowPrice)

        // Set click listeners for sorting
        llSortOptionStar.setOnClickListener {
            sortGuides("star", isStarAscending)
            updateSortUI(tvSortStar, ivArrowStar, isStarAscending)
            isStarAscending = !isStarAscending
        }

        llSortOptionPrice.setOnClickListener {
            sortGuides("price", isPriceAscending)
            updateSortUI(tvSortPrice, ivArrowPrice, isPriceAscending)
            isPriceAscending = !isPriceAscending
        }

        return view
    }

    private fun loadGuidesFromFirebase(location: String) {
        val locationLowerCase = location.lowercase()

        db.collection("tbTourGuide")
            .get()
            .addOnSuccessListener { documents ->
                guideList.clear()
                if (documents.isEmpty) {
                    Toast.makeText(requireContext(), "No guides found in $location", Toast.LENGTH_SHORT).show()
                } else {
                    for (document in documents) {
                        val city = document.getString("kota")?.lowercase() ?: ""

                        if (city.contains(locationLowerCase)) {
                            val guide = tour_guide_detail(
                                aboutMe = document.getString("aboutMe") ?: "",
                                profile_pic = document.getString("image") ?: "",
                                name = document.getString("nama") ?: "Unknown",
                                location = document.getString("lokasi") ?: "",
                                city = document.getString("kota") ?: "",
                                language = document.getString("bahasa") ?: "",
                                price = document.getString("harga") ?: "",
                                rating = document.getDouble("rating").toString() ?: "0.0",
                                reviews = document.getString("reviews") ?: ""
                            )
                            guideList.add(guide)
                        }
                    }

                    filteredList.clear()
                    filteredList.addAll(guideList)
                    guideAdapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseError", "Error loading data", exception)
            }
    }

    private fun sortGuides(sortBy: String, isAscending: Boolean) {
        val sortedList = when (sortBy) {
            "star" -> if (isAscending) {
                guideList.sortedBy { it.rating.toDoubleOrNull() ?: 0.0 }
            } else {
                guideList.sortedByDescending { it.rating.toDoubleOrNull() ?: 0.0 }
            }
            "price" -> if (isAscending) {
                guideList.sortedBy { it.price.toDoubleOrNull() ?: Double.MAX_VALUE }
            } else {
                guideList.sortedByDescending { it.price.toDoubleOrNull() ?: Double.MIN_VALUE }
            }
            else -> guideList
        }

        filteredList.clear()
        filteredList.addAll(sortedList)
        guideAdapter.notifyDataSetChanged()
    }

    private fun updateSortUI(textView: TextView, imageView: ImageView, isAscending: Boolean) {
        textView.setTextColor(if (isAscending) 0xFF000000.toInt() else 0xFF808080.toInt())
        imageView.setImageResource(
            if (isAscending) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down
        )
    }

    companion object {
        private const val ARG_LOCATION = "location"

        @JvmStatic
        fun newInstance(location: String, email: String) = f_guide_search().apply {
            arguments = Bundle().apply {
                putString(ARG_LOCATION, location)
                putString("email", email)
            }
        }

        fun formatPrice(price: String): String {
            return try {
                val number = price.toDoubleOrNull() ?: 0.0
                val symbols = DecimalFormatSymbols().apply {
                    groupingSeparator = '.'
                    decimalSeparator = ','
                }
                DecimalFormat("#,###.00", symbols).format(number)
            } catch (e: Exception) {
                "0,00"
            }
        }
    }
}
