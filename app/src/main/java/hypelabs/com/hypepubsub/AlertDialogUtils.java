package hypelabs.com.hypepubsub;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

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
     * @param message Message of the dialog.
     */
    public static void showOkDialog(Context context, String title, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.show();
    }

    /**
     * This method creates and displays an alert dialog to receive an input.
     *
     * @param context Context on which the dialog should be displayed.
     * @param title Title of the the dialog.
     * @param hintInput Hint to suggest the type of input expected.
     * @param singleInputDialog Object of a class that implemented the SingleInputDialog interface.
     *                          This object defines what will be executed when the Ok and Cancel
     *                          buttons are pressed.
     */
    public static void showSingleInputDialog(Context context, String title, String hintInput, final SingleInputDialog singleInputDialog)
    {
        final EditText input = new EditText(context);
        input.setHint(hintInput);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(input);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setCancelable(true);
        builder.setView(layout);
        builder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        try {
                            singleInputDialog.onOk(input.getText().toString());
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                    }
                });

        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        singleInputDialog.onCancel();
                    }
                });

        AlertDialog dialog = builder.create();
        Window dialogWindow = dialog.getWindow();

        if(dialogWindow != null)
        {
            dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        dialog.show();
    }

    /**
     * Interface to be implemented and passed as a parameter when using a SingleInputDialog. It
     * acts like a callback when the Ok or the Cancel button are pressed.
     */
    public interface SingleInputDialog
    {
        /**
         *
         * Method called when the Ok button of a SingleInputDialog is pressed
         *
         * @param str Value of the dialog input
         * @throws IOException
         * @throws NoSuchAlgorithmException
         */
        void onOk(String str) throws IOException, NoSuchAlgorithmException;

        /**
         * Method called when the Cancel button of a SingleInputDialog is pressed
         */
        void onCancel();
    }

    /**
     *
     * This method creates and displays an alert dialog to receive 2 inputs.
     *
     * @param context Context on which the dialog should be displayed.
     * @param title Title of the the dialog.
     * @param hintInput1 Hint to suggest the type of 1st input expected.
     * @param hintInput2 Hint to suggest the type of 2nd input expected.
     * @param doubleInputDialog Object of a class that implemented the DoubleInputDialog interface.
     *                          This object defines what will be executed when the Ok and Cancel
     *                          buttons are pressed.
     */
    public static void showDoubleInputDialog(Context context, String title, String hintInput1, String hintInput2, final DoubleInputDialog doubleInputDialog)
    {
        final EditText input1 = new EditText(context);
        input1.setHint(hintInput1);
        final EditText input2 = new EditText(context);
        input2.setHint(hintInput2);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(input1);
        layout.addView(input2);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setCancelable(true);
        builder.setView(layout);
        builder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        try {
                            doubleInputDialog.onOk(input1.getText().toString(),
                                                 input2.getText().toString());
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                    }
                });

        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doubleInputDialog.onCancel();
                    }
                });

        AlertDialog dialog = builder.create();
        Window dialogWindow = dialog.getWindow();

        if(dialogWindow != null)
        {
            dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        dialog.show();
    }

    /**
     * Interface to be implemented and passed as a parameter when using a DoubleInputDialog. It
     * acts like a callback when the Ok or the Cancel button are pressed.
     */
    public interface DoubleInputDialog
    {
        /**
         *
         * Method called when the Ok button of a DoubleInputDialog is pressed
         *
         * @param str1 Value of the 1st dialog input
         * @param str2 Value of the 2nd dialog input
         * @throws IOException
         * @throws NoSuchAlgorithmException
         */
        void onOk(String str1, String str2) throws IOException, NoSuchAlgorithmException;

        /**
         * Method called when the Cancel button of a DoubleInputDialog is pressed
         */
        void onCancel();
    }

    /**
     *
     * This method creates and displays an alert dialog to display a list view
     *
     * @param context Context on which the dialog should be displayed.
     * @param title Title of the the dialog.
     * @param adapter Adapter to be given to the list view
     * @param listViewInputDialog Object of a class that implemented the ListViewInputDialog interface.
     *                            This object defines what will be executed when an item of the list is clicked.
     */
    public static void showListViewInputDialog(Context context, String title, ListAdapter adapter, final ListViewInputDialog listViewInputDialog)
    {
        final ListView listView = new ListView(context);
        listView.setAdapter(adapter);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(listView);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setCancelable(true);
        builder.setView(layout);
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }
                });

        final Dialog dialog = builder.create();
        dialog.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object listItem = listView.getItemAtPosition(position);
                try {
                    listViewInputDialog.onItemClick(listItem, dialog);

                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Interface to be implemented and passed as a parameter when using a ListViewInputDialog. It
     * acts like a callback when an item of the list is clicked.
     */
    public interface ListViewInputDialog
    {
        /**
         * Method called when a list item is pressed
         *
         * @param listItem Object associated to the selected list item.
         * @param dialog Dialog in which the list view is being displayed.
         * @throws IOException
         * @throws NoSuchAlgorithmException
         */
        void onItemClick(Object listItem, Dialog dialog) throws IOException, NoSuchAlgorithmException;
    }
}
