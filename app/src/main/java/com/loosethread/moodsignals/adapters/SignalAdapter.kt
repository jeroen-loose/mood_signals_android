package com.loosethread.moodsignals.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.loosethread.moodsignals.MainActivity
import com.loosethread.moodsignals.R
import com.loosethread.moodsignals.database.Db
import com.loosethread.moodsignals.databinding.ItemSignalBinding
import com.loosethread.moodsignals.datatypes.Signal
import com.loosethread.moodsignals.dialogs.DeleteSignalDialog

class SignalAdapter(
    private val signals: MutableList<Signal>,
    private val fragmentManager: FragmentManager,
    private val lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<SignalAdapter.SignalViewHolder>() {

    inner class SignalViewHolder(private val binding: ItemSignalBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(signal: Signal, position: Int) {
            binding.tvSignalName.text = signal.description

            binding.tvSignalName.setOnClickListener {
                editSignal(signal.id!!)
            }

            if (signals.size == 1) {
                binding.ibDelete.visibility = ImageButton.GONE
            } else {
                val requestKeyDelete = "delete_signal${signal.id}"
                binding.ibDelete.setOnClickListener {
                    val dialog = DeleteSignalDialog()
                    dialog.arguments = bundleOf(
                        "requestKey" to requestKeyDelete,
                        "id" to signal.id
                    )
                    dialog.show(fragmentManager, "DeleteSignalFragment${signal.id}")
                }

                fragmentManager.setFragmentResultListener(
                    requestKeyDelete,
                    lifecycleOwner
                ) { _, bundle ->
                    val deleted = bundle.getBoolean("isDeleted", false)
                    if (deleted) {
                        val archive = bundle.getBoolean("archive")
                        Db.deleteSignal(signal.id!!, archive)
                        signals.removeAt(position)
                        notifyItemRemoved(position)
                    }
                }
            }
        }

        private fun editSignal(id: Int) {
            val bundle = bundleOf("id" to id)
            Navigation.findNavController(
                activity = binding.root.context as MainActivity,
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