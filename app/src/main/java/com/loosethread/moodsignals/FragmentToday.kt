package com.loosethread.moodsignals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.loosethread.moodsignals.databinding.FragmentSignalsBinding
import com.loosethread.moodsignals.databinding.FragmentTodayBinding

class FragmentToday : Fragment() {
    private var _binding: FragmentTodayBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val signals = Db.getSignals()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTodayBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadSignal(0)

        binding.btnNext.setOnClickListener {
            findNavController().navigate(R.id.action_SignalsFragment_to_fragmentAddSignal)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun loadSignal(index : Int) {
        val signal = signals[index]

        binding.txtSignal.text = signal.description
        binding.txtScoreGreen.text = signal.scores[0].description
        binding.txtScoreOrange.text = signal.scores[1].description
        binding.txtScoreRed.text = signal.scores[2].description

        if (index == 0) {
            binding.btnPrevious.visibility = INVISIBLE
        } else {
            binding.btnPrevious.visibility = VISIBLE
        }

        if (index == signals.count()) {
            binding.btnNext.text = "Finish"
        }
    }
}