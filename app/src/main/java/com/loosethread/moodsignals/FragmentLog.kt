package com.loosethread.moodsignals

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.loosethread.moodsignals.databinding.FragmentLogBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class FragmentLog : Fragment() {

    private var _binding: FragmentLogBinding? = null
    private lateinit var logAdapter: LogAdapter

    private val binding get() = _binding!!
    private var currentIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLogBinding.inflate(inflater, container, false)
        logAdapter = LogAdapter(Db.getDays())
        binding.rvDaysLog.adapter = logAdapter
        val manager = FullWidthLinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvDaysLog.layoutManager = manager

        binding.btnPrevious.setOnClickListener {
            goToIndex(currentIndex + 1)
        }

        binding.btnNext.setOnClickListener {
            goToIndex(currentIndex - 1)
        }
         binding.btnNext.setVisibility(View.INVISIBLE)

        setDate()

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun goToIndex(newIndex: Int) {
        binding.rvDaysLog.smoothScrollToPosition(newIndex)
        currentIndex = newIndex
        if (currentIndex == logAdapter.getItemCount() - 1) {
            binding.btnPrevious.setVisibility(View.INVISIBLE)
        } else {
            binding.btnPrevious.setVisibility(View.VISIBLE)
        }

        if (currentIndex == 0) {
            binding.btnNext.setVisibility(View.INVISIBLE)
        } else {
            binding.btnNext.setVisibility(View.VISIBLE)
        }

        setDate()
    }

    fun setDate() {
        binding.tvDate.text = DateManager.formatStringForDisplay(logAdapter.days[currentIndex].date)
    }

}