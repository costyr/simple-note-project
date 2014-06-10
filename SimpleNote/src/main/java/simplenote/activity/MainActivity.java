/**
 * (C) Copyright 2014 Costi Rada.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU General Public License
 * (GPL) version 3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/copyleft/gpl.html
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * Contributors:
 */

package simplenote.activity;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.content.Intent;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import simplenote.db.Note;
import simplenote.db.NotesDb;

public class MainActivity extends Activity {

    public final static String NOTE_TEXT = "simplenote.activity.NOTE_TEXT";
    public final static String NOTE_ID = "simplenote.activity.NOTE_ID";
    public final static String NOTE_INDEX = "simplenote.activity.NOTE_INDEX";
    public final static int NOTE_OPERATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditMessageIntent = new Intent(this, EditNoteActivity.class);

        ListView myList = (ListView)findViewById(R.id.listView);

        mStringAdapter = new ArrayAdapter<Note>(this, android.R.layout.simple_list_item_1, android.R.id.text1);

        mNotesDb = new NotesDb(this);

        Cursor notes = mNotesDb.GetNotes();

        mNextId = 0;
        if (notes != null) {
            if (notes.moveToFirst()) {
                do {
                    Integer id = notes.getInt(0);
                    String item = notes.getString(1);
                    if (item != null) {
                        if (id > mNextId)
                            mNextId = id;
                        mStringAdapter.add(new Note(id, item));
                    }
                } while (notes.moveToNext());
            }
        }

        myList.setAdapter(mStringAdapter);

        myList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                //int itemPosition = position;

                // ListView Clicked item value
                //String itemValue = ((TextView)view).getText().toString();

                Note note = mStringAdapter.getItem(position);

                // Show Alert
                Toast.makeText(getApplicationContext(),
                        "Position :" + position + "  ListItem : " + note.toString(), Toast.LENGTH_LONG)
                        .show();

                mEditMessageIntent.putExtra(NOTE_TEXT, note.toString());
                mEditMessageIntent.putExtra(NOTE_ID, note.getId());
                mEditMessageIntent.putExtra(NOTE_INDEX, position);
                startActivityForResult(mEditMessageIntent, NOTE_OPERATION);

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void addToList(View view) {
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();

        editText.setText("");

        mNextId++;
        mStringAdapter.add(new Note(mNextId, message));

        mNotesDb.AddNote(mNextId, "note_" + mNextId, message);

        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data)
    {
        if (requestCode == NOTE_OPERATION)
        {
            if (resultCode == Activity.RESULT_OK)
            {
              Integer id = data.getIntExtra(MainActivity.NOTE_ID, 0);
              Integer index = data.getIntExtra(MainActivity.NOTE_INDEX, 0);

              Note note = mStringAdapter.getItem(index);
              mStringAdapter.remove(note);
              mNotesDb.DeleteNote(id);
            }
        }
    }

    private ArrayAdapter<Note> mStringAdapter;

    private Intent mEditMessageIntent;

    private NotesDb mNotesDb;

    private Integer mNextId;
}
