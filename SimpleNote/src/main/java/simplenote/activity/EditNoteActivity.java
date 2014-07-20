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
import android.os.Bundle;
import android.app.Activity;
import android.text.format.DateUtils;
import android.view.Menu;
import android.content.Intent;
import android.annotation.SuppressLint;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class EditNoteActivity extends Activity {

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        // Get the message from the intent
        Intent intent = getIntent();
        mNoteContent = intent.getStringExtra(MainActivity.NOTE_TEXT);
        mNoteTitle = intent.getStringExtra(MainActivity.NOTE_TITLE);

        mId = intent.getIntExtra(MainActivity.NOTE_ID, 0);
        mIndex = intent.getIntExtra(MainActivity.NOTE_INDEX, 0);

        EditText editNoteTitle = (EditText)findViewById(R.id.editNoteTitle);

        editNoteTitle.setText(mNoteTitle);

        EditText editNote = (EditText)findViewById(R.id.editNote);

        editNote.setText(mNoteContent);

        Long lastModified = intent.getLongExtra(MainActivity.NOTE_LAST_MODIFIED, 0);

        TextView lastModifiedView = (TextView)findViewById(R.id.lastModified);

        String lastModifiedText = getResources().getString(R.string.last_modified_label);
        if (DateUtils.isToday(lastModified))
            lastModifiedText += DateUtils.formatDateTime(getApplicationContext(), lastModified,
                DateUtils.FORMAT_SHOW_TIME);
        else
          lastModifiedText += DateUtils.getRelativeDateTimeString(getApplicationContext(),
            lastModified, DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_SHOW_DATE);

        lastModifiedView.setText(lastModifiedText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);

                EditText editNoteTitle = (EditText)findViewById(R.id.editNoteTitle);
                imm.hideSoftInputFromWindow(editNoteTitle.getWindowToken(), 0);

                EditText editNote = (EditText)findViewById(R.id.editNote);
                imm.hideSoftInputFromWindow(editNote.getWindowToken(), 0);

                DetectChangesAndSendToParent();
                return true;
            case R.id.action_delete:
                Intent ret = new Intent();
                ret.putExtra(MainActivity.NOTE_ID, mId);
                ret.putExtra(MainActivity.NOTE_INDEX, mIndex);
                ret.putExtra(MainActivity.NOTE_OP, MainActivity.NOTE_OP_DELETE);
                setResult(Activity.RESULT_OK, ret);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed ()
    {
      DetectChangesAndSendToParent();
    }

    private void DetectChangesAndSendToParent()
    {
        EditText editNote = (EditText)findViewById(R.id.editNote);
        String newNoteContent = editNote.getText().toString();

        EditText editNoteTitle = (EditText)findViewById(R.id.editNoteTitle);
        String newNoteTitle = editNoteTitle.getText().toString();

        boolean changed = false;
        if (((newNoteTitle != null) && !newNoteTitle.equals(mNoteTitle)) ||
            (!newNoteContent.equals(mNoteContent)))
            changed = true;

        if (changed) {
            Intent ret = new Intent();
            ret.putExtra(MainActivity.NOTE_ID, mId);
            ret.putExtra(MainActivity.NOTE_INDEX, mIndex);
            ret.putExtra(MainActivity.NOTE_OP, MainActivity.NOTE_OP_EDIT);

            ret.putExtra(MainActivity.NOTE_TITLE, newNoteTitle);
            ret.putExtra(MainActivity.NOTE_TEXT, newNoteContent);

            setResult(Activity.RESULT_OK, ret);
        }
        else
            setResult(Activity.RESULT_CANCELED, null);
        finish();
    }

    private Integer mId;
    private Integer mIndex;

    private String mNoteContent;
    private String mNoteTitle;
}
