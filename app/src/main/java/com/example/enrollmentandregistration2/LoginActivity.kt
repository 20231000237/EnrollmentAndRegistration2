package com.example.enrollmentandregistration2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViewById<TextView>(R.id.addNewStudentBtn).setOnClickListener {
            val intent = Intent(this, AddStudentActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.loginbtn).setOnClickListener {
            val studentNo = findViewById<EditText>(R.id.studentno).text.toString()
            val password = findViewById<EditText>(R.id.password).text.toString()

            val dbHelper = DatabaseHelper(this)

            // execute sql queries here to manipulate the database
//            dbHelper.updateStudentStatus(studentNo, "registered")
//            dbHelper.clearStudentSubjects()


            // end of "here"



            if (dbHelper.loginStudent(studentNo, password)) {
                val loggedInStudent = dbHelper.getStudent(studentNo)
                if (loggedInStudent != null) {
                    StudentData.studentNo = loggedInStudent.studentno
                    StudentData.fullname = loggedInStudent.fullname
                    StudentData.status = loggedInStudent.status
                }

                Toast.makeText(this, "Login Successful!", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, MainMenuActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Incorrect Credentials!", Toast.LENGTH_LONG).show()
            }
        }
    }
}