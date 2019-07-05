package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class Tweet {

    // list attributes
    public String body;
    public long uid; //databaseId for tweet
    public User user;
    public String createdAt;
    public boolean isFavorited;
    public boolean isRetweeted;
    public int favCount;
    public int retweetCount;
    public int replyCount;

    public Tweet(){

    }

    public static Tweet fromJSON (JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();


        //extract values from JSON
        tweet.body = jsonObject.getString("text");
        tweet.uid = jsonObject.getLong("id");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.isFavorited = jsonObject.getBoolean("favorited");
        tweet.favCount = jsonObject.getInt("favorite_count");
        tweet.isRetweeted = jsonObject.getBoolean("retweeted");
        tweet.retweetCount = jsonObject.getInt("retweet_count");
        //object only available with prenium and enterprise products
        //tweet.replyCount = jsonObject.getInt("reply_count");


        return tweet;
    }
}
