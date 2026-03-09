package com.loosethread.moodsignals

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAddSignalBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAdd.setOnClickListener {
            var scores = mutableListOf<SignalScore>()
            scores.add(SignalScore(1, binding.etSignalScore1.text.toString()))
            scores.add(SignalScore(2, binding.etSignalScore2.text.toString()))
            scores.add(SignalScore(3, binding.etSignalScore3.text.toString()))

            val signal = Signal(binding.etSignalName.text.toString(), scores)
            Db.addSignal(signal)
            findNavController().navigate(R.id.action_fragmentAddSignal_to_SignalsFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}