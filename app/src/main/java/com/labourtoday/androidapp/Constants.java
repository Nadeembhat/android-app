package com.labourtoday.androidapp;

public class Constants {

    public enum URLS {
        // Names of server endpoints
        // ROOT("https://labour-today.herokuapp.com/"),
        ROOT("https://labour-today.herokuapp.com/"),
        // PAYMENT(ROOT.string + "payment/"),
        WORKERS(ROOT.string + "api/workers/" ),
        WORKER_DETAIL(ROOT.string + "api/worker-detail/"),
        CONTRACTORS(ROOT.string + "api/contractors/" ),
        // CONTRACTOR_DETAIL(ROOT.string + "api/contractor-detail/"),
        TOKEN_AUTH(ROOT.string + "api/token-auth/"),
        JOBS(ROOT.string + "api/jobs/"),
        REFERENCES(ROOT.string + "api/references/"),
        ;
        public final String string;
        URLS(final String text ){
            this.string=text;
        }
    }


    public static String STRIPE_TOKEN = "stripe_token";
    public static String NO_DEVICE = "no_device";
    public static String REGISTRATION_ID = "registration_id";
    // public static String TWILIO_NUMBER = "+16042569605";
    public static String AUTH_TOKEN = "token";
    // Intent actions
    public static String ACTION_LOGIN = "actionLogin";
    public static String ACTION_CREATE_USER = "actionCreateUser";
    public static String LAST_LOGIN = "lastLogin";

    // Intent filters
    public static String REGISTRATION_COMPLETE = "registrationComplete";
    public static String GCM_REGISTRATION_TOKEN_SENT = "tokenSent";

    // Intent extra string keys
    public static String COMPANY_NAME = "company";
    public static String PASSWORD = "password";
    public static String USERNAME = "username";
    public static String FIRST_NAME = "first_name";
    public static String LAST_NAME = "last_name";
    public static String PHONE_NUMBER = "phone_number";

    public static String CONTRACTOR = "contractor";
    public static String WORKER = "labourer";


    public static String UPDATE_LABOURER_JOBS = "updateLabourerJobs";
    public static String ACTION_UPDATE_IMMEDIATE = "updateWorkerImmediate";
}
