package com.loosethread.moodsignals.fragments

import com.loosethread.moodsignals.fragments.DatePickerFragment
import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.loosethread.moodsignals.FullWidthLinearLayoutManager
import com.loosethread.moodsignals.adapters.TodayAdapter
import com.loosethread.moodsignals.database.Db
import com.loosethread.moodsignals.databinding.FragmentTodayBinding
import com.loosethread.moodsignals.datatypes.NotificationTime
import com.loosethread.moodsignals.helpers.DateManager

class FragmentToday : Fragment() {
    private var _binding: FragmentTodayBinding? = null
    lateinit var todayAdapter: TodayAdapter

    private val binding get() = _binding!!
    private val notificationTimes = Db.getNotificationTimes()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val signals = Db.getSignals()
        if (signals.size == 0) {
            findNavController().popBackStack()
        }


        _binding = FragmentTodayBinding.inflate(inflater, container, false)

        binding.tvDate.text = DateManager.formatForDisplay()

        val spinner: Spinner = binding.spNotificationTime
        ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_item,
            notificationTimes
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        val notificationTime = spinner.selectedItem as NotificationTime

        todayAdapter =
            TodayAdapter(Db.getSignals(notificationTime.id), DateManager.formatForSql()) {
                binding.clComment.setVisibility(View.VISIBLE)
                binding.btnDone.setOnClickListener {
                    val comment = binding.etComment.text.toString()
                    Db.updateComment(todayAdapter.dayId, comment)

                    findNavController().popBackStack()
                }
            }
        binding.rvSignals.adapter = todayAdapter
        binding.rvSignals.layoutManager = FullWidthLinearLayoutManager(
            binding.root.context,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(pos) as NotificationTime
                todayAdapter.updateSignals(Db.getSignals(selectedItem.id))
                todayAdapter.notifyDataSetChanged()
                resetViews()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.llDate.setOnClickListener {
            val dialog = DatePickerFragment()
            dialog.show(parentFragmentManager, "datePicker")
            parentFragmentManager.setFragmentResultListener("requestKey", viewLifecycleOwner) { requestKey, bundle ->
                binding.tvDate.text = DateManager.formatForDisplay()
                todayAdapter.setDate(DateManager.formatForSql())
            }
        }
        binding.etComment.setText(Db.getComment(todayAdapter.dayId))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun resetViews() {
        binding.clComment.setVisibility(View.INVISIBLE)
    }

}