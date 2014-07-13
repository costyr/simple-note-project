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
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.content.Intent;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import simplenote.db.Note;
import simplenote.db.NotesDb;

public class MainActivity extends Activity
                          implements AbsListView.OnScrollListener {

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

        mList = (ListView)findViewById(R.id.listView);
        View header = getLayoutInflater().inflate(R.layout.header, mList, false);
        mPlaceholderView = header.findViewById(R.id.placeholder);
        mQuickReturnView = findViewById(R.id.sticky);

        mList.addHeaderView(header);

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

        mList.setAdapter(mStringAdapter);

        mList.setOnItemClickListener(new OnItemClickListener() {

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


        mList.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mQuickReturnHeight = mQuickReturnView.getHeight();
                        computeScrollY();
                    }
                });

        mList.setOnScrollListener(this);
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
                note.setLastModified(System.currentTimeMillis());

                mStringAdapter.notifyDataSetChanged();

                mNotesDb.UpdateNote(note);
              }
            }
        }
    }

    @Override
    public void onScroll (AbsListView view, int firstVisibleItem, int visibleItemCount,
                          int totalItemCount)
    {
        int scrollY = Math.min(mCalculatedHeight - mList.getHeight(), getScrollY());

        int placeholderTop = mPlaceholderView.getTop();

        int rawY = placeholderTop - scrollY;
        int translationY = 0;

        String message = String.format("placeholderTop: %d, scrollY: %d, rawY: %d", placeholderTop, scrollY, rawY);

        Log.v("QuickReturnFragment ", message);

        switch (mState) {
            case STATE_OFFSCREEN:
                if (rawY <= mMinRawY) {
                    mMinRawY = rawY;
                } else {
                    mState = STATE_RETURNING;
                }
                translationY = rawY;
                break;

            case STATE_ONSCREEN:
                if (rawY < -mQuickReturnHeight) {
                    mState = STATE_OFFSCREEN;
                    mMinRawY = rawY;
                }
                translationY = rawY;
                break;

            case STATE_RETURNING:
                translationY = (rawY - mMinRawY) - mQuickReturnHeight;

                message = String.format("Returning: translationY: %d, quickReturnHeight: %d, minRawY: %d", translationY, mQuickReturnHeight, mMinRawY);

                Log.v("QuickReturnFragment ", message);
                if (translationY > 0) {
                    translationY = 0;
                    mMinRawY = rawY - mQuickReturnHeight;
                }

                if (rawY > 0) {
                    mState = STATE_ONSCREEN;
                    translationY = rawY;
                }

                if (translationY < -mQuickReturnHeight) {
                    mState = STATE_OFFSCREEN;
                    mMinRawY = rawY;
                }
                break;
        }
        mQuickReturnView.setTranslationY(translationY);
    }

    @Override
    public void onScrollStateChanged (AbsListView view, int scrollState)
    {

    }

    private void computeScrollY() {
        mCalculatedHeight = 0;
        int itemCount = mList.getAdapter().getCount();
        if (mItemOffsetY == null) {
            mItemOffsetY = new int[itemCount];
        }
        for (int i = 0; i < itemCount; ++i) {
            View view = mList.getAdapter().getView(i, null, mList);
            view.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            mItemOffsetY[i] = mCalculatedHeight;
            mCalculatedHeight += view.getMeasuredHeight();
        }
        mIsScrollComputed = true;
    }

    private int getScrollY() {
        if (!mIsScrollComputed) {
            return 0;
        }
        int pos = mList.getFirstVisiblePosition();
        View view = mList.getChildAt(0);
        int nItemY = view.getTop();
        return (mItemOffsetY[pos] - nItemY);
    }

    private ArrayAdapter<Note> mStringAdapter;

    private Intent mEditMessageIntent;

    private NotesDb mNotesDb;

    private Integer mNextId;

    private ListView mList;

    private static final int STATE_ONSCREEN = 0;
    private static final int STATE_OFFSCREEN = 1;
    private static final int STATE_RETURNING = 2;

    private int mMinRawY = 0;
    private int mState = STATE_ONSCREEN;

    private View mPlaceholderView;
    private View mQuickReturnView;
    private int mQuickReturnHeight;
    private boolean mIsScrollComputed;
    private int mCalculatedHeight;
    private int mItemOffsetY[];
}
