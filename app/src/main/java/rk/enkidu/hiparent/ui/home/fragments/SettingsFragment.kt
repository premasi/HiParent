package rk.enkidu.hiparent.ui.home.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityOptionsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import rk.enkidu.hiparent.R
import rk.enkidu.hiparent.databinding.FragmentSettingsBinding
import rk.enkidu.hiparent.ui.authentification.LoginActivity

class SettingsFragment : Fragment() {

    private var _binding : FragmentSettingsBinding? = null
    private val binding get() = _binding

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //setup firebase auth
        auth = Firebase.auth

        //change language
        language()

        //logout
        logout()
    }

    private fun logout() {
        binding?.tvLogout?.setOnClickListener {
            AlertDialog.Builder(requireActivity()).apply {
                setTitle(getString(R.string.alert))
                setMessage(getString(R.string.confirmation))
                setNegativeButton(getString(R.string.no)){ _, _ -> }
                setPositiveButton(getString(R.string.yes)) { _, _ ->
                    auth.signOut()

                    val intent = Intent(requireActivity(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity() as Activity).toBundle())
                }
                create()
                show()
            }
        }

    }

    private fun language() {
        binding?.tvBahasa?.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}