package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO extends SQLiteOpenHelper implements AccountDAO {

    private static final String DATABASE_NAME = "IndexNumber";

    private static final String TABLE_ACCOUNTS = "Accounts";

    private static final String COLUMN_ACC_NO = "Account_No";
    private static final String COLUMN_BANK_NAME = "Bank_Name";
    private static final String COLUMN_HOLDER_NAME = "Holder_Name";
    private static final String COLUMN_BALANCE = "Balance";

    private static final int VERSION_NUMBER = 1;

    public PersistentAccountDAO(Context context) {
        super(context, DATABASE_NAME, null, VERSION_NUMBER);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_ACCOUNTS + " (" +
                COLUMN_ACC_NO + " TEXT, " +
                COLUMN_BANK_NAME + " TEXT, " +
                COLUMN_BALANCE + " REAL, " +
                COLUMN_HOLDER_NAME + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNTS);

        onCreate(sqLiteDatabase);
    }


    public void addAccount(Account account) {

        // Create a database object
        SQLiteDatabase DB = this.getWritableDatabase();
        // Create a content value
        ContentValues Entry_Cache = new ContentValues();

        // Generate cache line
        Entry_Cache.put(COLUMN_ACC_NO, account.getAccountNo());
        Entry_Cache.put(COLUMN_BANK_NAME, account.getBankName());
        Entry_Cache.put(COLUMN_BALANCE, account.getBalance());
        Entry_Cache.put(COLUMN_HOLDER_NAME, account.getAccountHolderName());

        DB.beginTransaction();
        try {
            // Insert the cache line to the table
            DB.insert(TABLE_ACCOUNTS, null, Entry_Cache);
            DB.setTransactionSuccessful();
        } finally {
            DB.endTransaction();
        }
    }

    public List<String> getAccountNumbersList() {

        List<String> list = new ArrayList<>();
        // Create a database object
        SQLiteDatabase DB = this.getReadableDatabase();

        // Create a cursor file we get from executing this above command
        Cursor crsr = DB.query(
                TABLE_ACCOUNTS,
                new String[] {COLUMN_ACC_NO},
                null, null, null, null, COLUMN_ACC_NO);

        crsr.moveToFirst();

        while (!crsr.isAfterLast()) {
            // Add that to the array list
            list.add(crsr.getString(crsr.getColumnIndex(COLUMN_ACC_NO)));
            // Go to the next row
            crsr.moveToNext();
        }

        // Closes database and cursor and return the list
        crsr.close(); DB.close();
        return list;
    }

    public List<Account> getAccountsList() {
        List<Account> list = new ArrayList<>();
        // Create a database object
        SQLiteDatabase DB = this.getReadableDatabase();

        // Create a cursor file we get from executing this above command
        Cursor crsr = DB.query(
                TABLE_ACCOUNTS,
                new String[] {COLUMN_ACC_NO, COLUMN_BANK_NAME, COLUMN_BALANCE, COLUMN_HOLDER_NAME},
                null, null, null, null, COLUMN_ACC_NO);

        crsr.moveToFirst();

        while (!crsr.isAfterLast()) {
            // Add that to the array list
            Account account = new Account(crsr.getString(crsr.getColumnIndex(COLUMN_ACC_NO)),
                    crsr.getString(crsr.getColumnIndex(COLUMN_BANK_NAME)),
                    crsr.getString(crsr.getColumnIndex(COLUMN_HOLDER_NAME)),
                    crsr.getDouble(crsr.getColumnIndex(COLUMN_BALANCE)));
            list.add(account);
            // Go to the next row
            crsr.moveToNext();
        }

        // Closes database and cursor and return the list
        crsr.close(); DB.close();
        return list;
    }

    public Account getAccount(String accountNo) throws InvalidAccountException {

        List<Account> list = new ArrayList<>();
        Account account = null;
        // Create a database object
        SQLiteDatabase DB = this.getReadableDatabase();

        // Create a cursor file we get from executing this above command
        Cursor crsr = DB.query(
                TABLE_ACCOUNTS,
                new String[] {COLUMN_ACC_NO, COLUMN_BANK_NAME, COLUMN_BALANCE, COLUMN_HOLDER_NAME},
                COLUMN_ACC_NO + "=?", new String[]{accountNo}, null, null, null);

        crsr.moveToFirst();

        while (!crsr.isAfterLast()) {
            // Add that to the array list
            account = new Account(crsr.getString(crsr.getColumnIndex(COLUMN_ACC_NO)),
                    crsr.getString(crsr.getColumnIndex(COLUMN_BANK_NAME)),
                    crsr.getString(crsr.getColumnIndex(COLUMN_HOLDER_NAME)),
                    crsr.getDouble(crsr.getColumnIndex(COLUMN_BALANCE)));
            // Go to the next row
            crsr.moveToNext();
        }

        if (account == null) {
            throw new InvalidAccountException("Account doesn't exist");
        }
        // Closes database and cursor and return the list
        crsr.close(); DB.close();
        return account;
    }

    public void removeAccount(String accountNo) throws InvalidAccountException {
        // Create a database object
        SQLiteDatabase DB = this.getWritableDatabase();

        // return the delete statement
        String command = COLUMN_ACC_NO + " = ?" + accountNo;
        String[] locator = {COLUMN_ACC_NO};

        if (!(DB.delete(TABLE_ACCOUNTS, command, locator) > 0)) {
            throw new InvalidAccountException("No account exists!");
        }
    }

    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {

        // Create a database object
        SQLiteDatabase DB = this.getWritableDatabase();
        // Generate content values
        ContentValues Entry_Cache = new ContentValues();
        // Put values to content values
        Entry_Cache.put(COLUMN_BALANCE, amount);

        DB.beginTransaction();
        try {
            DB.update(
                    TABLE_ACCOUNTS,
                    Entry_Cache,
                    COLUMN_ACC_NO + " = ?",
                    new String[] {accountNo});

            DB.setTransactionSuccessful();
        } finally {DB.endTransaction();}
    }
}
