package com.loosethread.moodsignals.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.loosethread.moodsignals.MainActivity
import com.loosethread.moodsignals.R
import com.loosethread.moodsignals.databinding.ItemSignalBinding
import com.loosethread.moodsignals.datatypes.Signal

class SignalAdapter(
    private val signals: MutableList<Signal>
) : RecyclerView.Adapter<SignalAdapter.SignalViewHolder>() {

    inner class SignalViewHolder(private val signalBinding: ItemSignalBinding) : RecyclerView.ViewHolder(signalBinding.root) {
        fun bind(signal: Signal, position: Int) {
            signalBinding.tvSignalName.text = signal.description

            signalBinding.tvSignalName.setOnClickListener {
                editSignal(signal.id!!)
            }
        }

        private fun editSignal(id: Int) {
            val bundle = bundleOf("id" to id)
            Navigation.findNavController(
                activity = signalBinding.root.context as MainActivity,
                viewId = R.id.rvSignals
            ).navigate(R.id.action_SignalsFragment_to_fragmentAddSignal, bundle)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SignalViewHolder {
        val signalBinding = ItemSignalBinding.inflate(LayoutInflater.from(parent.context))
        return SignalViewHolder(signalBinding)
    }

    override fun onBindViewHolder(holder: SignalViewHolder, position: Int) {
        val currentSignal = signals[position]
        if (currentSignal != null)
            holder.bind(currentSignal, position)
    }

    override fun getItemCount(): Int {
        return signals.size
    }

    fun submitList(signals: MutableList<Signal>) {
        notifyDataSetChanged()
    }



}