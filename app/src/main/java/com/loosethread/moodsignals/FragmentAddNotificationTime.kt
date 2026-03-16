package com.loosethread.moodsignals

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.loosethread.moodsignals.databinding.FragmentAddNotificationTimeBinding

class FragmentAddNotificationTime : Fragment() {
    private var _binding: FragmentAddNotificationTimeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddNotificationTimeBinding.inflate(inflater, container, false)
        binding.tpTime.setIs24HourView(true)

        val id = getArguments()?.getInt("id")

        id?.let {
            val notificationTime = Db.getNotificationTime(id)
            binding.etTitle.setText(notificationTime.title)
            binding.etQuestion.setText(notificationTime.question)
            binding.tpTime.hour = notificationTime.time!!.substring(0, 2).toInt()
            binding.tpTime.minute = notificationTime.time!!.substring(3, 5).toInt()
            binding.btnAdd.text = "Save"
            binding.btnAdd.tag = id
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAdd.setOnClickListener {
            val notificationTime = NotificationTime(
                null,
                binding.etTitle.text.toString(),
                binding.etQuestion.text.toString(),
                binding.tpTime.hour.toString() + ":" + binding.tpTime.minute.toString(),
            )
            if(binding.btnAdd.tag != null) {
                notificationTime.id = binding.btnAdd.tag as Int?
                Db.updateNotificationTime(notificationTime)
            } else {
                Db.addNotificationTime(notificationTime)
            }
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}