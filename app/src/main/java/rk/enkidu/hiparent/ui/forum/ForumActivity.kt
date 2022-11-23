package rk.enkidu.hiparent.ui.forum

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.annotation.StringRes
import androidx.core.app.ActivityOptionsCompat
import com.google.android.material.tabs.TabLayoutMediator
import rk.enkidu.hiparent.R
import rk.enkidu.hiparent.databinding.ActivityForumBinding
import rk.enkidu.hiparent.ui.forum.add.AddNewDiscussActivity
import rk.enkidu.hiparent.ui.forum.pager.SectionPagerAdapter

class ForumActivity : AppCompatActivity() {

    private var _binding : ActivityForumBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityForumBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //close top bar
        setupView()

        //show tab layout
        showTabLayout()

        //add new discussion
        addDiscuss()
    }

    private fun addDiscuss() {
        binding?.btnAddNewDiscuss?.setOnClickListener {
            intent = Intent(this@ForumActivity, AddNewDiscussActivity::class.java)
            startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this@ForumActivity as Activity).toBundle())
        }
    }

    private fun showTabLayout() {
        //show tab layout
        val sectionsPagerAdapter = SectionPagerAdapter(this)
        binding?.vp2Forum?.adapter = sectionsPagerAdapter

        TabLayoutMediator(binding?.tlForum!!, binding?.vp2Forum!!) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
        supportActionBar?.elevation = 0f
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

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_text_1,
            R.string.tab_text_2,
        )
    }
}