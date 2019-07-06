package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    private List<Tweet> mTweets;
    private Context context;
    TwitterClient client;
    //pass in tweets array to constructor
    public TweetAdapter(List<Tweet> tweets) {
        mTweets = tweets;
    }

    //for each row, inflate the layout and cache references

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View tweetView = inflater.inflate(R.layout.item_tweet, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    //bind values based on position of element

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        //get data according to position
        final Tweet tweet = mTweets.get(position);

        //populate views according to data
        viewHolder.tvUsername.setText(tweet.user.name);
        viewHolder.tvBody.setText(tweet.body);
        viewHolder.tvTimeStamp.setText(getRelativeTimeAgo(tweet.createdAt));
        viewHolder.tvRetweetCnt.setText(String.valueOf(tweet.retweetCount));
        viewHolder.tvLikeCnt.setText(String.valueOf(tweet.favCount));
        viewHolder.ibReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ReplyActivity.class);
                i.putExtra("user", Parcels.wrap(tweet.user));
                i.putExtra("id",tweet.uid);
                context.startActivity(i);
            }
        });
        if(tweet.isRetweeted) {
            viewHolder.ibRetweet.setImageResource(R.drawable.ic_retweet_pressed);
        }
        if(tweet.isFavorited) {
            viewHolder.ibLike.setImageResource(R.drawable.ic_heart_pressed);
        }
        viewHolder.ibRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Retweet functionality
                client = TwitterApp.getRestClient(context);
                retweet(tweet, viewHolder);
            }
        });
        viewHolder.ibLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Like functionality
                client = TwitterApp.getRestClient(context);
                favorite(tweet, viewHolder);
            }
        });
        /*
        //Setting up detail view
        viewHolder.rlTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change view to detail view
                Intent i = new Intent(context, DetailView.class);
                i.putExtra("tweet", Parcels.wrap(tweet));
                context.startActivity(i);
            }
        });
        */

        Glide.with(context)
                .load(tweet.user.profileImageUrl)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(14)))
                .into(viewHolder.ivProfileImage);
        if (tweet.mediaUrl != null) {
            Glide.with(context)
                    .load(tweet.mediaUrl)
                    .into(viewHolder.ivBodyImage);
        }
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    //create viewholder class

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivProfileImage;
        public ImageView ivBodyImage;
        public TextView tvUsername;
        public TextView tvBody;
        public TextView tvTimeStamp;
        public TextView tvRetweetCnt;
        public TextView tvLikeCnt;
        public ImageButton ibReply;
        public ImageButton ibRetweet;
        public ImageButton ibLike;
        public RelativeLayout rlTweet;


        public ViewHolder(final View itemView) {
            super(itemView);

            // perform findViewById lookups

            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvTimeStamp = (TextView) itemView.findViewById(R.id.tvTimeStamp);
            ibReply = (ImageButton) itemView.findViewById(R.id.ivRplyBtn);
            ibRetweet = (ImageButton) itemView.findViewById(R.id.ivReTweet);
            ibLike = (ImageButton) itemView.findViewById(R.id.ivLike);
            rlTweet = (RelativeLayout) itemView.findViewById(R.id.ivTweet);
            tvRetweetCnt = (TextView) itemView.findViewById(R.id.tvRetweetCount);
            tvLikeCnt = (TextView) itemView.findViewById(R.id.tvLikeCount);
            ivBodyImage = (ImageView) itemView.findViewById(R.id.ivBodyImage);


        }
    }

    // Clean all elements of the recycler
    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        mTweets.addAll(list);
        notifyDataSetChanged();
    }

    public void favorite(final Tweet tweet, final ViewHolder itemView){
        if (tweet.isFavorited) {
            client.unFavTweet(tweet.uid, new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    itemView.ibLike.setImageResource(R.drawable.ic_heart_unpressed);
                    super.onSuccess(statusCode, headers, response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                }
            });
        } else {
            client.favTweet(tweet.uid, new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    itemView.ibLike.setImageResource(R.drawable.ic_heart_pressed);
                    super.onSuccess(statusCode, headers, response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                }
            });
        }

    }
    public void retweet(Tweet tweet, final ViewHolder itemView) {
        if (tweet.isRetweeted) {
            client.unReTweet(tweet.uid, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    itemView.ibRetweet.setImageResource(R.drawable.ic_retweet_unpressed);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                }
            });
        } else {
            client.reTweet(tweet.uid, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    itemView.ibRetweet.setImageResource(R.drawable.ic_retweet_pressed);
                    super.onSuccess(statusCode, headers, response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.d("RETWEET","CANNOT RETWEET WRONG API");
                    super.onFailure(statusCode, headers, responseString, throwable);
                }
            });
        }
    }
}
