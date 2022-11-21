package rk.enkidu.hiparent.ui.home.fragments

import android.os.Build
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import rk.enkidu.hiparent.R
import rk.enkidu.hiparent.data.entity.Banner
import rk.enkidu.hiparent.databinding.FragmentHomeBinding
import rk.enkidu.hiparent.ui.adapter.HomeBannerAdapter

class HomeFragment : Fragment() {

    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding

    private lateinit var auth: FirebaseAuth

    private lateinit var adapter: HomeBannerAdapter
    private lateinit var dots: ArrayList<TextView>
    private val list = ArrayList<Banner>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //setup firebase auth
        auth = Firebase.auth

        //get name
        binding?.tvFullnameTextHome?.text = auth.currentUser?.displayName!!

        //setup add banner image
        addBannerImage()

    }

    private fun addBannerImage() {
        list.add(
            Banner(
                "https://images.pexels.com/photos/39691/family-pier-man-woman-39691.jpeg"
            )
        )

        list.add(
            Banner(
                "https://images.pexels.com/photos/4205505/pexels-photo-4205505.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"
            )
        )

        list.add(
            Banner(
                "https://images.pexels.com/photos/4473870/pexels-photo-4473870.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"
            )
        )

        //adapter
        adapter = HomeBannerAdapter(list)
        binding?.vp2BannerSlide?.adapter = adapter
        dots = ArrayList()
        setIndicator()

        binding?.vp2BannerSlide?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                selectedDots(position)
                super.onPageSelected(position)
            }
        })
    }

    private fun selectedDots(position: Int) {
        for(i in 0 until list.size){
            if(i == position){
                dots[i].setTextColor(ContextCompat.getColor(requireActivity(), R.color.pink))
            } else {
                dots[i].setTextColor(ContextCompat.getColor(requireActivity(), R.color.light_pink))
            }
        }

    }

    private fun setIndicator() {
        for (i in 0 until list.size){
            dots.add(TextView(requireActivity()))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                dots[i].text = Html.fromHtml("&#9679", Html.FROM_HTML_MODE_LEGACY).toString()
            } else {
                dots[i].text = Html.fromHtml("&#9679")
            }
            dots[i].textSize = 18F
            binding?.vp2Indicator?.addView(dots[i])
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}