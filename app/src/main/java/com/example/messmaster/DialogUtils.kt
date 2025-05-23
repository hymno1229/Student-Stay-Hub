// Name this file something like DialogUtils.kt

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.messmaster.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun showAlertDialog(
    context: Context,
    title: String,
    message: String,
    positiveButtonText: String = "OK",
    onPositiveButtonClick: () -> Unit = {}
) {
    val alertDialogBuilder = AlertDialog.Builder(context)

    // Create a linear layout to hold the title, message, and button
    val layout = LinearLayout(context)
    layout.orientation = LinearLayout.VERTICAL
    layout.gravity = Gravity.CENTER
    layout.layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )

    layout.setPadding(20.dpToPx(), 20.dpToPx(), 20.dpToPx(), 20.dpToPx()) // Add padding to all sides

    // Create and customize the title TextView
    val titleView = TextView(context)
    titleView.layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    ).apply {
        bottomMargin = 10.dpToPx() // Add margin to the bottom of the title
    }
    titleView.text = title
    titleView.setTextColor(Color.BLACK)
    titleView.textSize = 20f
    titleView.gravity = Gravity.CENTER
    titleView.setTypeface(null, Typeface.BOLD) // Make the title bold
    layout.addView(titleView)

    // Create and customize the message TextView
    val messageView = TextView(context)
    messageView.layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )
    messageView.text = message
    messageView.setTextColor(Color.BLACK)
    messageView.gravity = Gravity.CENTER
    layout.addView(messageView)

    // Set the custom layout to the AlertDialog
    alertDialogBuilder.setView(layout)

    // Set the positive button with a listener
    alertDialogBuilder.setPositiveButton(positiveButtonText) { dialog, which ->
        onPositiveButtonClick()
        dialog.dismiss()
    }

    // Create and show the AlertDialog
    val alertDialog = alertDialogBuilder.create()
    alertDialog.setOnShowListener {
        val positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        val positiveButtonLayout = positiveButton.layoutParams as LinearLayout.LayoutParams
        positiveButtonLayout.width = ViewGroup.LayoutParams.MATCH_PARENT
        positiveButtonLayout.gravity = Gravity.CENTER
        positiveButton.layoutParams = positiveButtonLayout
        positiveButton.setTextColor(Color.WHITE)
        positiveButton.backgroundTintList = ContextCompat.getColorStateList(context, R.color.primaryColor)
    }
    alertDialog.window?.setBackgroundDrawableResource(R.drawable.popupbg) // Set rounded dialog background
    alertDialog.show()
}

// Extension function to convert dp to pixels
private fun Int.dpToPx(): Int {
    return (this * Resources.getSystem().displayMetrics.density).toInt()
}

fun showConfirmationDialog(context: Context, title: String, message: String, positiveText: String, negativeText: String, positiveAction: () -> Unit, negativeAction: () -> Unit) {
    val alertDialogBuilder = MaterialAlertDialogBuilder(
        context,
        R.style.CustomAlertDialogTheme
    ) // Use custom theme here
    alertDialogBuilder.apply {
        setTitle(title)
        setMessage(message)
        setPositiveButton(positiveText) { dialog, _ ->
            // User clicked Yes, perform the action here
            // For example,
            positiveAction()
            // Perform any other action here
            dialog.dismiss() // Dismiss the dialog
        }
        setNegativeButton(negativeText) { dialog, _ ->
            // User clicked No, do nothing
            negativeAction()
            dialog.dismiss() // Dismiss the dialog
        }
    }

    // Access the buttons and set their text color and background tint
    val alertDialog = alertDialogBuilder.create()
    alertDialog.setOnShowListener {
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.apply {
            setTextColor(Color.WHITE)
            backgroundTintList =
                ColorStateList.valueOf(context.resources.getColor(R.color.primaryColor)) // Set your desired color

            // Set margin only to the left side
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(40, 0, 0, 0) // Adjust left margin in pixels as needed
            this.layoutParams = layoutParams
        }
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.apply {
            setTextColor(Color.WHITE)
            backgroundTintList =
                ColorStateList.valueOf(context.resources.getColor(R.color.primaryColor)) // Set your desired color
        }
    }
    alertDialog.show()
}
