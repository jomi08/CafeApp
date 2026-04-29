package com.example.cafeapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var phoneInput: EditText
    private lateinit var btnSubmit: Button
    private lateinit var tabLogin: TextView
    private lateinit var tabRegister: TextView
    private lateinit var sp: SharedPreferences

    private var isLoginMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sp = getSharedPreferences("UserData", MODE_PRIVATE)

        // If arriving here from logout, isLoggedIn is already false via commit()
        // This check only auto-skips login if genuinely still logged in
        if (sp.getBoolean("isLoggedIn", false)) {
            goToHome()
            return
        }

        setContentView(R.layout.activity_main)

        nameInput   = findViewById(R.id.nameInput)
        emailInput  = findViewById(R.id.emailInput)
        phoneInput  = findViewById(R.id.phoneInput)
        btnSubmit   = findViewById(R.id.btn_submit)
        tabLogin    = findViewById(R.id.tabLogin)
        tabRegister = findViewById(R.id.tabRegister)

        setLoginMode()

        tabLogin.setOnClickListener {
            isLoginMode = true
            setLoginMode()
        }

        tabRegister.setOnClickListener {
            isLoginMode = false
            setRegisterMode()
        }

        btnSubmit.setOnClickListener {
            if (isLoginMode) handleLogin() else handleRegister()
        }
    }

    private fun setLoginMode() {
        emailInput.visibility = View.GONE
        btnSubmit.text = "Login"
        tabLogin.setBackgroundResource(R.drawable.btn_primary)
        tabLogin.setTextColor(0xFFFFFFFF.toInt())
        tabRegister.setBackgroundResource(android.R.color.transparent)
        tabRegister.setTextColor(0xFF8D6E63.toInt())
    }

    private fun setRegisterMode() {
        emailInput.visibility = View.VISIBLE
        btnSubmit.text = "Register"
        tabRegister.setBackgroundResource(R.drawable.btn_primary)
        tabRegister.setTextColor(0xFFFFFFFF.toInt())
        tabLogin.setBackgroundResource(android.R.color.transparent)
        tabLogin.setTextColor(0xFF8D6E63.toInt())
    }

    private fun handleLogin() {
        val name  = nameInput.text.toString().trim()
        val phone = phoneInput.text.toString().trim()

        if (name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Enter your name and phone number", Toast.LENGTH_SHORT).show()
            return
        }

        if (!sp.getBoolean("isRegistered", false)) {
            Toast.makeText(this, "No account found. Please register first!", Toast.LENGTH_SHORT).show()
            return
        }

        val savedName  = sp.getString("name", "")
        val savedPhone = sp.getString("phone", "")

        if (name == savedName && phone == savedPhone) {
            sp.edit()
                .putBoolean("isLoggedIn", true)
                .putString("loggedInName", name)
                .commit()
            goToHome()
        } else {
            Toast.makeText(this, "Name or phone doesn't match. Try again!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleRegister() {
        val name  = nameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val phone = phoneInput.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields ☕", Toast.LENGTH_SHORT).show()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show()
            return
        }

        if (phone.length < 10) {
            Toast.makeText(this, "Enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show()
            return
        }

        sp.edit()
            .putString("name", name)
            .putString("email", email)
            .putString("phone", phone)
            .putBoolean("isRegistered", true)
            .commit()

        Toast.makeText(this, "Registered successfully! Please login ✅", Toast.LENGTH_SHORT).show()

        isLoginMode = true
        setLoginMode()
        nameInput.setText(name)
        phoneInput.setText(phone)
        emailInput.setText("")
    }

    private fun goToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}