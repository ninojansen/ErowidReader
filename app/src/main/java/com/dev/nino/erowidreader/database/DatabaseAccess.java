package com.dev.nino.erowidreader.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.dev.nino.erowidreader.data.ExperienceType;
import com.dev.nino.erowidreader.data.Substance;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

/**
 *
 */
public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DatabaseAccess instance;

    /**
     * Private constructor to aboid object creation from outside classes.
     *
     * @param context
     */
    private DatabaseAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    /**
     * Return a singleton instance of DatabaseAccess.
     *
     * @param context the Context
     * @return the instance of DabaseAccess
     */
    public static DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    /**
     * Open the database connection.
     */
    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    /**
     * Close the database connection.
     */
    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    public ArrayList<Substance> getSubstances() {
        Cursor c = database.rawQuery("SELECT * FROM substances", null);

        ArrayList<Substance> substances = new ArrayList<>();

        while (c.moveToNext()) {
            int idx;
            idx = c.getColumnIndexOrThrow("name");
            String name = c.getString(idx);

            idx = c.getColumnIndexOrThrow("subText");
            String subText = c.getString(idx);
            if (subText == null) {
                subText = "";
            }
            idx = c.getColumnIndexOrThrow("removed");
            boolean removed = c.getInt(idx) != 0;
            idx = c.getColumnIndexOrThrow("reportCount");
            int reportCount = c.getInt(idx);

            idx = c.getColumnIndexOrThrow("userViewCount");
            int userViewCount = c.getInt(idx);
            substances.add(new Substance(name, subText, removed, reportCount, userViewCount));
        }
        c.close();
        return substances;
    }
    // TODO make input a substance not just a name
    public ArrayList<ExperienceType> getExperienceTypesForSubstance(String substanceName) {
        Cursor c = database.rawQuery("SELECT * FROM experience_types WHERE experience_types.substanceID == (SELECT substanceID FROM substances WHERE substances.name == \""+substanceName+"\")", null);
        if(c.getCount()>0) {
            ArrayList<ExperienceType> res = new ArrayList<>();
            while(c.moveToNext()) {
                int idx;

                idx = c.getColumnIndexOrThrow("experienceTypeID");
                int experienceTypeID = c.getInt(idx);
                idx = c.getColumnIndexOrThrow("name");
                String name = c.getString(idx);
                idx = c.getColumnIndexOrThrow("url");
                String url = c.getString(idx);
                idx = c.getColumnIndexOrThrow("substanceID");
                int substanceID = c.getInt(idx);
                idx= c.getColumnIndexOrThrow("reportCount");
                int reportCount = c.getInt(idx);
                idx = c.getColumnIndexOrThrow("removed");
                boolean removed = c.getInt(idx) != 0;
                idx = c.getColumnIndexOrThrow("userViewCount");
                int userViewCount = c.getInt(idx);

                res.add(new ExperienceType(experienceTypeID,substanceID,reportCount,removed,url,name,userViewCount));
            }
            c.close();
            return res;
        }
        else {
            c.close();
            return null;
        }
    }
    public void refreshSubstances(ArrayList<Substance> newSubstances) {
        newSubstances = filterSubstancesInDB(newSubstances);

        for (Substance substance: newSubstances) {
            insertSubstance(substance);
        }
        Log.v("Substances", "Refreshed substances table " + newSubstances.size() + " New substances added ");
    }

    private ArrayList<Substance> filterSubstancesInDB(ArrayList<Substance> newSubstances) {
        ArrayList<Substance> substances = getSubstances();
        Iterator<Substance> iter = newSubstances.iterator();
        while (iter.hasNext()) {
            Substance newSubstance = iter.next();
            for (Substance currentSubstance : substances) {
                if (Objects.equals(newSubstance.getName(), currentSubstance.getName())) {
                    iter.remove();
                    break;
                }
            }
        }
        return newSubstances;
    }

    private void insertSubstance(Substance substance) {
        database.execSQL("INSERT INTO substances(name,subtext) VALUES(?,?)", new String[] {substance.getName(), substance.getSubText()});
    }

    public void setSubstanceRemoved() {

    }
}
