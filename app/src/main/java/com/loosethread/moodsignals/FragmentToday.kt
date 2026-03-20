package com.loosethread.moodsignals

import DatePickerFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.loosethread.moodsignals.databinding.FragmentTodayBinding
import java.util.Locale

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

        binding.tvDate.text = DateManager.formatForDisplay()

        todayAdapter = TodayAdapter(Db.getSignals(), DateManager.formatForSql()) {
            binding.clComment.setVisibility(View.VISIBLE)
            binding.btnDone.setOnClickListener {
                val comment = binding.etComment.text.toString()
                Db.updateComment(todayAdapter.dayId, comment)

                findNavController().popBackStack()
            }
        }
        binding.rvSignals.adapter = todayAdapter
        binding.rvSignals.layoutManager = FullWidthLinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
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

}