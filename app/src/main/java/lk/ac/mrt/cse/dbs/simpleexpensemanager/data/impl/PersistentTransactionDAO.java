package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

/**
 * Created by Charith on 11/19/2016.
 */

public class PersistentTransactionDAO  implements TransactionDAO{

    private final ExpenseManagerDbHelper expenseManagerDbHelper;
    private final Context mContext;

    public PersistentTransactionDAO(Context context) {
        this.mContext = context;
        this.expenseManagerDbHelper = ExpenseManagerDbHelper.getHelper(context);
    }

    /***
     * Log the transaction requested by the user.
     *
     * @param date        - date of the transaction
     * @param accountNo   - account number involved
     * @param expenseType - type of the expense
     * @param amount      - amount involved
     */
    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {

        boolean transactionType = false;
        if (expenseType == ExpenseType.EXPENSE) transactionType = true;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = sdf.format(date);

        SQLiteDatabase db = expenseManagerDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ExpenseManagerContract.TransactionEntry.COLUMN_DATE, formattedDate);
        values.put(ExpenseManagerContract.TransactionEntry.COLUMN_ACC_NO,accountNo);
        values.put(ExpenseManagerContract.TransactionEntry.COLUMN_EXPENSE_TYPE, transactionType);
        values.put(ExpenseManagerContract.TransactionEntry.COLUMN_AMOUNT, amount);

        db.insert(ExpenseManagerContract.TransactionEntry.TABLE_NAME, null, values);
        db.close();
    }

    /***
     * Return all the transactions logged.
     *
     * @return - a list of all the transactions
     */
    @Override
    public List<Transaction> getAllTransactionLogs() {
        List<Transaction> transactionList = new ArrayList<>();
        String select_Query = "SELECT * from "+ ExpenseManagerContract.TransactionEntry.TABLE_NAME;

        SQLiteDatabase db = expenseManagerDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(select_Query, null);
        if (cursor.moveToFirst()){
            do{
                Date date = null;
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                try {
                    date = format.parse(cursor.getString(0));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String acc_no = cursor.getString(1);

                ExpenseType expenseType = ExpenseType.INCOME;
                if (cursor.getInt(2) == 1) expenseType = ExpenseType.EXPENSE;

                double amount = cursor.getDouble(3);

                Transaction transaction = new Transaction(date,acc_no,expenseType,amount);
                transactionList.add(transaction);
            }while(cursor.moveToNext());
        }
        db.close();
        return transactionList;
    }

    /***
     * Return a limited amount of transactions logged.
     *
     * @param limit - number of transactions to be returned
     * @return - a list of requested number of transactions
     */
    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        List<Transaction> transactionList = new ArrayList<>();
        SQLiteDatabase db = expenseManagerDbHelper.getReadableDatabase();

        String select_Query = "SELECT * FROM "+ExpenseManagerContract.TransactionEntry.TABLE_NAME+" ORDER BY "+ExpenseManagerContract.TransactionEntry.COLUMN_DATE+" DESC Limit "+limit;
        Cursor cursor = db.rawQuery(select_Query,null);
        if (cursor.moveToFirst()){
            do{
                Date date = null;
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                try {
                    date = format.parse(cursor.getString(0));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String acc_no = cursor.getString(1);

                ExpenseType expenseType = ExpenseType.INCOME;
                if (cursor.getInt(2) == 1) expenseType = ExpenseType.EXPENSE;

                double amount = cursor.getDouble(3);

                Transaction transaction = new Transaction(date,acc_no,expenseType,amount);
                transactionList.add(transaction);
            }while(cursor.moveToNext());
        }
        db.close();
        return transactionList;
    }
}
