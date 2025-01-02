import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.paba.project.R
import com.paba.project.f_guide_search

class f_main_search : Fragment() {

    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_f_main_search, container, false)

        db = FirebaseFirestore.getInstance()

        // Setup button
        val btnBali = view.findViewById<Button>(R.id.btnBali)
        val btnSurabaya = view.findViewById<Button>(R.id.btnsby)
        val btnJakarta = view.findViewById<Button>(R.id.btnjkt)
        val viewjkt = view.findViewById<FrameLayout>(R.id.jakartaSection)
        val viewsby = view.findViewById<FrameLayout>(R.id.surabayaSection)
        val viewbdg = view.findViewById<FrameLayout>(R.id.bandungSection)
        val viewbali = view.findViewById<FrameLayout>(R.id.baliSection)
        val viewygk = view.findViewById<FrameLayout>(R.id.yogyakartaSection)
        val viewmdn = view.findViewById<FrameLayout>(R.id.medanSection)

        // Button Bali
        btnBali.setOnClickListener {
            tekanButtonKota("Bali")
        }

        // Button Surabaya
        btnSurabaya.setOnClickListener {
            tekanButtonKota("Surabaya")
        }

        // Button Jakarta
        btnJakarta.setOnClickListener {
            tekanButtonKota("Jakarta")
        }

        //view jakarta
        viewjkt.setOnClickListener {
            tekanButtonKota("Jakarta")
        }

        //view surabaya
        viewsby.setOnClickListener {
            tekanButtonKota("Surabaya")
        }

        //view bandung
        viewbdg.setOnClickListener {
            tekanButtonKota("Bandung")
        }

        //view bali
        viewbali.setOnClickListener {
            tekanButtonKota("Bali")
        }

        //view yogyakarta
        viewygk.setOnClickListener {
            tekanButtonKota("Yogyakarta")
        }

        //view medan
        viewmdn.setOnClickListener {
            tekanButtonKota("Medan")
        }

        return view
    }

    private fun tekanButtonKota(location: String) {
        db.collection("tbTourGuide")
            .whereEqualTo("kota", location)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(requireContext(), "No guides found in $location", Toast.LENGTH_SHORT).show()
                } else {
                    Log.d("wow", "${arguments?.getString("email")}")
                    if ((arguments?.getString("email")) != null) {
                        val searchFragment = f_guide_search.newInstance(location, arguments?.getString("email").toString())

                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainer, searchFragment)
                            .addToBackStack(null)
                            .commit()
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to fetch data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
