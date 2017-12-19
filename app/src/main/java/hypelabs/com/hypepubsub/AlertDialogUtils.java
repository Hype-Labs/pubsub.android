package hypelabs.com.hypepubsub;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * This class has some utility methods to create several types of AlertDialogs.
 */
public class AlertDialogUtils
{
    /**
     * This method creates and displays an alert dialog with a given message and an Ok button.
     *
     * @param context Context on which the dialog should be displayed.
     * @param title Title of the the dialog.
     * @param info Message of the dialog.
     */
    public static void showInfoDialog(Context context, String title, String info)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(info);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.show();
    }

    /**
     * This method creates and displays an alert dialog to receive an input.
     *
     * @param context Context on which the dialog should be displayed.
     * @param title Title of the the dialog.
     * @param hintInput Hint to suggest the type of input expected.
     * @param inputDialog Object of a class that implemented the ISingleInputDialog interface.
     *                          This object defines what will be executed when the Ok and Cancel
     *                          buttons are pressed.
     */
    public static void showSingleInputDialog(Context context, String title, String msg,
                                             String hintInput, final ISingleInputDialog inputDialog) {
        final EditText textInput = new EditText(context);
        textInput.setHint(hintInput);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(textInput);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(true);
        builder.setView(layout);
        builder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        inputDialog.onOk(textInput.getText().toString());
                    }
                });

        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        inputDialog.onCancel();
                    }
                });

        AlertDialog dialog = builder.create();
        Window dialogWindow = dialog.getWindow();

        if(dialogWindow != null) {
            dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        dialog.show();
    }

    /**
     * Interface to be implemented and passed as a parameter when using a ISingleInputDialog. It
     * acts like a callback when the Ok or the Cancel button are pressed.
     */
    public interface ISingleInputDialog {

        /**
         *
         * Method called when the Ok button of a ISingleInputDialog is pressed
         *
         * @param str Value of the dialog input
         */
        void onOk(String str);

        /**
         * Method called when the Cancel button of a ISingleInputDialog is pressed
         */
        void onCancel();
    }
}
