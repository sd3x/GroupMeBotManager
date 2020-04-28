package hash.cache.botutils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import java.util.Objects;

public class DeleteConfirmationFragment extends DialogFragment {

    Bot bot;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Really Delete Bot?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        BotSettingsActivity activity = (BotSettingsActivity) getActivity();
                        Objects.requireNonNull(activity).deleteBot();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { /* Do Nothing */ }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
