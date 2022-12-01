@file:Suppress("DEPRECATION")

package rk.enkidu.hiparent.ui.home.fragments

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import rk.enkidu.hiparent.R
import rk.enkidu.hiparent.data.result.Result
import rk.enkidu.hiparent.databinding.FragmentProfileBinding
import rk.enkidu.hiparent.logic.helper.ViewModelFactory
import rk.enkidu.hiparent.logic.viewmodel.ProfileViewModel
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {

    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding

    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseStorage: FirebaseStorage

    private lateinit var imageUri : Uri

    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //state
        showLoading(false)
        showEtFullname(false)
        showTvFullname(true)
        showIvChangeFullname(true)

        //setup firebase auth
        auth = Firebase.auth
        firebaseStorage = Firebase.storage

        val user = auth.currentUser

        //setup viewModel
        profileViewModel = ViewModelProvider(viewModelStore, ViewModelFactory.getInstance(auth))[ProfileViewModel::class.java]

        //set data
        setupData(user)

        //set camera
        setupCamera()

        //change fullname is clicked
        changeTypeFullname()

        //close change fullname is clicked
        closeEditFullname()

        //save update profile
        saveUpdate(user)
    }

    private fun closeEditFullname() {
        binding?.ivChangeFullnameClose?.setOnClickListener {
            setState(state1 = true, state2 = true, state3 = false, state4 = false)

            //change name to before
            binding?.etFullnameTextProfile?.setText(auth.currentUser?.displayName)
        }
    }

    private fun changeTypeFullname() {
        binding?.ivChangeFullname?.setOnClickListener {
            setState(state1 = false, state2 = false, state3 = true, state4 = true)
        }
    }

    private fun setState(state1: Boolean, state2: Boolean, state3: Boolean, state4: Boolean){
        showTvFullname(state1)
        showIvChangeFullname(state2)
        showEtFullname(state3)
        showIvChangeFullnameClose(state4)
    }

    private fun saveUpdate(user: FirebaseUser?) {
        binding?.btnSave?.setOnClickListener {
            val fullname = binding?.etFullnameTextProfile?.text.toString()

            when{
                fullname.isEmpty() -> {
                    binding?.etFullnameTextProfile?.error = getString(R.string.fullname_empty_error)
                }
                fullname.length > 30 -> {
                    binding?.etFullnameTextProfile?.error = getString(R.string.max_fullname_char)
                }
                else -> {
                    AlertDialog.Builder(requireActivity()).apply {
                        setTitle(getString(R.string.alert))
                        setMessage(getString(R.string.update_confirmation))
                        setNegativeButton(getString(R.string.no)) { _, _ -> }
                        setPositiveButton(getString(R.string.yes)) { _, _ ->
                            save(user, fullname)
                        }
                        create()
                        show()
                    }
                }
            }
        }
    }

    private fun save(user: FirebaseUser?, fullname: String) {
        val image = when{
            ::imageUri.isInitialized -> imageUri //upload
            user?.photoUrl == null -> Uri.parse("https://picsum.photos/id/106/200/300") //tidak ada foto
            else -> user.photoUrl //setelah foto diupload
        }

        profileViewModel.updateProfile(image!!, fullname).observe(viewLifecycleOwner){ result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showLoading(false)
                        setState(state1 = true, state2 = true, state3 = false, state4 = false)
                        Toast.makeText(activity, getString(R.string.update_success), Toast.LENGTH_SHORT).show()

                        //setup data after update
                        setupData(auth.currentUser)

                        //update realtime
                        val name = result.data.displayName
                        val photo = result.data.photoUri
                        fetchData(name.toString(), photo.toString())
                        fetchComments(name.toString(), photo.toString())
                    }
                    is Result.Error -> {
                        showLoading(false)
                        Toast.makeText(activity, getString(R.string.update_failed), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun fetchComments(name: String, photo: String) {
        profileViewModel.fetchComments()

        profileViewModel.message.observe(requireActivity()){
            if(it != null){
                for (i in it.indices){
                    val id = it[i].id
                    val uid = it[i].uid
                    if(uid.toString() == auth.currentUser?.uid){
                        updateRealtimeComment(id.toString(), name, photo)
                    }
                }
            }
        }
    }

    private fun updateRealtimeComment(id: String, name: String, photo: String) {
        profileViewModel.updateNameAndProfileComments(id, name, photo)
    }

    private fun fetchData(name: String, photo: String) {
        profileViewModel.fetchDiscussion()

        profileViewModel.discuss.observe(requireActivity()){
            if(it != null){
                for(i in it.indices){
                    val id = it[i].id
                    val uid = it[i].uid
                    if(uid.toString() == auth.currentUser?.uid){
                        updateRealtime(id.toString(), name, photo)
                    }
                }
            }
        }
    }

    private fun updateRealtime(id: String, name: String, photo: String) {
        profileViewModel.updateNameAndProfile(id, name, photo)
    }



    private fun setupData(user: FirebaseUser?) {
        if (user != null){
            //set fullname
            binding?.tvFullnameTextProfile?.text = user.displayName

            //set et fullname
            binding?.etFullnameTextProfile?.setText(user.displayName)

            //set email
            binding?.tvEmailTextProfile?.text = user.email

            if(user.photoUrl != null ){
                //set image
                Picasso.get().load(user.photoUrl).into(binding?.ivProfile)
            } else {
                Picasso.get().load("https://picsum.photos/id/106/200/300").into(binding?.ivProfile)
            }
        }

    }

    private fun setupCamera() {
        binding?.ivProfileClick?.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA),  REQUEST_CODE_PERMISSION)
                return@setOnClickListener
            }
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
                activity?.packageManager?.let {
                    intent.resolveActivity(it).also {
                        startActivityForResult(intent, REQUEST_CAMERA)
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CAMERA && resultCode == RESULT_OK){
            val img = data?.extras?.get("data") as Bitmap
            uploadImage(img)
        }
    }

    private fun uploadImage(img: Bitmap) {
        showLoading(true)
        val outputStream = ByteArrayOutputStream()
        val storageReference = firebaseStorage.reference.child("img/${auth.currentUser?.uid}")

        img.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val image = outputStream.toByteArray()

        storageReference.putBytes(image)
            .addOnCompleteListener{
                if(it.isSuccessful){
                    showLoading(false)
                    storageReference.downloadUrl.addOnCompleteListener { result ->
                        result.result?.let { uri ->
                            imageUri = uri

                            binding?.ivProfile?.setImageBitmap(img)
                        }
                    }
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    private fun showLoading(isLoading: Boolean){ binding?.pbProfile?.visibility = if (isLoading) View.VISIBLE else View.GONE }

    private fun showTvFullname(isShow: Boolean){ binding?.tvFullnameTextProfile?.visibility = if (isShow) View.VISIBLE else View.INVISIBLE }

    private fun showEtFullname(isShow: Boolean){ binding?.etFullnameTextProfile?.visibility = if (isShow) View.VISIBLE else View.INVISIBLE }

    private fun showIvChangeFullname(isShow: Boolean){ binding?.ivChangeFullname?.visibility = if (isShow) View.VISIBLE else View.INVISIBLE }

    private fun showIvChangeFullnameClose(isShow: Boolean){ binding?.ivChangeFullnameClose?.visibility = if (isShow) View.VISIBLE else View.INVISIBLE }

    companion object{
        private const val REQUEST_CODE_PERMISSION = 10
        private const val REQUEST_CAMERA = 100
    }
}