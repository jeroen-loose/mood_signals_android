package com.loosethread.moodsignals

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.loosethread.moodsignals.database.Db
import com.loosethread.moodsignals.adapters.CategoryAdapter
import com.loosethread.moodsignals.databinding.FragmentCategoriesBinding
import com.loosethread.moodsignals.datatypes.SignalCategory
import com.loosethread.moodsignals.dialogs.EditCategoryDialog

class FragmentCategories : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    lateinit var adapter: CategoryAdapter

    private val binding get() = _binding!!
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        adapter = CategoryAdapter(Db.getCategories(), childFragmentManager, viewLifecycleOwner)
        binding.rvCategories.adapter = adapter
        layoutManager = FullWidthLinearLayoutManager(requireContext())
        val dividerItemDecoration = DividerItemDecoration(binding.root.context, LinearLayoutManager.VERTICAL)
        binding.rvCategories.addItemDecoration(dividerItemDecoration)

        binding.rvCategories.layoutManager = layoutManager
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val requestKey = "add_comment"

        binding.btnAdd.setOnClickListener {
            val dialog = EditCategoryDialog()
            dialog.arguments = bundleOf(
                "requestKey" to requestKey
            )
            dialog.show(parentFragmentManager, "EditCategoryFragment")
        }

        setFragmentResultListener(requestKey) { _, bundle ->
            val updated = bundle.getBoolean("isUpdated", false)
            if (updated) {
                val description = bundle.getString("description")
                description?.isNullOrEmpty()?.let {
                    if(!it) {
                        val id = Db.addCategory(description)
                        adapter.addCategory(SignalCategory(id, description))
                    }
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}