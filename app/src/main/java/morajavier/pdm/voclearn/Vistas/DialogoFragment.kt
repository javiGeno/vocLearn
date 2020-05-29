package morajavier.pdm.voclearn.Vistas

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import morajavier.pdm.voclearn.R

class DialogoFragment: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage("Hola")
                .setPositiveButton(R.string.aceptar,
                    DialogInterface.OnClickListener { dialog, id ->
                        // FIRE ZE MISSILES!
                    })
                .setNegativeButton(R.string.cancelar,
                    DialogInterface.OnClickListener { dialog, id ->
                        // User cancelled the dialog
                    })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}