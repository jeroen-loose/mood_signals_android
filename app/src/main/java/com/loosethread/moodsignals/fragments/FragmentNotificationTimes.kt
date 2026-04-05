package com.loosethread.moodsignals

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.loosethread.moodsignals.database.Db
import com.loosethread.moodsignals.FullWidthLinearLayoutManager
import com.loosethread.moodsignals.adapters.NotificationTimeAdapter
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
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNotificationTimesBinding.inflate(inflater, container, false)
        notificationTimeAdapter = NotificationTimeAdapter(Db.getNotificationTimes(), childFragmentManager, viewLifecycleOwner)
        binding.rvNotificationTimes.adapter = notificationTimeAdapter
        layoutManager = FullWidthLinearLayoutManager(requireContext())
        val dividerItemDecoration = DividerItemDecoration(binding.root.context, LinearLayoutManager.VERTICAL)
        binding.rvNotificationTimes.addItemDecoration(dividerItemDecoration)

        binding.rvNotificationTimes.layoutManager = layoutManager
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