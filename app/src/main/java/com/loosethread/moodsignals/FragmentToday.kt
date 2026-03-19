package com.loosethread.moodsignals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.loosethread.moodsignals.databinding.FragmentTodayBinding

class FragmentToday : Fragment() {
    private var _binding: FragmentTodayBinding? = null
    lateinit var todayAdapter: TodayAdapter

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val signals = Db.getSignals()
        if (signals.size == 0) {
            findNavController().popBackStack()
        }

        _binding = FragmentTodayBinding.inflate(inflater, container, false)
        todayAdapter = TodayAdapter(Db.getSignals())
        binding.rvSignals.adapter = todayAdapter
        binding.rvSignals.layoutManager = FullWidthLinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}