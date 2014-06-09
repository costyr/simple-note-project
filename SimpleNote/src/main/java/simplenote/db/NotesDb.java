package simplenote.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Costi on 5/11/2014.
 */
public class NotesDb {

    public NotesDb(Context context) {
        mDbHelper =  new NotesDbOpenHelper(context);
        mDb = mDbHelper.getWritableDatabase();
    }

    /*Close()
    {
      mDb.close();
    */

    public void AddNote(Integer aId, String aTitle, String aNote)
    {
        if (mDb != null) {
            ContentValues values = new ContentValues();
            values.put(NotesDbOpenHelper.ID_COLUMN, aId.toString());
            values.put(NotesDbOpenHelper.TITLE_COLUMN, aTitle);
            values.put(NotesDbOpenHelper.NOTE_COLUMN, aNote);
            values.put(NotesDbOpenHelper.DELETED_COLUMN, 0);
            mDb.insert(NotesDbOpenHelper.NOTES_TABLE_NAME, null, values);
            //String query = "INSERT INTO " + NOTES_TABLE_NAME + " VALUES (" + aTitle + ", " + aNote + ");";
            //    db.rawQuery(query, null);
        }
    }

    public void DeleteNote(Integer aId)
    {
        if (mDb != null) {
            ContentValues values = new ContentValues();
            values.put(NotesDbOpenHelper.DELETED_COLUMN, 1);

            String cond = NotesDbOpenHelper.ID_COLUMN + "=" + aId;
            mDb.update(NotesDbOpenHelper.NOTES_TABLE_NAME, values, cond, null);
        }
    }

    public Cursor GetNotes()
    {
        String query = "SELECT " + NotesDbOpenHelper.ID_COLUMN + ", " + NotesDbOpenHelper.NOTE_COLUMN + ", "+ NotesDbOpenHelper.DELETED_COLUMN + " FROM " + NotesDbOpenHelper.NOTES_TABLE_NAME +
                " WHERE " + NotesDbOpenHelper.DELETED_COLUMN + "=0;";
        if (mDb != null) {
            Cursor result = mDb.rawQuery(query, null);
            return result;
        }

        else
            return null;
    }

    private NotesDbOpenHelper mDbHelper;
    private SQLiteDatabase mDb;
}

