package rk.enkidu.hiparent.ui.forum.pager

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import rk.enkidu.hiparent.ui.forum.fragments.AllPostFragment
import rk.enkidu.hiparent.ui.forum.fragments.MyPostFragment

class SectionPagerAdapter (activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    override fun createFragment(position: Int): Fragment {
        var fragment : Fragment? = null
        when(position){
            0 -> fragment = AllPostFragment()
            1 -> fragment = MyPostFragment()
        }
        return fragment as Fragment
    }

    override fun getItemCount(): Int {
        return 2
    }
}