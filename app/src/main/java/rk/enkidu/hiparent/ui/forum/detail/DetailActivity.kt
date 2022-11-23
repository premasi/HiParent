package rk.enkidu.hiparent.ui.forum.detail

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateUtils
import android.view.WindowInsets
import android.view.WindowManager
import com.squareup.picasso.Picasso
import rk.enkidu.hiparent.data.entity.remote.Discussion
import rk.enkidu.hiparent.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private var _binding : ActivityDetailBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //close top bar
        setupView()

        //get data
        val dataDetail = intent.getParcelableExtra<Discussion>(DATA_DETAIL) as Discussion

        //show data
        showData(dataDetail)
    }

    private fun showData(dataDetail: Discussion) {
        Picasso.get().load(dataDetail.photoUrl).into(binding?.ivDetail)
        binding?.tvFullnameDetail?.text = dataDetail.name
        binding?.tvDateDetail?.text = DateUtils.getRelativeTimeSpanString(dataDetail.timestamp!!)
        binding?.tvTitleDetail?.text = dataDetail.title
        binding?.tvDescDetail?.text = dataDetail.desc
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        supportActionBar?.hide()
    }

    companion object{
        const val DATA_DETAIL = "extra_data"
    }
}