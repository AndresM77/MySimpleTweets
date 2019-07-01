package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;

public class User {

    // list attributes
    public String name;
    public long uid;
    public String screenName;
    public String profileImageUrl;
    //deserialize JSON

    public static User fromJson(JSONObject json) throws JSONException {
        User user = new User();

        //extract and fill values
        user.name = json.getString("name");
        user.uid = json.getLong("id");
        user.screenName = json.getString("screen_name");
        user.profileImageUrl = json.getString("profile_image_url_https");

        return user;
    }
}
