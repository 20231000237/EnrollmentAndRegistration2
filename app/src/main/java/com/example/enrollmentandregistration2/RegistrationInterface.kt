package com.example.enrollmentandregistration2

import android.os.Bundle
import android.provider.ContactsContract.Data
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegistrationInterface : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_interface)

        val dbHelper = DatabaseHelper(this)

        val selectedSubjects = mutableListOf<Subject>()
        // list view
        val subjectListView = findViewById<ListView>(R.id.subjects)
        val adapter = SubjectAdapterWithDelete(this, selectedSubjects)
        subjectListView.adapter = adapter

        findViewById<Button>(R.id.addSubjectButton).setOnClickListener {
            val courseCode = findViewById<EditText>(R.id.courseCode).text.toString()
            val subjectName = findViewById<EditText>(R.id.subjectName).text.toString()
            val units = findViewById<EditText>(R.id.units).text.toString()
            val schedule = findViewById<EditText>(R.id.schedule).text.toString()
            val professor = findViewById<EditText>(R.id.prof).text.toString()

            if (courseCode.isNotBlank() && subjectName.isNotBlank() && units.isNotBlank() && schedule.isNotBlank() && professor.isNotBlank()) {
                val newSubject = Subject(courseCode, subjectName, units.toInt(), schedule, professor)
                selectedSubjects.add(newSubject)
                // reset input fields
                findViewById<EditText>(R.id.courseCode).setText("")
                findViewById<EditText>(R.id.subjectName).setText("")
                findViewById<EditText>(R.id.units).setText("")
                findViewById<EditText>(R.id.schedule).setText("")
                findViewById<EditText>(R.id.prof).setText("")
                // update ListView
                adapter.notifyDataSetChanged()
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

        findViewById<Button>(R.id.registerbtn).setOnClickListener {
            // add handler if selected subjects is empty
            if (selectedSubjects.isEmpty()) {
                AlertDialog.Builder(this)
                    .setTitle("NOTICE")
                    .setMessage("Please add at least one subject!")
                    .setNegativeButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            } else {
                // add subjects to Subjects table in database
                for (i in 0 until selectedSubjects.count()) {
                    val currentSubject = selectedSubjects[i]
                    dbHelper.addSubject(StudentData.studentNo, currentSubject.courseCode, currentSubject.subjectName, currentSubject.units, currentSubject.schedule, currentSubject.prof)
                }
                // assign subjects to student
                for (i in 0 until selectedSubjects.count()) {
                    val currentSubject = selectedSubjects[i]
                    dbHelper.assignSubjectToStudent(StudentData.studentNo, currentSubject.courseCode)
                }
                // update status of student (unregistered -> registered)
                if (dbHelper.updateStudentStatus(StudentData.studentNo, "registered")) {
                    StudentData.status = "registered"
                    AlertDialog.Builder(this)
                        .setTitle("SUCCESS")
                        .setMessage("Registration Complete!")
                        .setNegativeButton("OK") { dialog, _ ->
                            dialog.dismiss()
                            finish()
                        }
                        .show()
                } else {
                    Toast.makeText(this, "Failed to register selected subjects.", Toast.LENGTH_LONG).show()
                }
            }
        }

        // back button
        findViewById<ImageView>(R.id.backBtn).setOnClickListener {
            if (selectedSubjects.isNotEmpty()) {
                AlertDialog.Builder(this)
                    .setTitle("NOTICE")
                    .setMessage("You have unsaved changes, continue?")
                    .setNegativeButton("NO") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("YES") { dialog, _ ->
                        dialog.dismiss()
                        finish()
                    }
                    .show()
            } else {
                finish()
            }
        }


    }
}