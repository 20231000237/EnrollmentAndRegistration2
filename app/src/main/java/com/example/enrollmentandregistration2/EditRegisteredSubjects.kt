package com.example.enrollmentandregistration2

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class EditRegisteredSubjects : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_registered_subjects)

        val dbHelper = DatabaseHelper(this)

        // combine registered and to-be-added subjects in one list
        val allSubjects = dbHelper.getSubjectsForStudent(StudentData.studentNo).toMutableList()

        val subjectListView = findViewById<ListView>(R.id.subjects)
        val adapter = SubjectAdapterWithDelete(this, allSubjects)
        subjectListView.adapter = adapter

        findViewById<Button>(R.id.addSubjectButton).setOnClickListener {
            val courseCode = findViewById<EditText>(R.id.courseCode).text.toString()
            val subjectName = findViewById<EditText>(R.id.subjectName).text.toString()
            val units = findViewById<EditText>(R.id.units).text.toString()
            val schedule = findViewById<EditText>(R.id.schedule).text.toString()
            val professor = findViewById<EditText>(R.id.prof).text.toString()

            if (courseCode.isNotBlank() && subjectName.isNotBlank() && units.isNotBlank() && schedule.isNotBlank() && professor.isNotBlank()) {
                val newSubject = Subject(courseCode, subjectName, units.toInt(), schedule, professor)
                allSubjects.add(newSubject)

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

        // apply button
        findViewById<Button>(R.id.applybtn).setOnClickListener {
            if (allSubjects.isEmpty()) {
                AlertDialog.Builder(this)
                    .setTitle("NOTICE")
                    .setMessage("Please add at least one subject!")
                    .setNegativeButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            } else {
                // first, update database to remove all registered subjects to student
                dbHelper.clearStudentSubjectsFor(StudentData.studentNo)

                // then, add all subjects in the allSubjects list to the student
                // add subjects to Subjects table in database
                for (i in 0 until allSubjects.count()) {
                    val currentSubject = allSubjects[i]
                    dbHelper.addSubject(StudentData.studentNo, currentSubject.courseCode, currentSubject.subjectName, currentSubject.units, currentSubject.schedule, currentSubject.prof)
                }
                // assign subjects to student
                for (i in 0 until allSubjects.count()) {
                    val currentSubject = allSubjects[i]
                    dbHelper.assignSubjectToStudent(StudentData.studentNo, currentSubject.courseCode)
                }

                // get currently registered subjects
                val currentlyRegisteredSubjects = dbHelper.getSubjectsForStudent(StudentData.studentNo).toMutableList()
                if (currentlyRegisteredSubjects.toList() == allSubjects.toList()) {
                    AlertDialog.Builder(this)
                        .setTitle("NOTICE")
                        .setMessage("Registered Subjects updated successfully.")
                        .setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                            finish()
                        }
                        .show()
                } else {
                    Toast.makeText(this, "Failed to update registered subjects!", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }





        // back button
        // get currently registered subjects
        val currentlyRegisteredSubjects = dbHelper.getSubjectsForStudent(StudentData.studentNo).toMutableList()
        findViewById<ImageView>(R.id.backBtn).setOnClickListener {
            if (allSubjects.toList() != currentlyRegisteredSubjects.toList()) {
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