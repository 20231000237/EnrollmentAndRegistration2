package com.example.enrollmentandregistration2

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class AddStudentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_student)

        findViewById<TextView>(R.id.loginStudentBtn).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.addbtn).setOnClickListener {
            val studentNo = findViewById<EditText>(R.id.studentno).text.toString()
            val fullName = findViewById<EditText>(R.id.fullname).text.toString()
            val password = findViewById<EditText>(R.id.password).text.toString()
            val passwordConfirmation = findViewById<EditText>(R.id.confirmPassword).text.toString()

            val dbHelper = DatabaseHelper(this)

            if (studentNo.isNotBlank() && fullName.isNotBlank() && password.isNotBlank()) {
                if (password != passwordConfirmation) {
                    AlertDialog.Builder(this)
                        .setTitle("NOTICE")
                        .setMessage("Passwords do not match!")
                        .setNegativeButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                } else {
                    val success = dbHelper.registerStudent(studentNo, fullName, password)
                    if (success) {
                        Toast.makeText(this, "Registered!", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Failed to register student!", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                AlertDialog.Builder(this)
                    .setTitle("NOTICE")
                    .setMessage("Please fill up all input fields!")
                    .setNegativeButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }
}

