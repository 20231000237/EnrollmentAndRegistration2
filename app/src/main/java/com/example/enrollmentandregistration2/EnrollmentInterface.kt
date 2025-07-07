package com.example.enrollmentandregistration2

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class EnrollmentInterface : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enrollment_interface)

        val dbHelper = DatabaseHelper(this)

        findViewById<TextView>(R.id.studentno).text = StudentData.studentNo
        findViewById<TextView>(R.id.fullname).text = StudentData.fullname

        val selectedSubjects = dbHelper.getSubjectsForStudent(StudentData.studentNo)
//        Toast.makeText(this, "Subjects loaded: ${selectedSubjects.size}", Toast.LENGTH_LONG).show()

        val subjectListView = findViewById<ListView>(R.id.subjects)
        val adapter = SubjectAdapter(this, selectedSubjects)
        subjectListView.adapter = adapter

        // enroll button
        findViewById<Button>(R.id.enroll).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("NOTICE")
                .setMessage("Enroll with the registered subjects?")
                .setPositiveButton("YES") { dialog, _ ->
                    // update database
                    if (dbHelper.updateStudentStatus(StudentData.studentNo, "enrolled")) {
                        AlertDialog.Builder(this)
                        .setTitle("SUCCESS")
                        .setMessage("Enrollment Complete!")
                        .setNegativeButton("OK") { dialog, _ ->
                            dialog.dismiss()
                            finish()
                        }
                        .show()
                    } else {
                        Toast.makeText(this, "Failed to enroll student.", Toast.LENGTH_LONG).show()
                    }

                    // update global variable
                    StudentData.status = "enrolled"

                    dialog.dismiss()
                }
                .setNegativeButton("NO") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        // add / remove subject button
        findViewById<Button>(R.id.addOrRemove).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("NOTICE")
                .setMessage("Edit registered subjects?")
                .setPositiveButton("YES") { dialog, _ ->
                    startActivity(Intent(this, EditRegisteredSubjects::class.java))
                    dialog.dismiss()
                }
                .setNegativeButton("NO") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        // back button
        findViewById<ImageView>(R.id.backBtn).setOnClickListener { finish() }
    }
    override fun onResume() {
        super.onResume()

        val dbHelper = DatabaseHelper(this)

        val updatedSubjects = dbHelper.getSubjectsForStudent(StudentData.studentNo)

        val subjectListView = findViewById<ListView>(R.id.subjects)
        val adapter = SubjectAdapter(this, updatedSubjects)
        subjectListView.adapter = adapter

        adapter.notifyDataSetChanged()
    }

}