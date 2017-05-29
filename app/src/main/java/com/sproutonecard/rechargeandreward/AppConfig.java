package com.sproutonecard.rechargeandreward;

/**
 * Created by richman on 10/29/16.
 */
public interface AppConfig {

    String TAG      = "RechargeAndReward";
    String APP_NAME = "RechargeAndReward";
    int SPLASH_TIME_OUT = 1000;
    long ALERT_TIME     = 2000;



    /********************** User info *****************************/
    String CURRENT_USER = "current_user";
    String URL = "url";
    String USER_ID                 = "user_id";
    String EMAIL                   = "email";
    String MOBILE_PHONE            = "mobile_phone";
    String MOBILE_PIN              = "mobile_pin";
    String USER_FIRST_NAME         = "first_name";
    String USER_LAST_NAME          = "last_name";
    String ACCOUNTS                = "accounts";
    String FUNDING_SOURCES         = "funding_sources";
    String USER_FULL_NAME          = "full_name";
    String USER_EMAIL              = "username";
    String NAME                    ="name";
    String AVAILABLE_BALANCE       = "available_balance";
    String CURRENT_BALANCE         ="current_balance";
    String AVAILABLE_POINTS        ="points_balance";
    String AMOUNT                  ="amount";
    String FUNDING_SOURCE_ID       ="funding_source_id";
    String ACTION                  ="action";
    String TRIGGER_BALANCE         ="trigger_balance";
    String PROMOTION_CODE          ="promotion_code";
    String NEW_POINTS_BALANCE      ="new_points_balance";
    String NET_TRANSACTION_AMOUNT  ="net_transaction_amount";
    String POINTS                  ="points";
    String TRANSACTIONS            ="transactions";
    String CREATED                 ="created";
    String TRANSACTION_TYPE        ="transaction_type";
    String TRANSACTION_AMOUNT      ="transaction_amount";
    String TRANSACTION_ID          ="transaction_id";
    String ITEMS                   ="items";
    String PRICE                   ="price";
    String DESCRIPTION             ="description";
    String ACCOUNT_URL             ="account_url";
    String REASON                  ="reason";
    String USER_PASSWORD           = "password";
    String GROUP                   ="group";
    String CHARITY                 ="charity";
    String CHARITIES               ="charities";
    String EDIT_CHARITY            ="edit_charity";
    String GROUPS                  ="groups";
    String CODE                    ="code";
    String ALIAS                   ="alias";
    String NICKNAME                ="nickname";
    String USER_OLD_PASSWORD       = "old_password";
    String USER_NEW_PASSWORD       = "new_password";
    String TOKEN                   ="token";
    String CARD_TYPE               ="card_type";
    String LAST_FOUR               ="last_four";
    String ZIP_CODE                ="zip_code";
    String EXPIRATION              ="expiration";


    String REF_USER = "ref_user";
    String USER_GCM_ID = "user_gcm_id";
    String USER_BIO                = "user_bio";
    String USER_PHOTO              = "user_photo";
    String USER_PHOTO_URL          = "user_photo_url";
    String USER_LOCATION_ADDRESS   = "user_location_address";
    String USER_LOCATION_LATITUDE  = "user_location_latitude";
    String USER_LOCATION_LONGITUDE = "user_location_longitude";
    String USER_BOOED_COUNT        = "user_booed_count";
    String USER_BOOING_COUNT       = "user_booing_count";
    String USER_FOLLOWERS_COUNT    = "user_followers_count";
    String USER_FOLLOWING_COUNT    = "user_following_count";
    String USER_POSTS_COUNT        = "user_posts_count";
    String SIGNUP_MODE             = "signup_mode";
    String USER_FACEBOOK_ID        = "user_facebook_id";
    String USER_TWITTER_ID         = "user_twitter_id";
    String IS_FOLLOWED_BY_ME       = "is_followed_by_me";
    String USERS                   = "users";


    //    Response
    String MESSAGE = "message";
    String STATUS  = "status";

    int MAX_WIDTH  = 512;
    int MAX_HEIGHT = 512;

    int search_limit = 30;
    String TYPE = "type";
    String FROM = "from";
    String GCM_LISTENER = "GCM_Listener";
}
