package com.example.enrollmentandregistration2

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONArray

class FeeInterface : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fee_interface)

        // function for reading misc fees file
        fun readMiscellaneousFees(context: Context): List<Pair<String, Int>> {
            val assetManager = context.assets
            val inputStream = assetManager.open("miscellaneous_fees.txt")
            val jsonString = inputStream.bufferedReader().use { it.readText() }

            val fees = mutableListOf<Pair<String, Int>>()
            val jsonArray = JSONArray(jsonString)

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val name = obj.getString("name")
                val fee = obj.getInt("fee")
                fees.add(Pair(name, fee))
            }

            return fees
        }

        findViewById<TextView>(R.id.studentno).text = StudentData.studentNo
        findViewById<TextView>(R.id.fullname).text = StudentData.fullname

        val dbHelper = DatabaseHelper(this)

        // calculate total fee for subjects
        val enrolledSubjects = dbHelper.getSubjectsForStudent(StudentData.studentNo).toList()
        var totalUnits = 0
        for (i in 0 until enrolledSubjects.count()) {
            totalUnits += enrolledSubjects[i].units
        }
//        Log.d("UNITS COUNT", "${totalUnits}")
        val subjectsFee = totalUnits * 300

        // calculate total fee for miscellaneous
        val miscellaneous = readMiscellaneousFees(this)
        var miscFee = 0
        for (i in 0 until miscellaneous.count()) {
            miscFee += miscellaneous[i].second
        }
        Log.d("MISC FEE", "${miscFee}")

        // calculate overall total fee
        val totalFee = subjectsFee + miscFee


        // subjects list view
        val selectedSubjects = dbHelper.getSubjectsForStudent(StudentData.studentNo).toMutableList()
        val subjectListView = findViewById<ListView>(R.id.subjects)
        val subjectAdapter = SubjectAdapterWithFee(this, selectedSubjects)
        subjectListView.adapter = subjectAdapter
        findViewById<TextView>(R.id.subjectsTotalFee).text = "₱%,.2f".format(subjectsFee.toDouble())

        // misc list view
        val miscListView = findViewById<ListView>(R.id.misc)
        val miscAdapter = MiscFeeAdapter(this, miscellaneous)
        miscListView.adapter = miscAdapter
        findViewById<TextView>(R.id.miscTotalFee).text = "₱%,.2f".format(miscFee.toDouble())

        // overall total fee
        findViewById<TextView>(R.id.totalFee).text = "₱%,.2f".format(totalFee.toDouble())

        // back button
        findViewById<ImageView>(R.id.backBtn).setOnClickListener { finish() }
    }
}