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

    public final static String NOTE_TITLE = "simplenote.activity.NOTE_TITLE";
    public final static String NOTE_TEXT = "simplenote.activity.NOTE_TEXT";
    public final static String NOTE_LAST_MODIFIED = "simplenote.activity.NOTE_LAST_MODIFIED";
    public final static String NOTE_ID = "simplenote.activity.NOTE_ID";
    public final static String NOTE_INDEX = "simplenote.activity.NOTE_INDEX";
    public final static String NOTE_OP = "simplenote.activity.NOTE_OP";
    public final static int EDIT_NOTE_REQUEST = 1;
    public final static int NOTE_OP_EDIT = 2;
    public final static int NOTE_OP_DELETE = 3;

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
                    String title = notes.getString(1);
                    String note = notes.getString(2);
                    Long lastModified = notes.getLong(3);
                    if (note != null) {
                        if (id > mNextId)
                            mNextId = id;
                        mStringAdapter.add(new Note(id, title, note, lastModified));
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

                mEditMessageIntent.putExtra(NOTE_TEXT, note.toString());
                mEditMessageIntent.putExtra(NOTE_ID, note.getId());
                mEditMessageIntent.putExtra(NOTE_INDEX, position);
                mEditMessageIntent.putExtra(NOTE_TITLE, note.getTitle());
                mEditMessageIntent.putExtra(NOTE_LAST_MODIFIED, note.getLastModified());
                startActivityForResult(mEditMessageIntent, EDIT_NOTE_REQUEST);

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
        Note newNote = new Note(mNextId, "", message, System.currentTimeMillis());
        mStringAdapter.add(newNote);

        mNotesDb.AddNote(newNote);

        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data)
    {
        if (requestCode == EDIT_NOTE_REQUEST)
        {
            if (resultCode == Activity.RESULT_OK)
            {
              Integer id = data.getIntExtra(MainActivity.NOTE_ID, 0);
              Integer index = data.getIntExtra(MainActivity.NOTE_INDEX, 0);

              Integer op = data.getIntExtra(MainActivity.NOTE_OP, 0);
              if (op == MainActivity.NOTE_OP_DELETE)
              {
                Note note = mStringAdapter.getItem(index);
                mStringAdapter.remove(note);
                mNotesDb.DeleteNote(id);

                // Show Alert
                Toast.makeText(getApplicationContext(),
                  getResources().getString(R.string.note_deleted_alert), Toast.LENGTH_LONG).show();
              }
              else if (op == MainActivity.NOTE_OP_EDIT)
              {
                Note note = mStringAdapter.getItem(index);
                String newNoteTitle = data.getStringExtra(MainActivity.NOTE_TITLE);
                String newNoteText = data.getStringExtra(MainActivity.NOTE_TEXT);

                note.setTitle(newNoteTitle);
                note.setNote(newNoteText);

                mStringAdapter.notifyDataSetChanged();

                mNotesDb.UpdateNote(id, newNoteTitle, newNoteText);
              }
            }
        }
    }

    private ArrayAdapter<Note> mStringAdapter;

    private Intent mEditMessageIntent;

    private NotesDb mNotesDb;

    private Integer mNextId;
}
