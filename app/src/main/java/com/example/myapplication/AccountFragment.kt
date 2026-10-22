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
    private lateinit var db: AppDatabaseHelper
    private var currentUser: User? = null

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
        db = AppDatabaseHelper(requireContext())

        val image = view.findViewById<ImageView>(R.id.imgProfilePicture)
        profileImageView = image
        ProfilePhotoStore.bitmap?.let(image::setImageBitmap)

        val name = view.findViewById<EditText>(R.id.etProfileName)
        val address = view.findViewById<EditText>(R.id.etProfileAddress)
        val email = view.findViewById<EditText>(R.id.etProfileEmail)
        val contact = view.findViewById<EditText>(R.id.etProfileContact)
        val dob = view.findViewById<EditText>(R.id.etProfileDob)

        val sessionPrefs = requireContext().getSharedPreferences("yummcart_session", Context.MODE_PRIVATE)
        val activeEmail = sessionPrefs.getString("active_user_email", "mahendra@gmail.com") ?: "mahendra@gmail.com"

        currentUser = db.getUserByEmail(activeEmail)
        currentUser?.let { user ->
            name.setText(user.name)
            address.setText(user.address)
            email.setText(user.email)
            contact.setText(user.contact)
            dob.setText(user.dob)
        }

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
                    val updatedUser = User(
                        id = currentUser?.id ?: 0,
                        name = nameValue,
                        email = emailValue,
                        password = currentUser?.password ?: "12345678",
                        address = addressValue,
                        contact = contactValue,
                        dob = dobValue
                    )

                    val success = db.updateUser(updatedUser)
                    if (success) {
                        sessionPrefs.edit().putString("active_user_email", emailValue).apply()
                        currentUser = updatedUser
                        Toast.makeText(requireContext(), "Account details updated in SQLite Database!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Failed to update profile in database", Toast.LENGTH_SHORT).show()
                    }
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
