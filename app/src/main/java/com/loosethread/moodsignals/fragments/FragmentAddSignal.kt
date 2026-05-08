package com.loosethread.moodsignals.fragments

import android.R
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.loosethread.moodsignals.database.Db
import com.loosethread.moodsignals.databinding.FragmentAddSignalBinding
import com.loosethread.moodsignals.datatypes.NotificationTime
import com.loosethread.moodsignals.datatypes.Signal
import com.loosethread.moodsignals.datatypes.SignalCategory
import com.loosethread.moodsignals.datatypes.SignalScore
import com.loosethread.moodsignals.dialogs.DeleteSignalDialog
import com.loosethread.moodsignals.dialogs.UpdateSignalDialog

class FragmentAddSignal : Fragment() {

    private var _binding: FragmentAddSignalBinding? = null

    private val binding get() = _binding!!
    private val notificationTimes = Db.getNotificationTimes()
    private val categories = Db.getCategories()
    private lateinit var signal : Signal

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddSignalBinding.inflate(inflater, container, false)

        val category: Spinner = binding.spCategory
        ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_item,
            categories
        ).also { categoryAdapter ->
            categoryAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            category.adapter = categoryAdapter
        }

        val notificationTime: Spinner = binding.spNotificationTime
        ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_item,
            notificationTimes
        ).also { notificationTimeAdapter ->
                notificationTimeAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                notificationTime.adapter = notificationTimeAdapter
        }

        val id = getArguments()?.getInt("id")

        id?.let {
            signal = Db.getSignal(id)
            binding.etSignalName.setText(signal.description)
            binding.etSignalScore1.setText(signal.scores[0].description)
            binding.etSignalScore2.setText(signal.scores[1].description)
            binding.etSignalScore3.setText(signal.scores[2].description)
            binding.smActiveChoice.isChecked = signal.activeChoice!!
            categories.find { it.id == signal.categoryId }?.let {
                binding.spCategory.setSelection(categories.indexOf(it))
            }
            notificationTimes.find { it.id == signal.notificationTimeId }?.let {
                binding.spNotificationTime.setSelection(notificationTimes.indexOf(it))
            }
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

            val newSignal = Signal(
                null,
                binding.etSignalName.text.toString(),
                scores,
                binding.smActiveChoice.isChecked,
                (binding.spCategory.selectedItem as SignalCategory).id!!,
                (binding.spNotificationTime.selectedItem as NotificationTime).id!!
            )
            if(binding.btnAdd.tag != null) {
                newSignal.id = binding.btnAdd.tag as Int?
                if (Db.signalHasEntries(signal.id!!) && hasChanged(newSignal, signal)) {
                    val requestKeyUpdate = "update_signal${signal.id}"
                    val dialog = UpdateSignalDialog()
                    dialog.arguments = bundleOf(
                        "requestKey" to requestKeyUpdate
                    )
                    dialog.show(childFragmentManager, "UpdateSignalFragment${signal.id}")

                    childFragmentManager.setFragmentResultListener(
                        requestKeyUpdate,
                        viewLifecycleOwner
                    ) { _, bundle ->
                        val updated = bundle.getBoolean("isUpdated", false)
                        if (updated) {
                            val updatedPermanently = bundle.getBoolean("isUpdatedPermanently", false)
                            if (updatedPermanently) {
                                Db.updateSignal(newSignal)
                            } else {
                                Db.deleteSignal(signal.id!!, true)
                                Db.addSignal(newSignal)
                            }
                        }
                        findNavController().popBackStack()
                    }

                } else {
                    Db.updateSignal(newSignal)
                    findNavController().popBackStack()
                }
            } else {
                Db.addSignal(newSignal)
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun hasChanged(newSignal: Signal, oldSignal: Signal) : Boolean {
        return newSignal.description != oldSignal.description ||
                newSignal.scores[0].description != oldSignal.scores[0].description ||
                newSignal.scores[1].description != oldSignal.scores[1].description ||
                newSignal.scores[2].description != oldSignal.scores[2].description
    }
}