package com.labourtoday.androidapp;

public class Constants {

    public enum URLS {
        // Names of server endpoints
        ROOT("https://labour-today.herokuapp.com/"),
        //ROOT("http://10.0.1.51:8000/"),
        PAYMENT(ROOT.string + "payment/"),
        LABOURER_LIST(ROOT.string + "labourer-list/" ),
        LABOURER_DETAIL(ROOT.string + "labourer-detail/"),
        CONTRACTOR_LIST(ROOT.string + "contractor-list/" ),
        CONTRACTOR_DETAIL(ROOT.string + "contractor-detail/"),
        TOKEN_AUTH(ROOT.string + "api-token-auth/"),
        LABOURER_SEARCH(ROOT.string + "labourer-search/" ),
        ;
        public final String string;
        URLS(final String text ){
            this.string=text;
        }
    }

    public static String STRIPE_TOKEN = "stripe_token";
    public static String NO_DEVICE = "no_device";
    public static String REGISTRATION_ID = "registration_id";
    public static String SERVER_API_KEY = "AIzaSyCdujUvUcucQ_iOnTS_aMCQ1__Zvz5RMcM";
    public static String SENDER_ID = "212482481391";
    public static String TWILIO_NUMBER = "+16042569605";
    public static String AUTH_TOKEN = "token";
    // Intent actions
    public static String ACTION_LOGIN = "actionLogin";
    public static String ACTION_CREATE_USER = "actionCreateUser";
    public static String LAST_LOGIN = "lastLogin";

    // Intent filters
    public static String REGISTRATION_COMPLETE = "registrationComplete";
    public static String GCM_REGISTRATION_TOKEN_SENT = "tokenSent";

    public static String PAYMENT_CONFIRMED = "paymentConfirmed";


    // Intent extra string keys
    public static String COMPANY_NAME = "company_name";
    public static String PASSWORD = "password";
    public static String EMAIL = "email";
    public static String FIRST_NAME = "first_name";
    public static String LAST_NAME = "last_name";
    public static String PHONE_NUMBER = "phone_number";

    public static String CONTRACTOR = "contractor";
    public static String LABOURER = "labourer";
}
