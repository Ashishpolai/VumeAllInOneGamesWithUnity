package com.vume.allinonegames.Model;

public class FirebaseAnalyticsCustomKeys {
    public static final String EVENT_NEW_INSTALL = "new_install";
    public static final String EVENT_LOGIN_ERROR = "login_error";
    public static final String EVENT_START_REGISTRATION = "start_registration"; //Once phone no is verified while registration
    public static final String EVENT_CONTINUE_REGISTRATION = "continue_registration";//Once otp is verified while registration
    public static final String EVENT_FINISH_REGISTRATION = "finish_registration";//Once registration is completed successfully
    public static final String EVENT_GAME_STARTED = "game_started";//Once webview game is started from buyin dialog
    public static final String EVENT_FROM_INSTALL_TO_LOBBY_AT_FIRSTTIME = "reached_lobby_on_firsttimeopen_session";
    public static final String EVENT_FROM_INSTALL_TO_WEBVIEWGAME_AT_FIRSTTIME = "reached_webviewgame_on_firsttimeopen_session";
    public static final String EVENT_EDITPHONENO_ONOTPSCREEN_REGISTER = "editphoneno_onotpscreen_duringregister";

    public static final String EVENT_PARMATER_INSTALLKEY = "install_key";
    public static final String EVENT_PARMATER_USERID = "user_id";
    public static final String EVENT_PARAMETER_USERANALYTICSIDENTIFER = "event_user_id";
    public static final String EVENT_PARAMETER_USERANALYTICSNAME= "event_user_name";
    public static final String EVENT_PARAMETER_USERANALYTICSIDENTIFERTYPE = "event_user_idtype";
    public static final String EVENT_PARMATER_LOGINERROR_MSG = "login_error_msg";
    public static final String EVENT_PARMATER_LOGINERROR_CODE = "login_error_code";
    public static final String EVENT_PARMATER_STARTREGISTRATION_MOBILENO = "start_registration_mobileno";
    public static final String EVENT_PARMATER_CONTINUEREGISTRATION_MOBILENO = "continue_registration_mobileno";
    public static final String EVENT_PARAMETER_EDITPHONENO_ONOTPSCREEN_REGISTER_MOBILENO = "editphoneno_onotpscreen_mobileno";
    public static final String EVENT_PARAMETER_FINISHREGISTRATION_USERID = "finish_registration_userid";
    public static final String EVENT_PARAMETER_GAME_STARTED_TABLEID = "game_started_tableid";
    public static final String EVENT_PARAMETER_GAME_STARTED_BETAMOUNT = "game_started_betvalue";

    public static final String USER_PROPERTY_USERNAME = "Username";
    public static final String USER_PROPERTY_IDTYPE = "ID_Type";

    public static final String USER_PROPERTY_IDTYPE_VALUE_USERID = "User ID";
    public static final String USER_PROPERTY_IDTYPE_VALUE_INSTALLKEY = "Install Key";

    public static final String LOGIN_PARAMETER_METHOD_VALUE_USERNAMEPASS = "Username & Password";
    public static final String LOGIN_PARAMETER_METHOD_VALUE_AUTOSIGNIN = "Auto SignIn";
}
