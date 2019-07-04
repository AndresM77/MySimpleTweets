package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ReplyActivity extends AppCompatActivity {
    TwitterClient client;
    Tweet post;
    EditText tweetEdit;
    long uid;
    User user;
    private final int REQUEST_CODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        uid = (long) getIntent().getLongExtra("id", 0);
        user = Parcels.unwrap(getIntent().getParcelableExtra("user"));

        Button tweetBtn = findViewById(R.id.ivSendRply);
        client = TwitterApp.getRestClient(this);
        // get user text
        tweetEdit = findViewById(R.id.ivReplyTxt);
        tweetEdit.setText(String.format("@%s ",user.name));
        final TextView txtCtr = findViewById(R.id.ivTextCtr2);

        tweetEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Setting size of usrText string to value outputted by txtCtr
                txtCtr.setText(String.format("%d/280", tweetEdit.length()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });





        tweetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.replyTweet(tweetEdit.getText().toString(), uid, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            //This is where I want to return the information
                            post = Tweet.fromJSON(response);
                            Intent i = new Intent(ReplyActivity.this, TimelineActivity.class);
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
