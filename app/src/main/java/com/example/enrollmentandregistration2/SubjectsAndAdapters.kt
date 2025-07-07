package com.example.enrollmentandregistration2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

data class Subject(
    val courseCode: String,
    val subjectName: String,
    val units: Int,
    val schedule: String,
    val prof: String
)

// adapter for subjects for registration and enrollment
class SubjectAdapter(
    private val context: Context,
    private val subjects: List<Subject>
) : ArrayAdapter<Subject>(context, 0, subjects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val subject = subjects[position]
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.subject_item, parent, false)

        view.findViewById<TextView>(R.id.courseCode).text = "${subject.courseCode}"
        view.findViewById<TextView>(R.id.subjectName).text = "${subject.subjectName}"
        view.findViewById<TextView>(R.id.units).text = "${subject.units}"
        view.findViewById<TextView>(R.id.schedule).text = "${subject.schedule}"
        view.findViewById<TextView>(R.id.prof).text = "${subject.prof}"

        return view
    }
}

// adapter for subjects for fee interface
class SubjectAdapterWithFee(
    private val context: Context,
    private val subjects: List<Subject>
) : ArrayAdapter<Subject>(context, 0, subjects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val subject = subjects[position]
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.subject_item_with_fee, parent, false)

        view.findViewById<TextView>(R.id.courseCode).text = subject.courseCode
        view.findViewById<TextView>(R.id.subjectName).text = subject.subjectName
        view.findViewById<TextView>(R.id.units).text = "${subject.units}"

        // You handle fee calculation; example fee = units * 1000
        val calculatedFee = subject.units * 300
        view.findViewById<TextView>(R.id.fee).text = "₱%,.2f".format(calculatedFee.toDouble())

        return view
    }
}

// adapter for misc items for fee interface
class MiscFeeAdapter(
    context: Context,
    private val miscList: List<Pair<String, Int>>
) : ArrayAdapter<Pair<String, Int>>(context, 0, miscList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = miscList[position]
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.misc_item, parent, false)

        view.findViewById<TextView>(R.id.miscName).text = item.first
        view.findViewById<TextView>(R.id.miscFee).text = "₱%,.2f".format(item.second.toDouble())

        return view
    }
}


// adapter for subjects with delete button
class SubjectAdapterWithDelete(
    private val context: Context,
    private val subjects: MutableList<Subject>
) : ArrayAdapter<Subject>(context, 0, subjects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.subject_item_with_delete, parent, false)

        val subject = subjects[position]

        val subjectInfo = view.findViewById<TextView>(R.id.subjectInfo)
        val deleteButton = view.findViewById<Button>(R.id.deleteButton)

        subjectInfo.text = "Course Code: ${subject.courseCode}\nSubject: ${subject.subjectName}\nUnits: ${subject.units}\nSchedule: ${subject.schedule}\nProfessor: ${subject.prof}"

        deleteButton.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("NOTICE")
                .setMessage("Delete ${subject.courseCode}?")
                .setPositiveButton("YES") { dialog, _ ->
                    subjects.removeAt(position)
                    notifyDataSetChanged()
                    dialog.dismiss()
                }
                .setNegativeButton("NO") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
        return view
    }
}

