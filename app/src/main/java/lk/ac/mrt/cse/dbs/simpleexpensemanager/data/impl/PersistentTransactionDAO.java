package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO extends SQLiteOpenHelper implements TransactionDAO {

    private static final String DATABASE_NAME = "140499X";

    private static final String TABLE_TRANSACTIONS = "Transactions";

    private static final String COLUMN_ACC_NO = "Account_No";
    private static final String COLUMN_DATE = "Date";
    private static final String COLUMN_EXPENSE_TYPE = "Expense_Type";
    private static final String COLUMN_AMOUNT = "Amount";

    private static final int VERSION_NUMBER = 1;

    public PersistentTransactionDAO(Context context) {
        super(context, DATABASE_NAME, null, VERSION_NUMBER);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_TRANSACTIONS + " (" +
                COLUMN_DATE + " DATE, " +
                COLUMN_ACC_NO + " TEXT, " +
                COLUMN_EXPENSE_TYPE + " TEXT, " +
                COLUMN_AMOUNT + " REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);

        onCreate(sqLiteDatabase);
    }


    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        // Create a database object
        SQLiteDatabase DB = this.getWritableDatabase();
        // Create a content value
        ContentValues Entry_Cache = new ContentValues();

        // Generate cache line
        Entry_Cache.put(COLUMN_DATE, date.toString());
        Entry_Cache.put(COLUMN_ACC_NO, accountNo);
        Entry_Cache.put(COLUMN_EXPENSE_TYPE, expenseType.toString());
        Entry_Cache.put(COLUMN_AMOUNT, amount);

        DB.beginTransaction();
        try {
            // Insert the cache line to the table
            DB.insert(TABLE_TRANSACTIONS, null, Entry_Cache);
            DB.setTransactionSuccessful();
        } finally {
            DB.endTransaction();
        }
    }

    public List<Transaction> getAllTransactionLogs() {

        List<Transaction> list = new ArrayList<>();
        // Create a database object
        SQLiteDatabase DB = this.getReadableDatabase();

        // Create a cursor file we get from executing this above command
        Cursor crsr = DB.query(
                TABLE_TRANSACTIONS,
                new String[] {COLUMN_DATE, COLUMN_ACC_NO, COLUMN_EXPENSE_TYPE, COLUMN_AMOUNT},
                null, null, null, null, null);

        crsr.moveToFirst();

        while (!crsr.isAfterLast()) {
            // Add that to the array list
            Date date = new Date(Long.parseLong(crsr.getString(crsr.getColumnIndex(COLUMN_DATE))));
            Transaction transaction = new Transaction(date,
                    crsr.getString(crsr.getColumnIndex(COLUMN_ACC_NO)),
                    (ExpenseType.valueOf(crsr.getString(crsr.getColumnIndex(COLUMN_EXPENSE_TYPE)))),
                    crsr.getDouble(crsr.getColumnIndex(COLUMN_AMOUNT)));
            list.add(transaction);
            // Go to the next row
            crsr.moveToNext();
        }

        // Closes database and cursor and return the list
        crsr.close(); DB.close();
        return list;
    }

    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        List<Transaction> list = new ArrayList<>();
        // Create a database object
        SQLiteDatabase DB = this.getReadableDatabase();

        // Create a cursor file we get from executing this above command
        Cursor crsr = DB.query(
                TABLE_TRANSACTIONS,
                new String[] {COLUMN_DATE, COLUMN_ACC_NO, COLUMN_EXPENSE_TYPE, COLUMN_AMOUNT},
                null, null, null, null, null);

        crsr.moveToFirst();

        while (!crsr.isAfterLast()) {
            // Add that to the array list
            Date date = new Date(Long.parseLong(crsr.getString(crsr.getColumnIndex(COLUMN_DATE))));
            Transaction transaction = new Transaction(date,
                    crsr.getString(crsr.getColumnIndex(COLUMN_ACC_NO)),
                    (ExpenseType.valueOf(crsr.getString(crsr.getColumnIndex(COLUMN_EXPENSE_TYPE)))),
                    crsr.getDouble(crsr.getColumnIndex(COLUMN_AMOUNT)));
            list.add(transaction);
            // Go to the next row
            crsr.moveToNext();
        }

        // Closes database and cursor and return the list
        crsr.close(); DB.close();
        return list;
    }
}
