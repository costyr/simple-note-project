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

import android.os.Bundle;
import android.app.Activity;
import android.text.format.DateUtils;
import android.view.Menu;
import android.content.Intent;
import android.annotation.SuppressLint;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
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
        String noteContent = intent.getStringExtra(MainActivity.NOTE_TEXT);
        String noteTitle = intent.getStringExtra(MainActivity.NOTE_TITLE);

        mId = intent.getIntExtra(MainActivity.NOTE_ID, 0);
        mIndex = intent.getIntExtra(MainActivity.NOTE_INDEX, 0);

        EditText editNoteTitle = (EditText)findViewById(R.id.editNoteTitle);

        editNoteTitle.setText(noteTitle);

        EditText editNote = (EditText)findViewById(R.id.editNote);

        editNote.setText(noteContent);

        Long lastModified = intent.getLongExtra(MainActivity.NOTE_LAST_MODIFIED, 0);

        TextView lastModifiedView = (TextView)findViewById(R.id.lastModified);

        String lastModifiedText = getResources().getString(R.string.last_modified_label);
        lastModifiedText += DateUtils.getRelativeDateTimeString(getApplicationContext(),
          lastModified, 5, 43200000, DateUtils.FORMAT_SHOW_DATE);
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
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_delete:
                Intent ret = new Intent();
                ret.putExtra(MainActivity.NOTE_ID, mId);
                ret.putExtra(MainActivity.NOTE_INDEX, mIndex);
                setResult(Activity.RESULT_OK, ret);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
}

    Integer mId;
    Integer mIndex;
}
