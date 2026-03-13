package com.loosethread.moodsignals

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.navigation.fragment.findNavController
import com.loosethread.moodsignals.databinding.FragmentAddSignalBinding
import com.loosethread.moodsignals.databinding.FragmentSignalsBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class FragmentAddSignal : Fragment() {

    private var _binding: FragmentAddSignalBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val notificationTimes = Db.getNotificationTimes()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddSignalBinding.inflate(inflater, container, false)

        val spinner: Spinner = binding.spNotificationTime
        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            notificationTimes
        ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
        }

        val id = getArguments()?.getInt("id")

        id?.let {
            val signal = Db.getSignal(id)
            binding.etSignalName.setText(signal.description)
            binding.etSignalScore1.setText(signal.scores[0].description)
            binding.etSignalScore2.setText(signal.scores[1].description)
            binding.etSignalScore3.setText(signal.scores[2].description)
            binding.cbActiveChoice.isChecked = signal.activeChoice!!
            binding.spNotificationTime.setSelection(notificationTimes.indexOf(Db.getNotificationTime(signal.notificationTimeId!!)))
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
                binding.cbActiveChoice.isChecked,
                binding.spNotificationTime.adapter.getItem(binding.spNotificationTime.selectedItemPosition).toString().toInt()
            )
            if(binding.btnAdd.tag != null) {
                signal.id = binding.btnAdd.tag as Int?
                Db.updateSignal(signal)
            } else {
                Db.addSignal(signal)
            }
            findNavController().navigate(R.id.action_fragmentAddSignal_to_SignalsFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}