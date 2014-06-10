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
 * Lesser General Public License for more details.
 *
 * Contributors:
 */

package simplenote.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Costi on 5/11/2014.
 */
public class NotesDbOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 6;
    private static final String DATABASE_NAME = "NotesDb";
    public static final String NOTES_TABLE_NAME = "notes";
    public static final String ID_COLUMN = "ID";
    public static final String TITLE_COLUMN = "Title";
    public static final String NOTE_COLUMN = "Note";
    public static final String DELETED_COLUMN = "Deleted";
    private static final String NOTES_TABLE_CREATE =
            "CREATE TABLE " + NOTES_TABLE_NAME + " (" +
                    ID_COLUMN + " INT PRIMARY KEY, " +
                    TITLE_COLUMN + " TEXT, " +
                    NOTE_COLUMN + " TEXT, " +
                    DELETED_COLUMN + " INT);";

    private static final String NOTES_TABLE_DROP = "DROP TABLE IF EXISTS " + NOTES_TABLE_NAME + " ;";

    NotesDbOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
}
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(NOTES_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.w(NotesDbOpenHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data"
        );
        db.execSQL(NOTES_TABLE_DROP);
        onCreate(db);
    }
}

