package com.sproutonecard.rechargeandreward.api;

public interface HttpUrlManager {
    String API_KEY = "03c48c1b03bde4946f63150e8c0ebc64";
    String HPSTOKENSERVIC_EPUBLICKEY = "pkapi_cert_xXgWdXg6pp9Td1aryg";
    String CONTENT_TYPE = "application/json";
    String ACCEPT = "application/json; version=1.0";
    String POST = "POST";
    String GET = "GET";
    String PATCH = "PATCH";
    String DELETE = "DELETE";

//  Base API urls
    String URL_SERVER        = "https://rechargeandreward-cert.bytevampire.com";
    String URL_MEDIA_USERS       = URL_SERVER + "/assets/media/users/";
    String URL_MEDIA_PHOTOS      = URL_SERVER + "/assets/media/post_photos/";
    String URL_MEDIA_VIDEOS      = URL_SERVER + "/assets/media/post_videos/";
    String URL_MEDIA_VIDEO_THUMB = URL_SERVER + "/assets/media/post_video_thumbs/";


//    API URLs
    String URL_SOCIAL_SIGNUP        = URL_SERVER + "/api/user_social_signup";


    String URL_MANUAL_LOGIN         = URL_SERVER + "/login/";
    String URL_MANUAL_SIGNUP        = URL_SERVER + "/api/user_manual_signup";

    String URL_MANAGE_MY_POSTS      = URL_SERVER + "/api/manage_my_posts";
    String URL_BOO_POST             = URL_SERVER + "/api/boo_post";
    String URL_COMMENT_POST         = URL_SERVER + "/api/comment_post";
    String URL_ACTIVITIES           = URL_SERVER + "/api/activities";
    String URL_ACTIVITY_DONE        = "/api/activity_done";
    String URL_USER_CHANGE_PASSWORD = URL_SERVER + "/api/user_change_password";
    String URL_FOLLOW_USER          = URL_SERVER + "/api/follow_user";
    String URL_USER_PROFILE_UPDATE  = URL_SERVER + "/api/user_profile_update";
    String URL_USER_PROFILE_PHOTO_UPDATE  = URL_SERVER + "/api/user_profile_photo_update";
    String URL_GET_FEED_POSTS       = URL_SERVER + "/api/get_feed_posts";
    String URL_GET_POST             = URL_SERVER + "/api/get_post";
    String URL_GET_NEARBY_POSTS     = "/api/get_nearby_posts";
    String URL_GET_GLOBAL_POSTS     = "/api/get_global_posts";
    String URL_GET_PROFILE          = URL_SERVER + "/api/get_profile";
    String URL_GET_USER_POSTS       = URL_SERVER + "/api/get_user_posts";
    String URL_GET_FOLLOWING        = URL_SERVER + "/api/get_following";
    String URL_GET_FOLLOWERS        = URL_SERVER + "/api/get_followers";
    String URL_GET_BOOERS           = "/api/get_booers";
    String URL_GET_TAGGED_USERS     = "/api/get_tagged_users";
    String URL_GET_TAGGED_POSTS     = URL_SERVER + "/api/get_tagged_posts";
    String URL_GET_USER_BY_NAME     = URL_SERVER + "/api/get_user_by_name";
    String URL_SEND_EMAIL           = URL_SERVER + "/api/send_email";
}
