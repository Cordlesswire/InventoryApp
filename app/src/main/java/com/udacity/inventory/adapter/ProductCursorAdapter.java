package com.udacity.inventory.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.udacity.inventory.R;
import com.udacity.inventory.data.ProductContract.ProductEntry;


public class ProductCursorAdapter extends CursorAdapter {

    public final static int STORAGE_PERMISSION_CODE = 23;
    private Context mContext;


    public ProductCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_list_cardview, parent, false);
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        mContext = context;
        TextView nameText     = (TextView) view.findViewById(R.id.nameText);
        ImageView imageView   = (ImageView) view.findViewById(R.id.productImage);
        TextView quantityText = (TextView) view.findViewById(R.id.quantityText);
        TextView priceText    = (TextView) view.findViewById(R.id.textview_price);

        final String name = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.PRODUCT_NAME));
        final Integer quantity = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.PRODUCT_QUANTITY));
        final Float price      = cursor.getFloat(cursor.getColumnIndexOrThrow(ProductEntry.PRODUCT_PRICE));
        final String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.PRODUCT_IMAGE));
        nameText.setText(name);
        quantityText.setText(Integer.toString(quantity));
        priceText.setText(Float.toString(price));

        setupImageOfProduct(imageView, imagePath);

        Button sellButton = (Button) view.findViewById(R.id.sellButton);
        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view != null) {
                    ContentValues values = new ContentValues();
                    values.put(ProductEntry.PRODUCT_NAME, name);
                    values.put(ProductEntry.PRODUCT_IMAGE, imagePath);
                    values.put(ProductEntry.PRODUCT_QUANTITY, (  quantity >= 1 ? quantity - 1 : 0)  );
                    values.put(ProductEntry.PRODUCT_PRICE, price);

                    String tag = view.getTag().toString();
                    Uri currentPetUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, Integer.parseInt(tag));

                    int rowsAffected = mContext.getContentResolver().update(currentPetUri, values, null, null);
                    if (rowsAffected == 0 || quantity == 0) {
                        Snackbar.make(view, mContext.getString(R.string.sell_product_failed), Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });
        sellButton.setTag(cursor.getInt(cursor.getColumnIndex(ProductEntry._ID)));
    }


    public boolean setupImageOfProduct(ImageView image, String path) {
        if (path != null) {
            image.setVisibility(View.VISIBLE);

            // First checking if the app is already having the permission
            if (isReadStorageAllowed()) {
                // If permission is already having then showing the toast
                image.setImageURI(Uri.parse(path));
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
        int result = ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE);
        // If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        // If permission is not granted returning false
        return false;
    }


    public void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, Manifest.permission.READ_EXTERNAL_STORAGE)){
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions((Activity) mContext, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }



}
