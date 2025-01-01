package com.paba.project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.paba.project.adapterGuides
import com.paba.project.tour_guide_detail

class f_guide_search : Fragment() {

    private var db = FirebaseFirestore.getInstance()
    private lateinit var recyclerView: RecyclerView
    private lateinit var guideAdapter: adapterGuides
    private var guideList = ArrayList<tour_guide_detail>()
    private var filteredList = ArrayList<tour_guide_detail>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_f_guide_search, container, false)

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.rvTourGuide)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize Adapter
        guideAdapter = adapterGuides(filteredList)
        recyclerView.adapter = guideAdapter

        // Retrieve search query from arguments
        arguments?.getString(ARG_LOCATION)?.let { location ->
            loadGuidesFromFirebase(location)
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
                                rating = document.getString("rating") ?: "0.0",
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
                exception.printStackTrace()
            }
    }


    companion object {
        private const val ARG_LOCATION = "location"

        @JvmStatic
        fun newInstance(location: String) = f_guide_search().apply {
            arguments = Bundle().apply {
                putString(ARG_LOCATION, location)
            }
        }
    }
}
