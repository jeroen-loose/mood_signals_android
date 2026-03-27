import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.widget.DatePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.loosethread.moodsignals.DateManager
import com.loosethread.moodsignals.Db
import com.loosethread.moodsignals.R

class EditCommentFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater;

            val comment = arguments?.getString("comment")

            val dialogView = inflater.inflate(R.layout.dialog_edit_comment, null)

            val etComment = dialogView.findViewById<android.widget.EditText>(R.id.etComment)
            etComment.setText(comment)

            builder.setView(dialogView)
                .setPositiveButton("OK",
                    DialogInterface.OnClickListener { dialog, id ->
                        val resultBundle = Bundle().apply {
                            putBoolean("isUpdated", true)
                            putString("comment", etComment.text.toString())
                        }
                        setFragmentResult("edit_comment_request", resultBundle)
                    })
                .setNegativeButton("Cancel",
                    DialogInterface.OnClickListener { dialog, id ->
                        getDialog()?.cancel()
                    })

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")

    }
}