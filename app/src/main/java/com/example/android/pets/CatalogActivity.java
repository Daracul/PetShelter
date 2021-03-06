/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.pets.data.PetContract.PetEntry;
import com.example.android.pets.data.PetDbHelper;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = CatalogActivity.class.getSimpleName();

    private static final int PET_LOADER = 1;

    private ListView petListView;
    private PetCursorAdapter petCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        Log.d("myLogs", PetDbHelper.SQL_CREATE_ENTRIES);
        petListView = (ListView)findViewById(R.id.list_view_pet);
        View emptyView = findViewById(R.id.empty_view);
        petListView.setEmptyView(emptyView);
        petCursorAdapter = new PetCursorAdapter(this, null);
        petListView.setAdapter(petCursorAdapter);
        petListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long Id) {
                Uri currentPetUri = Uri.withAppendedPath(PetEntry.CONTENT_URI, String.valueOf(Id));
                Log.d(LOG_TAG, "TEST: "+ currentPetUri.toString());
                Intent petEditIntent = new Intent(CatalogActivity.this, EditorActivity.class);
                petEditIntent.setData(currentPetUri);
                startActivity(petEditIntent);
            }
        });
        getLoaderManager().initLoader(PET_LOADER, null, this);

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void insertData() {
        // Gets the data repository in write mode
//        SQLiteDatabase db = petDbHelper.getWritableDatabase();

// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME, "Toto");
        values.put(PetEntry.COLUMN_PET_BREED, "Terrier");
        values.put(PetEntry.COLUMN_PET_GENDER, PetEntry.GENDER_MALE);
        values.put(PetEntry.COLUMN_PET_WEIGHT, 7);

// Insert the new row, returning the primary key value of the new row
//        long newRowId = db.insert(PetEntry.TABLE_NAME, null, values);
        getContentResolver().insert(PetEntry.CONTENT_URI, values);


    }

    private void deleteData() {
        getContentResolver().delete(PetEntry.CONTENT_URI, null, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertData();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteData();
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }





    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projectiction = new String[]{PetEntry._ID, PetEntry.COLUMN_PET_NAME, PetEntry.COLUMN_PET_BREED};

        return new CursorLoader(this, PetEntry.CONTENT_URI, projectiction, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        petCursorAdapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        petCursorAdapter.swapCursor(null);

    }
}
