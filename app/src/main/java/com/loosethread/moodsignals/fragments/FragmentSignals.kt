package com.loosethread.moodsignals.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.loosethread.moodsignals.FullWidthLinearLayoutManager
import com.loosethread.moodsignals.R
import com.loosethread.moodsignals.adapters.SignalAdapter
import com.loosethread.moodsignals.database.Db
import com.loosethread.moodsignals.databinding.FragmentSignalsBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class FragmentSignals : Fragment() {

    private var _binding: FragmentSignalsBinding? = null
    lateinit var signalAdapter: SignalAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSignalsBinding.inflate(inflater, container, false)
        signalAdapter = SignalAdapter(Db.getSignals())
        binding.rvSignals.adapter = signalAdapter
        binding.rvSignals.layoutManager = FullWidthLinearLayoutManager(binding.root.context)
        val dividerItemDecoration = DividerItemDecoration(binding.root.context, LinearLayoutManager.VERTICAL)
        binding.rvSignals.addItemDecoration(dividerItemDecoration)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_SignalsFragment_to_fragmentAddSignal)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}