package com.loosethread.moodnotificationTimes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.loosethread.moodsignals.Db
import com.loosethread.moodsignals.NotificationTimeAdapter
import com.loosethread.moodsignals.R
import com.loosethread.moodsignals.databinding.FragmentNotificationTimesBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class FragmentNotificationTimes : Fragment() {

    private var _binding: FragmentNotificationTimesBinding? = null
    lateinit var notificationTimeAdapter: NotificationTimeAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNotificationTimesBinding.inflate(inflater, container, false)
        notificationTimeAdapter = NotificationTimeAdapter(Db.getNotificationTimes())
        binding.rvNotificationTimes.adapter = notificationTimeAdapter
        binding.rvNotificationTimes.layoutManager = LinearLayoutManager(binding.root.context)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentNotificationTimes_to_fragmentAddNotificationTime)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}