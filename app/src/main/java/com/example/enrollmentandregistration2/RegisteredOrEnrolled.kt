package com.example.enrollmentandregistration2

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegisteredOrEnrolled : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registered_or_enrolled)

        val dbHelper = DatabaseHelper(this)

        if (StudentData.status == "registered") {
            findViewById<TextView>(R.id.title).text = "Registration Status"
            findViewById<TextView>(R.id.message).text = "Registered Subjects for"
            findViewById<Button>(R.id.toNextStep).text = "PROCEED TO ENROLLMENT"
            findViewById<Button>(R.id.toNextStep).setOnClickListener {
                startActivity(Intent(this, EnrollmentInterface::class.java))
                finish()
            }
        } else {
            findViewById<TextView>(R.id.title).text = "Enrollment Status"
            findViewById<TextView>(R.id.message).text = "Enrolled Subjects for"
            findViewById<Button>(R.id.toNextStep).text = "PROCEED TO FEEs"
            findViewById<Button>(R.id.toNextStep).setOnClickListener {
                startActivity(Intent(this, FeeInterface::class.java))
                finish()
            }
        }

        findViewById<TextView>(R.id.studentno).text = StudentData.studentNo
        findViewById<TextView>(R.id.studentname).text = StudentData.fullname

        val selectedSubjects = dbHelper.getSubjectsForStudent(StudentData.studentNo)
        val subjectListView = findViewById<ListView>(R.id.subjects)
        val adapter = SubjectAdapter(this, selectedSubjects)
        subjectListView.adapter = adapter


        // back button
        findViewById<ImageView>(R.id.backBtn).setOnClickListener {
            finish()
        }
    }
}