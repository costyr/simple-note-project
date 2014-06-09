package simplenote.activity;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.content.Intent;
import android.annotation.SuppressLint;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.widget.TextView;

public class EditNoteActivity extends Activity {

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the message from the intent
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.NOTE_TEXT);

        mId = intent.getIntExtra(MainActivity.NOTE_ID, 0);
        mIndex = intent.getIntExtra(MainActivity.NOTE_INDEX, 0);

        // Create the text view
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(message);

        // Set the text view as the activity layout
        setContentView(textView);
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
