package com.loosethread.moodsignals.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.loosethread.moodsignals.database.Db
import com.loosethread.moodsignals.databinding.ItemTodayBinding
import com.loosethread.moodsignals.datatypes.DaySignalValue
import com.loosethread.moodsignals.datatypes.Signal

class FragmentSingleItemToday : Fragment() {
    var onScoreSelected: ((signalId: Int, score: Int) -> Unit)? = null
    var onCategoryId: ((categoryId: Int) -> Unit)? = null

    private val dayId: Int by lazy { requireArguments().getInt("dayId") }
    private val signalId: Int by lazy { requireArguments().getInt("signalId") }

    private var _binding: ItemTodayBinding? = null
    private val binding get() = _binding!!

    private var daySignalValues = mutableListOf<DaySignalValue>()
    private lateinit var buttons: List<Button>
    private lateinit var signal: Signal

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ItemTodayBinding.inflate(inflater, container, false)

        daySignalValues = Db.getDaySignalValues(dayId)
        signal = Db.getSignal(signalId)

        buttons =  listOf(
            binding.btnScoreGreen,
            binding.btnScoreOrange,
            binding.btnScoreRed
        )

        binding.tvSignalName.setText(signal.description)

        for(index in buttons.indices) {
            if (daySignalValues.any { it.signalId == signal.id && it.score == signal.scores[index].score }) {
                setButtonText(buttons[index], signal.scores[index].description.toString(), true)
            } else {
                setButtonText(buttons[index], signal.scores[index].description.toString(), false)
            }

            buttons[index].setOnClickListener {
                updateButtonColors(index)
                onScoreSelected?.invoke(signalId, signal.scores[index].score)
            }
        }

        binding.btnScoreNone.setOnClickListener {
            updateButtonColors(-1)
            onScoreSelected?.invoke(signalId, -1)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onCategoryId?.invoke(signal.categoryId!!)
    }

    override fun onResume() {
        super.onResume()
        daySignalValues = Db.getDaySignalValues(dayId)
        signal = Db.getSignal(signalId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun updateButtonColors(index: Int) {
        for(i in buttons.indices) {
            if (i == index) {
                setButtonText(buttons[i], signal.scores[i].description.toString(), true)
            } else {
                setButtonText(buttons[i], signal.scores[i].description.toString(), false)
            }
        }
    }

    fun setButtonText(button: Button, text: String, selected: Boolean) {
        if (selected) {
            button.setText("\u25b6 " + text + " \u25c0")
        } else {
            button.setText(text)
        }
    }
}