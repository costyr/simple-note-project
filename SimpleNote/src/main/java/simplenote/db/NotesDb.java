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

    public void AddNote(Note aNote)
    {
        if (mDb != null) {
            ContentValues values = new ContentValues();
            values.put(NotesDbOpenHelper.ID_COLUMN, aNote.getId().toString());
            values.put(NotesDbOpenHelper.TITLE_COLUMN, aNote.getTitle());
            values.put(NotesDbOpenHelper.NOTE_COLUMN, aNote.toString());
            values.put(NotesDbOpenHelper.LAST_MODIFIED_COLUMN, aNote.getLastModified());
            values.put(NotesDbOpenHelper.DELETED_COLUMN, 0);
            mDb.insert(NotesDbOpenHelper.NOTES_TABLE_NAME, null, values);
            //String query = "INSERT INTO " + NOTES_TABLE_NAME + " VALUES (" + aTitle + ", " + aNote + ");";
            //    db.rawQuery(query, null);
        }
    }

    public void UpdateNote(Integer aId, String aTitle, String aNote)
    {
        if (mDb != null) {
            ContentValues values = new ContentValues();

            values.put(NotesDbOpenHelper.TITLE_COLUMN, aTitle);
            values.put(NotesDbOpenHelper.NOTE_COLUMN, aNote);
            values.put(NotesDbOpenHelper.LAST_MODIFIED_COLUMN, System.currentTimeMillis());

            String cond = NotesDbOpenHelper.ID_COLUMN + "=" + aId;
            mDb.update(NotesDbOpenHelper.NOTES_TABLE_NAME, values, cond, null);
        }
    }

    public void DeleteNote(Integer aId)
    {
        if (mDb != null) {
            ContentValues values = new ContentValues();

            values.put(NotesDbOpenHelper.LAST_MODIFIED_COLUMN, System.currentTimeMillis());
            values.put(NotesDbOpenHelper.DELETED_COLUMN, 1);

            String cond = NotesDbOpenHelper.ID_COLUMN + "=" + aId;
            mDb.update(NotesDbOpenHelper.NOTES_TABLE_NAME, values, cond, null);
        }
    }

    public Cursor GetNotes()
    {
        String query = "SELECT " +
          NotesDbOpenHelper.ID_COLUMN + ", " +
          NotesDbOpenHelper.TITLE_COLUMN + ", " +
          NotesDbOpenHelper.NOTE_COLUMN + ", " +
          NotesDbOpenHelper.LAST_MODIFIED_COLUMN + ", "+
          NotesDbOpenHelper.DELETED_COLUMN +
                " FROM " + NotesDbOpenHelper.NOTES_TABLE_NAME +
                " WHERE " + NotesDbOpenHelper.DELETED_COLUMN + "=0;";
        if (mDb != null) {
            return mDb.rawQuery(query, null);
        }

        else
            return null;
    }

    private NotesDbOpenHelper mDbHelper;
    private SQLiteDatabase mDb;
}

