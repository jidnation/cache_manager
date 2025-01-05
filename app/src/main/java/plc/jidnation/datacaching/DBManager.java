package plc.jidnation.datacaching;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.widget.Toast;

public class DBManager  {

    static final String dbName = "Students";
    static final String tableName = "Logins";
    static final String columnUsername = "userName";
    static final String columnPassword = "password";
    static final int dbVersion = 1;
    SQLiteDatabase sqLiteDatabase;

    //create table Login(ID integer primary key autoincrement, username text, password text)
    static final String createTable = "Create Table IF NOT EXISTS " +
            tableName +
            "(ID integer PRIMARY KEY AUTOINCREMENT, " +
            columnUsername + " text, " +
            columnPassword + " text)";

    static class DatabaseHelperUser extends SQLiteOpenHelper {
        Context context;
        DatabaseHelperUser(Context context){
            super(context, dbName, null, dbVersion);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(createTable);
            Toast.makeText(context, "Database created", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("Drop table IF EXISTS " + tableName);
            onCreate(db);
        }
    }



    public DBManager(Context context){

        DatabaseHelperUser helperUser = new DatabaseHelperUser(context);
         sqLiteDatabase = helperUser.getWritableDatabase();
    }

   public  long insert(ContentValues values) {
        return  sqLiteDatabase.insert(tableName, "", values);
   }

    public Cursor query(String[] projection, String selection, String[] selectionArgs, String sortOrder){
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(tableName);

         return queryBuilder.query(sqLiteDatabase ,projection, selection, selectionArgs, null, null, sortOrder );
    }

    public int delete(String selection, String[] selectionArgs) {
        return sqLiteDatabase.delete(tableName, selection, selectionArgs);
    }

    public int update(ContentValues values, String selection, String[] selectionArgs) {
        return sqLiteDatabase.update(tableName, values, selection, selectionArgs);
    }
}
