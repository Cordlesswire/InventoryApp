package com.udacity.inventory.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.udacity.inventory.R;
import com.udacity.inventory.data.ProductContract.ProductEntry;


public class AddDialogFragment extends DialogFragment {

    public static final int REQUEST_CODE = 0;
    String mImageURI;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View addView = inflater.inflate(R.layout.dialog_add_product, null);

        ImageButton selectImage = (ImageButton) addView.findViewById(R.id.imageProduct);
        selectImageOnClick(selectImage);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final Dialog dialog = builder.setView(addView)
                .setPositiveButton(R.string.new_product, null)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getDialog().cancel();
                    }
                })
                .create();
        addProductOnShowClick(addView, dialog);
        return dialog;
    }


    private void addProductOnShowClick(final View addView, Dialog dialog) {
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialogInterface) {
                Button positiveButton = ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Boolean wantToCloseDialog = false;

                        EditText editTextName     = (EditText) addView.findViewById(R.id.nameText);
                        EditText editTextQuantity = (EditText) addView.findViewById(R.id.quantity);
                        EditText editTextPrice    = (EditText) addView.findViewById(R.id.price);

                        String nameString     = editTextName.getText().toString().trim();
                        String quantityString = editTextQuantity.getText().toString().trim();
                        String priceString    = editTextPrice.getText().toString().trim();

                        if (TextUtils.isEmpty(nameString) || TextUtils.isEmpty(quantityString) || TextUtils.isEmpty(priceString)) {
                            Snackbar.make(view, getString(R.string.product_info_not_empty), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        }
                        else
                        {
                            Integer quantity = Integer.parseInt(quantityString);
                            Float price      = Float.parseFloat(priceString);
                            insertProduct(nameString, quantity, price, mImageURI);
                            wantToCloseDialog = true;
                        }

                        if (wantToCloseDialog)
                            dialogInterface.dismiss();
                    }
                });
            }
        });
    }


    private void selectImageOnClick(ImageButton button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }


    private void insertProduct(String name, Integer quantity, Float price, String imagePath) {
        ContentValues values = new ContentValues();
        values.put(ProductEntry.PRODUCT_NAME, name);
        values.put(ProductEntry.PRODUCT_QUANTITY, quantity);
        values.put(ProductEntry.PRODUCT_PRICE, price);
        if (!"".equals(imagePath)) {
            values.put(ProductEntry.PRODUCT_IMAGE, imagePath);
        }
        getActivity().getContentResolver().insert(ProductEntry.CONTENT_URI, values);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            mImageURI = selectedImage.toString();
        }
    }


}
