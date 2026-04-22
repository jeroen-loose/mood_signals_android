package com.loosethread.moodsignals.fragments

import android.R
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.navigation.fragment.findNavController
import com.loosethread.moodsignals.database.Db
import com.loosethread.moodsignals.databinding.FragmentAddSignalBinding
import com.loosethread.moodsignals.datatypes.NotificationTime
import com.loosethread.moodsignals.datatypes.Signal
import com.loosethread.moodsignals.datatypes.SignalCategory
import com.loosethread.moodsignals.datatypes.SignalScore

class FragmentAddSignal : Fragment() {

    private var _binding: FragmentAddSignalBinding? = null

    private val binding get() = _binding!!
    private val notificationTimes = Db.getNotificationTimes()
    private val categories = Db.getCategories()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddSignalBinding.inflate(inflater, container, false)

        val category: Spinner = binding.spCategory
        ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_item,
            categories
        ).also { categoryAdapter ->
            categoryAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            category.adapter = categoryAdapter
        }

        val notificationTime: Spinner = binding.spNotificationTime
        ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_item,
            notificationTimes
        ).also { notificationTimeAdapter ->
                notificationTimeAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                notificationTime.adapter = notificationTimeAdapter
        }

        val id = getArguments()?.getInt("id")

        id?.let {
            val signal = Db.getSignal(id)
            binding.etSignalName.setText(signal.description)
            binding.etSignalScore1.setText(signal.scores[0].description)
            binding.etSignalScore2.setText(signal.scores[1].description)
            binding.etSignalScore3.setText(signal.scores[2].description)
            binding.smActiveChoice.isChecked = signal.activeChoice!!
            categories.find { it.id == signal.categoryId }?.let {
                binding.spCategory.setSelection(categories.indexOf(it))
            }
            notificationTimes.find { it.id == signal.notificationTimeId }?.let {
                binding.spNotificationTime.setSelection(notificationTimes.indexOf(it))
            }
            binding.btnAdd.text = "Save"
            binding.btnAdd.tag = id
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAdd.setOnClickListener {
            var scores = mutableListOf<SignalScore>()
            scores.add(SignalScore(1, binding.etSignalScore1.text.toString()))
            scores.add(SignalScore(2, binding.etSignalScore2.text.toString()))
            scores.add(SignalScore(3, binding.etSignalScore3.text.toString()))

            val signal = Signal(
                null,
                binding.etSignalName.text.toString(),
                scores,
                binding.smActiveChoice.isChecked,
                (binding.spCategory.selectedItem as SignalCategory).id!!,
                (binding.spNotificationTime.selectedItem as NotificationTime).id!!
            )
            if(binding.btnAdd.tag != null) {
                signal.id = binding.btnAdd.tag as Int?
                Db.updateSignal(signal)
            } else {
                Db.addSignal(signal)
            }
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}