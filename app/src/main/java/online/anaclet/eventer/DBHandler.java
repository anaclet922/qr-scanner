package online.anaclet.eventer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {

    // creating a constant variables for our database.
    // below variable is for our database name.
    private static final String DB_NAME = "ticketsdb";

    // below int is our database version
    private static final int DB_VERSION = 1;

    private static final String TABLE_NAME = "tickets";

    private static final String ID_COL = "id";
    private static final String TICKET_NO_COL = "ticket_no";
    private static final String EVENT_COL = "event_id";
    private static final String TICKET_NBR_COL = "tickets_nbr";
    private static final String VALID_COL = "valid";
    private static final String PAYED_COL = "payed";
    private static final String USED_COL = "used";

    // creating a constructor for our database handler.
    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // below method is for creating a database by running a sqlite query
    @Override
    public void onCreate(SQLiteDatabase db) {
        // on below line we are creating
        // an sqlite query and we are
        // setting our column names
        // along with their data types.
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TICKET_NO_COL + " TEXT,"
                + EVENT_COL + " INTEGER,"
                + TICKET_NBR_COL + " INTEGER,"
                + VALID_COL + " INTEGER,"
                + PAYED_COL + " TEXT,"
                + USED_COL + " INTEGER)";

        // at last we are calling a exec sql
        // method to execute above sql query
        db.execSQL(query);
    }

    // this method is use to add new course to our sqlite database.
    public void addNewTicket(String ticket_no, int event_id, int ticket_nbr, int valid, String payed, int used) {
        Log.d("Inserting", "Ok2");
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();


        values.put(TICKET_NO_COL, ticket_no);
        values.put(EVENT_COL, event_id);
        values.put(TICKET_NBR_COL, ticket_nbr);
        values.put(VALID_COL, valid);
        values.put(PAYED_COL, payed);
        values.put(USED_COL, used);

        // after adding all values we are passing
        // content values to our table.
        db.insert(TABLE_NAME, null, values);

        // at last we are closing our
        // database after adding database.
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public ArrayList<Ticket> readTicket(String ticket_no) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorTickets = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + TICKET_NO_COL + "='" + ticket_no + "'", null);


        ArrayList<Ticket> ticketModalArrayList = new ArrayList<>();

        // moving our cursor to first position.
        if (cursorTickets.moveToFirst()) {
            do {
                // on below line we are adding the data from cursor to our array list.
                ticketModalArrayList.add(new Ticket(cursorTickets.getInt(0),
                        cursorTickets.getString(1),
                        cursorTickets.getInt(2),
                        cursorTickets.getInt(3),
                        cursorTickets.getInt(4),
                        cursorTickets.getString(5),
                        cursorTickets.getInt(6)));
            } while (cursorTickets.moveToNext());
            // moving our cursor to next.
        }
        // at last closing our cursor
        // and returning our array list.
        cursorTickets.close();
        return ticketModalArrayList;
    }
    public Ticket getLastTicket() {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorTickets = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY ID DESC LIMIT 1", null);

        // moving our cursor to first position.
        cursorTickets.moveToFirst();
        if(cursorTickets.getCount() > 0 && cursorTickets != null) {
            Ticket ticket = new Ticket(cursorTickets.getInt(0),
                    cursorTickets.getString(1),
                    cursorTickets.getInt(2),
                    cursorTickets.getInt(3),
                    cursorTickets.getInt(4),
                    cursorTickets.getString(5),
                    cursorTickets.getInt(6));

            cursorTickets.close();
            return ticket;
        }else{
            return null;
        }
    }

}
