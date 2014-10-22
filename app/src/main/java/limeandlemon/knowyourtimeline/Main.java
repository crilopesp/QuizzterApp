package limeandlemon.knowyourtimeline;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import twitter4j.Paging;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;
import util.Preferencias;

public class Main extends Activity {
    // Constants
    static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";
    static final String TWITTER_CALLBACK_URL = "oauth://t4jsample";
    // Twitter oauth urls
    static final String URL_TWITTER_AUTH = "auth_url";
    static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";

    static String TWITTER_CONSUMER_KEY = "o4YaT3H0SgmjQFSkGJy1A";
    static String TWITTER_CONSUMER_SECRET = "uxCIVsaPSsvckIBpSfZCLYGli0jHus4xMkE5sgk";

    public static final int TWITTER_LOGIN_RESULT_CODE_SUCCESS = 1;
    public static final int TWITTER_LOGIN_RESULT_CODE_FAILURE = 2;
    /**
     * Register your here app https://dev.twitter.com/apps/new and get your
     * consumer key and secret
     */
    // Preference Constants
    static String PREFERENCE_NAME = "twitter_oauth";
    // Twitter
    private static Twitter twitter;
    private static RequestToken requestToken;
    // Login button
    Button btnLoginTwitter;
    // Update status button
    Button btnUpdateStatus;
    Button btnGetTimeLine;
    // Logout button
    Button btnLogoutTwitter;
    // EditText for update
    EditText txtUpdate;
    // lbl update
    TextView lblUpdate;
    TextView lblUserName;
    // Progress dialog
    ProgressDialog pDialog;
    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();
    // Internet Connection detector
    private ConnectionDetector cd;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (Preferencias.getLogged(this) == true) {
            Intent intent = new Intent(getApplicationContext(), Profile.class);
            startActivity(intent);
            this.finish();

        }
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        cd = new ConnectionDetector(getApplicationContext());

        // Check if Internet present
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            alert.showAlertDialog(Main.this,
                    "Internet Connection Error",
                    "Please connect to working Internet connection", false);
            // stop executing code by return
            return;
        }
        btnLoginTwitter = (Button) findViewById(R.id.button);


        /**
         * Twitter login button click event will call loginToTwitter() function
         * */
        btnLoginTwitter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Call login twitter function
                Intent i = new Intent(getApplicationContext(), TwitterLoginActivity.class);
                startActivityForResult(i, 1);


            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == TWITTER_LOGIN_RESULT_CODE_SUCCESS) {

                Intent intent = new Intent(getApplicationContext(), Profile.class);
                startActivity(intent);
                Preferencias.setLogged(this, true);
                this.finish();
            }
            if (resultCode == TWITTER_LOGIN_RESULT_CODE_FAILURE) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    protected void onResume() {
        super.onResume();
    }

    /*
     * Function to update status
     * */
    class updateTwitterStatus extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Main.this);
            pDialog.setMessage("Updating to twitter...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting Places JSON
         */
        protected String doInBackground(String... args) {
            Log.d("Tweet Text", "> " + args[0]);
            String status = args[0];
            try {
                ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
                builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);

                // Access Token
                String access_token = Preferencias.getOauthToken(getApplicationContext());
                String access_token_secret = Preferencias.getOauthSecret(getApplicationContext());

                AccessToken accessToken = new AccessToken(access_token,
                        access_token_secret);
                Twitter twitter = new TwitterFactory(builder.build())
                        .getInstance(accessToken);

                // Update status
                twitter4j.Status response = twitter.updateStatus(status);

                Log.d("Status", "> " + response.getText());
            } catch (TwitterException e) {
                // Error in updating status
                Log.d("Twitter Update Error", e.getMessage());
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog and show
         * the data in UI Always use runOnUiThread(new Runnable()) to update UI
         * from background thread, otherwise you will get error
         * *
         */
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "Status tweeted successfully", Toast.LENGTH_SHORT)
                            .show();
                    // Clearing EditText field
                    txtUpdate.setText("");
                }
            });
        }

    }

    /**
     * Function to get timeline
     */
    class getTimeLine extends AsyncTask<Void, Void, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Main.this);
            pDialog.setMessage("Getting Timeline from Twitter...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting Places JSON
         */
        protected String doInBackground(Void... args) {

            try {
                ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
                builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);

                String access_token = Preferencias.getOauthToken(getApplicationContext());
                String access_token_secret = Preferencias.getOauthSecret(getApplicationContext());

                AccessToken accessToken = new AccessToken(access_token,
                        access_token_secret);
                Twitter twitter = new TwitterFactory(builder.build())
                        .getInstance(accessToken);


                //Twitter twitter = new TwitterFactory().getInstance();
                User user = twitter.verifyCredentials();
                Paging paging = new Paging();
                paging.setCount(100);
                List<twitter4j.Status> statuses = twitter.getHomeTimeline(paging);
                System.out.println("Showing @" + user.getScreenName()
                        + "'s home timeline.");
                for (twitter4j.Status status : statuses) {
                    Log.d("Twitter", "@" + status.getUser().getScreenName()
                            + " - " + status.getText());
                }

            } catch (TwitterException e) {
                // Error in updating status
                Log.d("Twitter Update Error", e.getMessage());
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "Retrieving TimeLine Done..", Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }

    }


}