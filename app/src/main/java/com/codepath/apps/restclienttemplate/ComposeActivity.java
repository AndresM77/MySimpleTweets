package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity implements Serializable {

    TwitterClient client;
    Tweet post;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        Button tweetBtn = findViewById(R.id.ivTweetBtn);
        client = TwitterApp.getRestClient(this);
        // get user text
        final TextView usrText = findViewById(R.id.ivEditText);

        tweetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.sendTweet(usrText.getText().toString(), new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            post = Tweet.fromJSON(response);
                            Intent i = new Intent(ComposeActivity.this, TimelineActivity.class);
                            i.putExtra("tweet", (Serializable) post);
                            startActivity(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }
                });
            }
        });
    }
}
