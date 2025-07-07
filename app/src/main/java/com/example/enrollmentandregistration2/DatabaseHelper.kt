package com.example.enrollmentandregistration2

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "studentDB", null, 2) {

    override fun onCreate(db: SQLiteDatabase) {
        // Create table for students
        db.execSQL("""
            CREATE TABLE students (
                studentno TEXT PRIMARY KEY,
                fullname TEXT NOT NULL,
                password TEXT NOT NULL,
                status TEXT DEFAULT 'unregistered'
            )
        """)

        // Create table for subjects with composite PK: studentno + course_code
        db.execSQL("""
            CREATE TABLE subjects (
                studentno TEXT NOT NULL,
                course_code TEXT NOT NULL,
                name TEXT NOT NULL,
                units INTEGER NOT NULL,
                schedule TEXT NOT NULL,
                prof_name TEXT NOT NULL,
                PRIMARY KEY (studentno, course_code),
                FOREIGN KEY (studentno) REFERENCES students(studentno)
            )
        """)

        // Create junction table for subject assignments
        db.execSQL("""
            CREATE TABLE student_subjects (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                studentno TEXT NOT NULL,                  -- the student being assigned
                course_code TEXT NOT NULL,
                owner_studentno TEXT NOT NULL,            -- who owns the subject definition
                FOREIGN KEY(studentno) REFERENCES students(studentno),
                FOREIGN KEY(owner_studentno) REFERENCES students(studentno),
                FOREIGN KEY(course_code, owner_studentno) REFERENCES subjects(course_code, studentno)
            )
        """)
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS student_subjects")
        db.execSQL("DROP TABLE IF EXISTS students")
        onCreate(db)
    }

    fun registerStudent(studentNo: String, fullname: String, password: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("studentno", studentNo)
            put("fullname", fullname)
            put("password", password)
        }
        val result = db.insert("students", null, values)
        return result != -1L
    }

    fun loginStudent(studentNo: String, password: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM students WHERE studentno = ? AND password = ?",
            arrayOf(studentNo, password)
        )
        val loggedIn = cursor.count > 0
        cursor.close()
        return loggedIn
    }

    data class Student(
        val studentno: String,
        val fullname: String,
        val status: String
    )

    fun getStudent(studentNo: String): Student? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT studentno, fullname, status FROM students WHERE studentno = ?",
            arrayOf(studentNo)
        )

        var student: Student? = null
        if (cursor.moveToFirst()) {
            student = Student(
                studentno = cursor.getString(cursor.getColumnIndexOrThrow("studentno")),
                fullname = cursor.getString(cursor.getColumnIndexOrThrow("fullname")),
                status = cursor.getString(cursor.getColumnIndexOrThrow("status"))
            )
        }

        cursor.close()
        return student
    }

    fun updateStudentStatus(studentNo: String, status: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("status", status)
        }

        val rowsAffected = db.update(
            "students",       // table name
            values,           // new values
            "studentno = ?",  // where clause
            arrayOf(studentNo) // whereArgs
        )

        return rowsAffected > 0
    }

    fun addSubject(studentNo: String, code: String, name: String, units: Int, schedule: String, prof: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("studentno", studentNo)
            put("course_code", code)
            put("name", name)
            put("units", units)
            put("schedule", schedule)
            put("prof_name", prof)
        }
        return db.insert("subjects", null, values) != -1L
    }

    fun assignSubjectToStudent(studentNo: String, courseCode: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("studentno", studentNo)
            put("course_code", courseCode)
            put("owner_studentno", studentNo)
        }
        return db.insert("student_subjects", null, values) != -1L
    }

    fun getSubjectsForStudent(studentNo: String): List<Subject> {
        val subjects = mutableListOf<Subject>()
        val db = readableDatabase

        val cursor = db.rawQuery("""
            SELECT s.course_code, s.name, s.units, s.schedule, s.prof_name
            FROM subjects s
            INNER JOIN student_subjects ss
                ON s.course_code = ss.course_code AND s.studentno = ss.owner_studentno
            WHERE ss.studentno = ?
        """, arrayOf(studentNo))


        while (cursor.moveToNext()) {
            val subject = Subject(
                courseCode = cursor.getString(cursor.getColumnIndexOrThrow("course_code")),
                subjectName = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                units = cursor.getInt(cursor.getColumnIndexOrThrow("units")),
                schedule = cursor.getString(cursor.getColumnIndexOrThrow("schedule")),
                prof = cursor.getString(cursor.getColumnIndexOrThrow("prof_name"))
            )
            subjects.add(subject)
        }

        cursor.close()
        return subjects
    }

    fun clearStudentSubjectsFor(studentNo: String): Boolean {
        val db = writableDatabase
        return try {
            db.delete("student_subjects", "studentno = ?", arrayOf(studentNo))
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    fun clearStudentSubjects(): Boolean {
        val db = writableDatabase
        return try {
            db.execSQL("DELETE FROM student_subjects")
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


}

