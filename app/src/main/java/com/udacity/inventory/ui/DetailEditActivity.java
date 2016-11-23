package com.udacity.inventory.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.udacity.inventory.R;
import com.udacity.inventory.data.ProductContract.ProductEntry;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static com.udacity.inventory.adapter.ProductCursorAdapter.STORAGE_PERMISSION_CODE;


public class DetailEditActivity extends AppCompatActivity
                                implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = DetailEditActivity.class.getSimpleName();
    //
    private static final int EXISTING_PRODUCT_LOADER = 0;
    public static final int REQUEST_CODE             = 0;
    public static final String EXTRA_ROW_SAVE   = "onSaveOnClick";
    public static final String EXTRA_ROW_DELETE = "deleteProduct";

    private Uri mCurrentProductUri;
    private Uri mImageURI;

    private EditText mNameEdit;
    private TextView mQuantityText;
    private EditText mPriceEdit;
    private ImageView mImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_edit);

        mCurrentProductUri = getIntent().getData();

        getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);

        mNameEdit     = (EditText) findViewById(R.id.editName);
        mQuantityText = (TextView) findViewById(R.id.textQuantity);
        mPriceEdit    = (EditText) findViewById(R.id.editPrice);

        mImageView = (ImageView) findViewById(R.id.imageHead);
        choosePhotoOnClick(mImageView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.orderFab);
        ordemFromSupplierByEmailOnClick(fab, mNameEdit);
    }


    private void choosePhotoOnClick(ImageView image) {
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }


    private void ordemFromSupplierByEmailOnClick(FloatingActionButton fab, final EditText edit) {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edit.getText().toString().trim();
                String message = createOrderSummary(name);
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject) + " " + name);
                intent.putExtra(Intent.EXTRA_TEXT, message);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, mCurrentProductUri, ProductEntry.COLUMNS, null, null, null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int nameColumnIndex     = cursor.getColumnIndex(ProductEntry.PRODUCT_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.PRODUCT_QUANTITY);
            int priceColumnIndex    = cursor.getColumnIndex(ProductEntry.PRODUCT_PRICE);
            int imageColumnIndex    = cursor.getColumnIndex(ProductEntry.PRODUCT_IMAGE);

            String name      = cursor.getString(nameColumnIndex);
            Integer quantity = cursor.getInt(quantityColumnIndex);
            Float price      = cursor.getFloat(priceColumnIndex);
            String imagePath = cursor.getString(imageColumnIndex);

            mNameEdit.setText(name);
            mQuantityText.setText(Integer.toString(quantity));
            mPriceEdit.setText(Float.toString(price));

            setupImageOfProduct(mImageView, imagePath);
        }
    }


    public boolean setupImageOfProduct(ImageView image, String imagePath) {
        if (imagePath != null) {
            image.setVisibility(View.VISIBLE);

            // First checking if the app is already having the permission
            if (isReadStorageAllowed()) {
                // If permission is already having then showing the toast
                image.setImageURI(Uri.parse(imagePath));
                // Existing the method with return
                return true;
            }
            // If the app has not the permission then asking for the permission
            requestStoragePermission();
        }
        else {
            image.setVisibility(View.GONE);
        }
        return false;
    }


    public boolean isReadStorageAllowed() {
        // Getting the permission status
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        // If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        // If permission is not granted returning false
        return false;
    }


    public void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case STORAGE_PERMISSION_CODE: {
                Log.i(TAG, "If request is cancelled, the result arrays are empty.");
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "permission was granted, do your work....");
                } else {
                    Log.i(TAG, "Permission Denied: Disable the functionality that depends on this permission.");
                }
            }
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEdit.setText("");
        mQuantityText.setText(Integer.toString(0));
        mPriceEdit.setText(Float.toString(0));
        mImageView.setImageDrawable(null);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            if (selectedImage != null) {
                mImageURI = Uri.parse(selectedImage.toString());
                mImageView.setImageURI(selectedImage);
            }
        }
    }


    public String createOrderSummary(String name) {
        return getString(R.string.hi) + "\n" +
               getString(R.string.message) + "\n" +
               getString(R.string.product_name_title) + " " + name + "\n" +
               getString(R.string.thank);
    }


    public void onSaveOnClick(View view) {
        Intent intent = new Intent();
        int row = saveProduct();
        intent.putExtra(EXTRA_ROW_SAVE, row);
        setResult(RESULT_OK, intent);
        finish();
    }


    public int saveProduct() {
        String name = mNameEdit.getText().toString().trim();
        Integer quantity = Integer.parseInt(mQuantityText.getText().toString().trim());
        Float price = 0.0f;
        if (!"".equals(mPriceEdit.getText().toString().trim())) {
            price = Float.parseFloat(mPriceEdit.getText().toString().trim());
        }
        ContentValues values = new ContentValues();
        values.put(ProductEntry.PRODUCT_NAME, name);

        Bitmap icLanucher = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        mImageView = (ImageView) findViewById(R.id.imageHead);
        Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
        if (!isEquals(icLanucher, bitmap) && mImageURI != null) {
            values.put(ProductEntry.PRODUCT_IMAGE, mImageURI.toString());
        }
        values.put(ProductEntry.PRODUCT_QUANTITY, quantity);
        values.put(ProductEntry.PRODUCT_PRICE, price);

        return getContentResolver().update(mCurrentProductUri, values, null, null);
    }


    public void onDeleteOnClick(View view) {
        showDeleteConfirmationDialog();
    }


    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_dialog_title));
        builder.setPositiveButton(getString(R.string.btn_delete), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent();
                int rowDelete = deleteProduct();
                intent.putExtra(EXTRA_ROW_DELETE, rowDelete);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private int deleteProduct() {
        return getContentResolver().delete(mCurrentProductUri, null, null);
    }


    public boolean isEquals(Bitmap bitmapOne, Bitmap bitmapTwo) {
        ByteBuffer bufferOne = ByteBuffer.allocate(bitmapOne.getHeight() * bitmapOne.getRowBytes());
        bitmapOne.copyPixelsToBuffer(bufferOne);
        ByteBuffer bufferTwo = ByteBuffer.allocate(bitmapTwo.getHeight() * bitmapTwo.getRowBytes());
        bitmapTwo.copyPixelsToBuffer(bufferTwo);
        return Arrays.equals(bufferOne.array(), bufferTwo.array());
    }



}
