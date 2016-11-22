package com.udacity.inventory;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.udacity.inventory.adapter.ProductCursorAdapter;
import com.udacity.inventory.data.ProductContract.ProductEntry;
import com.udacity.inventory.ui.AddDialogFragment;
import com.udacity.inventory.ui.DetailEditActivity;

import static com.udacity.inventory.adapter.ProductCursorAdapter.STORAGE_PERMISSION_CODE;
import static com.udacity.inventory.ui.DetailEditActivity.EXTRA_ROW_DELETE;
import static com.udacity.inventory.ui.DetailEditActivity.EXTRA_ROW_SAVE;


public class MainActivity extends AppCompatActivity
                          implements LoaderManager.LoaderCallbacks<Cursor>  {

    private final static String TAG = MainActivity.class.getSimpleName();

    private ProductCursorAdapter mCursorAdapter;
    private ListView mListView;

    private final static int PRODUCT_LOADER = 0;
    private static final int REQUEST_ROW_SAVE = 3;
    private static final int REQUEST_ROW_DELETE = 4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddProductDialog();
            }
        });

        mListView = (ListView) findViewById(R.id.list_view_product);

        View emptyView = findViewById(R.id.empty_view);
        mListView.setEmptyView(emptyView);

        mCursorAdapter = new ProductCursorAdapter(this, null);
        startDetailEditActivityOnClick(mListView, mCursorAdapter);

        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }


    private void startDetailEditActivityOnClick(ListView productListView, ProductCursorAdapter adapter) {
        productListView.setAdapter(adapter);
        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailEditActivity.class);
                Uri currentPetUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);
                intent.setData(currentPetUri);
                startActivityForResult(intent, REQUEST_ROW_SAVE);
            }
        });
    }


    private void setupToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
    }


    private void showAddProductDialog() {
        AddDialogFragment newFragment = new AddDialogFragment();
        newFragment.show(getFragmentManager(), getString(R.string.new_product));
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, ProductEntry.CONTENT_URI, ProductEntry.COLUMNS, null, null, null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Log.i(TAG, "requestCode: " + requestCode);
            if (requestCode == REQUEST_ROW_SAVE) {
                int rowSaveByDetail = data.getIntExtra(EXTRA_ROW_SAVE, 0);
                if (rowSaveByDetail == 0) {
                    Snackbar.make(mListView, getString(R.string.update_product_failed), Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(mListView, getString(R.string.update_product_successful), Snackbar.LENGTH_SHORT).show();
                }
            }
            else
            if (requestCode == REQUEST_ROW_DELETE) {
                int rowDeleteByDetail = data.getIntExtra(EXTRA_ROW_DELETE, 0);
                if (rowDeleteByDetail == 0) {
                    Snackbar.make(mListView, getString(R.string.delete_product_failed), Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(mListView, getString(R.string.delete_product_successful), Snackbar.LENGTH_SHORT).show();
                }
            }
        }
    }



}
