package jakub.com.homeshield.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import jakub.com.homeshield.model.DoorState;


public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Name
    private static final String DATABASE_NAME = "home-shield";
    private static final int DATABASE_VERSION = 2;

    // Contacts table name
    public static final String TABLE_NAME = "events";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_STATE = "state";
    private static final String KEY_DATE = "date";

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_NAME + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + KEY_MESSAGE + " TEXT,"
            + KEY_STATE + " INT,"
            + KEY_DATE + " LONG" + ")";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DatabaseHandler.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Adding new contact
    public void addDoorState(DoorState doorState) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_MESSAGE, doorState.getMsg());
        values.put(KEY_STATE, doorState.getState());
        values.put(KEY_DATE, doorState.getTimestamp());

        // Inserting Row
        db.insert(TABLE_NAME, null, values);
        db.close(); // Closing database connection
    }

    public void deleteDoorState(DoorState doorState) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_ID + " = ?",
                new String[]{String.valueOf(doorState.getId())});
        db.close();
    }

    // Getting All DoorStates
    public List<DoorState> getAllStates() {
        List<DoorState> stateList = new ArrayList<DoorState>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DoorState doorState = cursorToDoorState(cursor);
                // Adding contact to list
                stateList.add(doorState);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return stateList;
    }

    private DoorState cursorToDoorState(Cursor cursor) {
        DoorState doorState = new DoorState();
        doorState.setId(cursor.getLong(0));
        doorState.setMsg(cursor.getString(1));
        doorState.setState(cursor.getInt(2));
        doorState.setTimestamp(Long.parseLong(cursor.getString(3)));
        return doorState;
    }

}
