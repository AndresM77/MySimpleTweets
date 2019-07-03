package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    TwitterClient client;
    Tweet post;
    private final int REQUEST_CODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        Button tweetBtn = findViewById(R.id.ivTweetBtn);
        client = TwitterApp.getRestClient(this);
        // get user text
        final TextView usrText = findViewById(R.id.ivEditText);
        final TextView txtCtr = findViewById(R.id.ivTextCtr);

        //Creating new thread so items are updated every second
        Thread t = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Setting size of usrText string to value outputted by txtCtr
                                txtCtr.setText(String.format("%d/280", usrText.length()));
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();

        tweetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.sendTweet(usrText.getText().toString(), new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            //This is where I want to return the information
                            post = Tweet.fromJSON(response);
                            Intent i = new Intent(ComposeActivity.this, TimelineActivity.class);
                            i.putExtra("tweet", Parcels.wrap(post));
                            setResult(REQUEST_CODE, i);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d("Compose", errorResponse.toString());
                    }
                });
            }
        });
    }
}
