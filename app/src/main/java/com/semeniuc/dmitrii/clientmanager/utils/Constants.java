package com.semeniuc.dmitrii.clientmanager.utils;

import com.semeniuc.dmitrii.clientmanager.model.Appointment;

public class Constants {

    public static final String USER = "user";
    public static final String GOOGLE_USER = "google";
    public static final String REGISTERED_USER = "registered";
    public static final String NEW_USER = "new";
    public static final String EMAIL = "email";
    public static final String LOGGED = "logged";
    public static final boolean LOGGED_IN = true;
    public static final boolean LOGGED_OUT = false;
    public static final String EMPTY = "";
    public static final String LOGIN_PREFS = "loginPrefs";
    public static final int USER_SAVED = 101;
    public static final int USER_NOT_SAVED = 102;
    public static final int USER_EXISTS = 103;
    public static final int NO_DB_RESULT = 404;
    public static final int SIZE_EMPTY = 0;
    public static final int RC_SIGN_IN = 9001;
    public static final boolean REMOTE_ACCESS_ENABLED = false;

    public static final boolean DEBUG = true;
    public static final String DOT = ".";

    public static final int CREATED = 1;
    public static final int UPDATED = 1;
    public static final int DELETED = 1;

    public static final int ORDER_BY_DATE = 111;
    public static final int ORDER_BY_CLIENT = 222;
    public static final int ARCHIVED_ORDER_BY_DATE = 333;
    public static final int ARCHIVED_ORDER_BY_CLIENT = 4444;

    public static final String TIME_FORMAT = "HH:mm";
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm";

    public static final String APPOINTMENT_PATH =
            Appointment.class.getPackage().getName().concat(DOT + Appointment.class.getSimpleName());

    public static final int PERMISSIONS_REQUEST_CALL_PHONE = 5555;

    // Images
    public static final int PAID = 1001;
    public static final int DONE = 1002;
    public static final int HAIR_COLORING = 1003;
    public static final int HAIRDO = 1004;
    public static final int HAIR_CUT = 1005;
    public static final int BRUSH = 1006;
    public static final int HAIR_BRUSH = 1007;
    public static final int HAIR_DRAYER = 1008;
    public static final int OXY = 1009;
    public static final int CUT_SET = 1010;
    public static final int HAIR_BAND = 1011;
    public static final int SPRAY = 1012;
    public static final int TUBE = 1013;
    public static final int TRIMMER = 1014;
}
