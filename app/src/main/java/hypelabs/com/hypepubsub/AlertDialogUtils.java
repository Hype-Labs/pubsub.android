package hypelabs.com.hypepubsub;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class AlertDialogUtils
{
    public static void showOkDialog(Context context, String title, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.show();
    }

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
                            singleInputDialog.actionOk(input.getText().toString());
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
                        singleInputDialog.actionCancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    public interface SingleInputDialog
    {
        void actionOk(String str) throws IOException, NoSuchAlgorithmException;
        void actionCancel();
    }

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
                            doubleInputDialog.actionOk(input1.getText().toString(),
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
                        doubleInputDialog.actionCancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    public interface DoubleInputDialog
    {
        void actionOk(String str1, String str2) throws IOException, NoSuchAlgorithmException;
        void actionCancel();
    }

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

    public interface ListViewInputDialog
    {
        void onItemClick(Object listItem, Dialog dialog) throws IOException, NoSuchAlgorithmException;
    }

    public static void showListViewDialog(Context context, String title, ListAdapter adapter)
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
                    public void onClick(DialogInterface dialog, int which)
                    {
                    }
                });

        final Dialog dialog = builder.create();
        dialog.show();
    }
}
