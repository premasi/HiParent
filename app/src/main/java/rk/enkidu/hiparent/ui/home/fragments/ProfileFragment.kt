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
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import rk.enkidu.hiparent.R
import rk.enkidu.hiparent.databinding.FragmentProfileBinding
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {

    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding

    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseStorage: FirebaseStorage

    private lateinit var imageUri : Uri

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

        //close loading
        showLoading(false)

        //setup firebase auth
        auth = Firebase.auth
        firebaseStorage = Firebase.storage

        //set data
        setupData()
    }

    private fun setupData() {
//        val user = auth.currentUser
//
//        if (user != null){
//            if(user.photoUrl != null ){
//                Picasso.get().load(user.photoUrl).into(binding?.ivProfile)
//            } else {
//                Picasso.get().load("https://ibb.co/FhVFxgp").into(binding?.ivProfile)
//            }
//        }

        //activated camera
        setupCamera()

        //save data
//        binding?.btnSave?.setOnClickListener {
//            val image = when{
//                ::imageUri.isInitialized -> imageUri //upload
//                user?.photoUrl == null -> Uri.parse("https://ibb.co/FhVFxgp") //tidak ada foto
//                else -> user.photoUrl //setelah foto diupload
//            }
//
//            UserProfileChangeRequest.Builder()
//                .setPhotoUri(image)
//                .build().also {
//                    user?.updateProfile(it)?.addOnCompleteListener {
//                        if(it.isSuccessful){
//                            Toast.makeText(requireActivity(), getString(R.string.update_success), Toast.LENGTH_SHORT).show()
//                        } else {
//                            Toast.makeText(requireActivity(), getString(R.string.update_failed), Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                }
//        }
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

    companion object{
        private const val REQUEST_CODE_PERMISSION = 10
        private const val REQUEST_CAMERA = 100
    }
}