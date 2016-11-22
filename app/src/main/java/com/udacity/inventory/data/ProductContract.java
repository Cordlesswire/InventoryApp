package com.udacity.inventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


public class ProductContract {

    public static final String CONTENT_AUTHORITY = "com.udacity.inventory";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PRODUCTS = "products";


    private ProductContract() {
    }


    public static final class ProductEntry implements BaseColumns
    {
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        public final static String TABLE_NAME       = "products";
        public final static String _ID              = BaseColumns._ID;
        public final static String PRODUCT_NAME     = "name";
        public final static String PRODUCT_QUANTITY = "quantity";
        public final static String PRODUCT_PRICE    = "price";
        public final static String PRODUCT_IMAGE    = "image";
        public final static String[] COLUMNS =
                {_ID, PRODUCT_NAME, PRODUCT_QUANTITY, PRODUCT_PRICE, PRODUCT_IMAGE};

        final static String SQL_CREATE_PRODUCTS_TABLE =
            "CREATE TABLE " + ProductEntry.TABLE_NAME + " ("
                + ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductEntry.PRODUCT_NAME + " TEXT NOT NULL, "
                + ProductEntry.PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + ProductEntry.PRODUCT_PRICE + " FLOAT NOT NULL DEFAULT 0.00, "
                + ProductEntry.PRODUCT_IMAGE + " TEXT"
            + ");";
        final static String  SQL_UPGRADE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }


}
