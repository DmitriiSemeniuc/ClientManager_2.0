package com.semeniuc.dmitrii.clientmanager.data.local;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.semeniuc.dmitrii.clientmanager.model.Appointment;
import com.semeniuc.dmitrii.clientmanager.model.Client;
import com.semeniuc.dmitrii.clientmanager.model.Contact;
import com.semeniuc.dmitrii.clientmanager.model.Service;
import com.semeniuc.dmitrii.clientmanager.model.Tools;
import com.semeniuc.dmitrii.clientmanager.model.User;
import com.semeniuc.dmitrii.clientmanager.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String LOG_TAG = DatabaseHelper.class.getSimpleName();
    private static final boolean DEBUG = Constants.DEBUG;

    private static final String DATABASE_NAME = "ClientManagerDB.sqlite";

    // Any time we make changes to our database objects,
    // we may have to increase the database version
    private static final int DATABASE_VERSION = 1;

    // The DAO object we use to access the SimpleData table pressure
    private Dao<User, Integer> userDao = null;
    private Dao<Contact, Integer> contactDao = null;
    private Dao<Client, Integer> clientDao = null;
    private Dao<Service, Integer> serviceDao = null;
    private Dao<Tools, Integer> toolsDao = null;
    private Dao<Appointment, Integer> appointmentDao = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {

            TableUtils.createTable(connectionSource, User.class);
            TableUtils.createTable(connectionSource, Contact.class);
            TableUtils.createTable(connectionSource, Client.class);
            TableUtils.createTable(connectionSource, Service.class);
            TableUtils.createTable(connectionSource, Tools.class);
            TableUtils.createTable(connectionSource, Appointment.class);
            if (DEBUG) Log.i(LOG_TAG, "Tables are created");
        } catch (SQLException e) {
            if (DEBUG) Log.e(LOG_TAG, "Can't create database", e);
            throw new RuntimeException(e);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
                          int oldVersion, int newVersion) {
        try {
            List<String> allSql = new ArrayList<>();
            switch (oldVersion) {
                case 1:
                    //allSql.add("altere AdData add column `new_col` VARCHAR");
                    //allSql.add("altere AdData add column `new_col2` VARCHAR");
            }
            for (String sql : allSql) {
                db.execSQL(sql);
            }
        } catch (SQLException e) {
            if (DEBUG) Log.e(LOG_TAG, "exception during onUpgrade", e);
            throw new RuntimeException(e);
        }
    }

    public Dao<User, Integer> getUserDao() {
        if (null == userDao) {
            try {
                userDao = getDao(User.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return userDao;
    }

    public Dao<Contact, Integer> getContactDao() {
        if (null == contactDao) {
            try {
                contactDao = getDao(Contact.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return contactDao;
    }

    public Dao<Client, Integer> getClientDao() {
        if (null == clientDao) {
            try {
                clientDao = getDao(Client.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return clientDao;
    }

    public Dao<Appointment, Integer> getAppointmentDao() {
        if (null == appointmentDao) {
            try {
                appointmentDao = getDao(Appointment.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return appointmentDao;
    }

    public Dao<Service, Integer> getServiceDao() {
        if (null == serviceDao) {
            try {
                serviceDao = getDao(Service.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return serviceDao;
    }

    public Dao<Tools, Integer> getToolsDao() {
        if (null == toolsDao) {
            try {
                toolsDao = getDao(Tools.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return toolsDao;
    }

    @Override
    public void close() {
        super.close();
        userDao = null;
        contactDao = null;
        clientDao = null;
        serviceDao = null;
        toolsDao = null;
        appointmentDao = null;
    }
}
