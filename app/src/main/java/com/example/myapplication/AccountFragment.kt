package com.example.myapplication

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import java.util.Calendar

class AccountFragment : Fragment() {

    private var profileImageView: ImageView? = null

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            ProfilePhotoStore.bitmap = bitmap
            profileImageView?.setImageBitmap(bitmap)
        } else if (isAdded) {
            Toast.makeText(requireContext(), R.string.p4_camera_cancelled, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_account, container, false)
        val prefs = requireContext().getSharedPreferences("yummcart_profile", Context.MODE_PRIVATE)

        val image = view.findViewById<ImageView>(R.id.imgProfilePicture)
        profileImageView = image
        ProfilePhotoStore.bitmap?.let(image::setImageBitmap)

        val name = view.findViewById<EditText>(R.id.etProfileName)
        val address = view.findViewById<EditText>(R.id.etProfileAddress)
        val email = view.findViewById<EditText>(R.id.etProfileEmail)
        val contact = view.findViewById<EditText>(R.id.etProfileContact)
        val dob = view.findViewById<EditText>(R.id.etProfileDob)

        name.setText(prefs.getString("name", ""))
        address.setText(prefs.getString("address", ""))
        email.setText(prefs.getString("email", ""))
        contact.setText(prefs.getString("contact", ""))
        dob.setText(prefs.getString("dob", ""))

        view.findViewById<Button>(R.id.btnTakePicture).setOnClickListener {
            cameraLauncher.launch(null)
        }

        dob.setOnClickListener {
            val calendar = Calendar.getInstance()
            val existing = dob.text.toString().split("/")
            if (existing.size == 3) {
                val day = existing[0].toIntOrNull()
                val month = existing[1].toIntOrNull()
                val year = existing[2].toIntOrNull()
                if (day != null && month != null && year != null) {
                    calendar.set(year, month - 1, day)
                }
            }
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    dob.setText(getString(R.string.p4_date_format, day, month + 1, year))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        view.findViewById<Button>(R.id.btnSaveProfile).setOnClickListener {
            val nameValue = name.text.toString().trim()
            val addressValue = address.text.toString().trim()
            val emailValue = email.text.toString().trim()
            val contactValue = contact.text.toString().trim()
            val dobValue = dob.text.toString().trim()

            when {
                nameValue.isEmpty() || addressValue.isEmpty() || emailValue.isEmpty() ||
                    contactValue.isEmpty() || dobValue.isEmpty() -> {
                    Toast.makeText(requireContext(), R.string.p4_fill_all_fields, Toast.LENGTH_SHORT).show()
                }
                !Patterns.EMAIL_ADDRESS.matcher(emailValue).matches() -> {
                    email.error = getString(R.string.p4_invalid_email)
                }
                contactValue.filter { it.isDigit() }.length < 10 -> {
                    contact.error = getString(R.string.p4_invalid_contact)
                }
                else -> {
                    prefs.edit()
                        .putString("name", nameValue)
                        .putString("address", addressValue)
                        .putString("email", emailValue)
                        .putString("contact", contactValue)
                        .putString("dob", dobValue)
                        .apply()
                    Toast.makeText(requireContext(), R.string.p4_profile_saved, Toast.LENGTH_SHORT).show()
                }
            }
        }

        return view
    }

    override fun onDestroyView() {
        profileImageView = null
        super.onDestroyView()
    }
}
