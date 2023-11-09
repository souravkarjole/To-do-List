package com.example.todolist

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter


class TaskOverView : Fragment() {

    lateinit var completeCount:TextView
    lateinit var pendingCount:TextView
    lateinit var pieChart: PieChart


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view:View =  inflater.inflate(R.layout.fragment_task_over_view, container, false)

        completeCount = view.findViewById(R.id.completedCount)
        pendingCount = view.findViewById(R.id.pendingCount)
        pieChart = view.findViewById(R.id.chart)

        setThePieChart()

        completeCount.text = AllTasks.totalCompletedTasks.toString()
        pendingCount.text = AllTasks.totalPendingTasks.toString()

        return view
    }

    override fun onResume() {
        super.onResume()

        setThePieChart()
        completeCount.text = AllTasks.totalCompletedTasks.toString()
        pendingCount.text = AllTasks.totalPendingTasks.toString()
    }


    private fun setThePieChart(){
        val array = mutableListOf<PieEntry>()

        array.add(PieEntry(AllTasks.totalCompletedTasks.toFloat() + 0.1f,"Completed"))
        array.add(PieEntry(AllTasks.totalInCompleteTasks.toFloat(),"In-complete"))

        // takes PieEntry instances along with Chart NAME, and pieDataSet is used to set the "text color", "value format", "color list"
        var pieDataSet = PieDataSet(array,"")


        // used my own color
        var color:MutableList<Int> = arrayListOf()
        color.add(resources.getColor(R.color.app_color_medium))
        color.add(resources.getColor(R.color.app_color_thin))


        pieDataSet.colors = color // takes color instance
        pieDataSet.valueTextColor = Color.WHITE
        pieDataSet.valueTextSize = 15f
        pieDataSet.valueFormatter = valueFormatter   // used this because floating values were displayed as e.g 0.100



        // PieData class's constructor take PieDataSet
        var pieData = PieData(pieDataSet)


        // PieChart class's instance is used to set the PieData
        pieChart.data = pieData
        pieChart.invalidate()    // used to refresh the chart each time values changes, if not invoked than changes are not immediately reflected
    }

    private val valueFormatter = object : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return value.toInt().toString()
        }
    }
}