package com.loosethread.moodsignals.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.loosethread.moodsignals.R
import com.loosethread.moodsignals.databinding.FragmentHomeBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FragmentHome : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSignals.setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_SignalsFragment)
        }

        binding.buttonToday.setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_fragmentToday)
        }

        binding.btnNotificationTimes.setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_fragmentNotificationTimes)
        }

        binding.btnLog.setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_fragmentDaysLog)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}