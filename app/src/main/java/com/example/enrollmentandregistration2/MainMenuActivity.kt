package com.example.enrollmentandregistration2

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        // display logged in student info
        findViewById<TextView>(R.id.studentno).text = StudentData.studentNo
        findViewById<TextView>(R.id.fullname).text = StudentData.fullname
        findViewById<TextView>(R.id.status).text = StudentData.status

        // link menu buttons
        val registrationBtn = findViewById<Button>(R.id.registerbtn);
        val enrollmentBtn = findViewById<Button>(R.id.enrollbtn);
        val feeBtn = findViewById<Button>(R.id.feebtn);

        registrationBtn.setOnClickListener {
            // change interface depending on the status of the logged in student
            when (StudentData.status) {
                "unregistered" -> {
                    startActivity(Intent(this, RegistrationInterface::class.java))
                }
                "registered" -> {
                    startActivity(Intent(this, RegisteredOrEnrolled::class.java))
                }
                "enrolled" -> {
                    AlertDialog.Builder(this)
                        .setTitle("NOTICE")
                        .setMessage("You are already enrolled!")
                        .setNegativeButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }
            }
        }
        enrollmentBtn.setOnClickListener {
            // change interface depending on the status of the logged in student
            when (StudentData.status.toString()) {
                "unregistered" -> {
                    AlertDialog.Builder(this)
                        .setTitle("NOTICE")
                        .setMessage("Please register first!")
                        .setNegativeButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }
                "registered" -> {
                    startActivity(Intent(this, EnrollmentInterface::class.java))
                }
                "enrolled" -> {
                    startActivity(Intent(this, RegisteredOrEnrolled::class.java))
                }
            }
        }
        feeBtn.setOnClickListener {
            // change interface depending on the status of the logged in student
            when (StudentData.status.toString()) {
                "unregistered" -> {
                    AlertDialog.Builder(this)
                        .setTitle("NOTICE")
                        .setMessage("Please enroll first!")
                        .setNegativeButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }
                "registered" -> {
                    AlertDialog.Builder(this)
                        .setTitle("NOTICE")
                        .setMessage("Please enroll first!")
                        .setNegativeButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }
                "enrolled" -> {
                    startActivity(Intent(this, FeeInterface::class.java))
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        findViewById<TextView>(R.id.status).text = StudentData.status
    }


}