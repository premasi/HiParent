package rk.enkidu.hiparent.ui.tips.detail

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import kotlinx.coroutines.*
import rk.enkidu.hiparent.data.entity.remote.Tips
import rk.enkidu.hiparent.databinding.ActivityDetailTipsBinding

@Suppress("DEPRECATION")
class DetailTipsActivity : AppCompatActivity() {

    private var _binding: ActivityDetailTipsBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailTipsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //show loading
        showLoading(true)

        //close top bar
        setupView()

        //get data
        val dataDetail = intent.getParcelableExtra<Tips>(DATA_DETAIL) as Tips

        //show data
        showData(dataDetail)

        //close
        close()
    }

    private fun close() {
        binding?.ivBack?.setOnClickListener {
            finish()
        }
    }

    private fun showData(data: Tips) {
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
            showLoading(false)
        }

        binding?.tvTitle?.text = data.title
        binding?.tvDesc?.text = data.desc
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        CoroutineScope(Dispatchers.Main).cancel()
    }

    private fun showLoading(isLoading: Boolean){ binding?.pbDetailTips?.visibility = if (isLoading) View.VISIBLE else View.GONE }

    companion object{
        const val DATA_DETAIL = "extra_detail"
    }
}