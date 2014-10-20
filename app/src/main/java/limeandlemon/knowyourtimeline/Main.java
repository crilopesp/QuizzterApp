package limeandlemon.knowyourtimeline;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Activity;
import android.os.StrictMode;
import android.util.Base64;
import android.view.Menu;

import twitter4j.Paging;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import util.Preferencias;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity {
    // Constants
    /**
     * Register your here app https://dev.twitter.com/apps/new and get your
     * consumer key and secret
     * */
    static String TWITTER_CONSUMER_KEY = "o4YaT3H0SgmjQFSkGJy1A";
    static String TWITTER_CONSUMER_SECRET = "uxCIVsaPSsvckIBpSfZCLYGli0jHus4xMkE5sgk";

    // Preference Constants
    static String PREFERENCE_NAME = "twitter_oauth";
    static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";

    static final String TWITTER_CALLBACK_URL = "oauth://t4jsample";

    // Twitter oauth urls
    static final String URL_TWITTER_AUTH = "auth_url";
    static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";

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

    // Twitter
    private static Twitter twitter;
    private static RequestToken requestToken;


    // Internet Connection detector
    private ConnectionDetector cd;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if(Preferencias.getLogged(this)==true){

                Intent intent = new Intent(getApplicationContext(),Profile.class);
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
        // Check if twitter keys are set
        if (TWITTER_CONSUMER_KEY.trim().length() == 0
                || TWITTER_CONSUMER_SECRET.trim().length() == 0) {
            // Internet Connection is not present
            alert.showAlertDialog(Main.this, "Twitter oAuth tokens",
                    "Please set your twitter oauth tokens first!", false);
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
                loginToTwitter();


            }
        });


        /**
         * Button click event for logout from twitter
         *
        btnLogoutTwitter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Call logout twitter function
                logoutFromTwitter();
            }
        });
         */
        if (!isTwitterLoggedInAlready()) {
            Uri uri = getIntent().getData();
            if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
                // oAuth verifier
                String verifier = uri
                        .getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);

                try {
                    // Get the access token


                    AccessToken accessToken = twitter.getOAuthAccessToken(
                            requestToken, verifier);
                    Preferencias.setOauthSecret(this, accessToken.getTokenSecret());

                    Preferencias.setOauthToken(this, accessToken.getToken());

                    Preferencias.setLogged(this, true);

                    if(Preferencias.getLogged(getApplicationContext())){
                        descargarFotos();
                        Intent intent = new Intent(getApplicationContext(),Profile.class);
                        startActivity(intent);
                    }
                } catch (Exception ex) {
                    // Check log for login errors
                    Log.e("Twitter Login Error", "> " + ex.getMessage());
                }
            }
        }
        else {
            // Show Update Twitter
            Intent intent = new Intent(this,Profile.class);
            startActivity(intent);
        }

    }

    private void descargarFotos() {
        String urlfoto = "";
        String urlfondo = "";
        String nombreuser = "";
        // Update status
        try {
            User user = twitter.showUser(twitter.getId());
            urlfoto = user.getOriginalProfileImageURL();
            urlfondo = user.getProfileBannerURL();
            String color = user.getProfileLinkColor();
            if(color.equals("FFFFFF")) color = "111111";
            Preferencias.setProfileColor(this,Color.parseColor("#" + color));
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        if(urlfoto!="")
            try {
                if(!urlfoto.equals(Preferencias.getPhotoURL(this))){
                Bitmap profilepic = new DownloadImageTask((ImageView) findViewById(R.id.iv_photoUser))
                        .execute(urlfoto).get();
                Preferencias.setPhoto(this,encodeTobase64(profilepic));
                Preferencias.setPhotoURL(this,urlfoto);}

                if(!urlfondo.equals(Preferencias.getBannerURL(this))){
                    Bitmap profilebanner = new DownloadImageTask((ImageView) findViewById(R.id.imgBack))
                            .execute(urlfondo).get();
                    Preferencias.setBanner(this, encodeTobase64(profilebanner));
                    Preferencias.setBannerURL(this,urlfondo);}
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
/*        TextView txtuser = (TextView) findViewById(R.id.txtUser);
        txtuser.setText(nombreuser);*/
    }

    /**
     * Function to login twitter
     * */
    private void loginToTwitter() {
        // Check if already logged in
        if (!isTwitterLoggedInAlready()) {
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
            builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
            Configuration configuration = builder.build();

            TwitterFactory factory = new TwitterFactory(configuration);
            twitter = factory.getInstance();

            try {
                requestToken = twitter
                        .getOAuthRequestToken(TWITTER_CALLBACK_URL);
                this.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                        .parse(requestToken.getAuthenticationURL())));
            } catch (TwitterException e) {
                e.printStackTrace();
            }

        } else {
            // user already logged into twitter
            Toast.makeText(getApplicationContext(),
                    "Already Logged into twitter", Toast.LENGTH_LONG).show();
        }
    }

    /*
     * Function to update status
     * */
    class updateTwitterStatus extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
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
         * */
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
         * **/
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
     * Function to logout from twitter It will just clear the application shared
     * preferences
     * */


    /**
     * Check user already logged in your application using twitter Login flag is
     * fetched from Shared Preferences
     * */
    private boolean isTwitterLoggedInAlready() {
        // return twitter login status from Shared Preferences
        return Preferencias.getLogged(this);
    }

    protected void onResume() {
        super.onResume();
    }

    public class LoginTask extends AsyncTask<Void, Void, RequestToken> {

        private ProgressDialog progressDialog;

        public LoginTask() {
            progressDialog = ProgressDialog.show(Main.this, "", "Loading. Please wait...", false);
        }
        @Override
        protected RequestToken doInBackground(Void... params) {
            // TODO Auto-generated method stub
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
            builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
            Configuration configuration = builder.build();

            TwitterFactory factory = new TwitterFactory(configuration);
            twitter = factory.getInstance();

            try {
                return requestToken = twitter
                        .getOAuthRequestToken(TWITTER_CALLBACK_URL);
            } catch (TwitterException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            Main.this.setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected void onPostExecute(RequestToken result) {
            // TODO Auto-generated method stub
            Main.this.setProgressBarIndeterminateVisibility(false);
            progressDialog.dismiss();
            try {
                requestToken = result;
                Main.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                        .parse(requestToken.getAuthenticationURL())));
            } catch(Exception e) {
                e.printStackTrace();
                alert.showAlertDialog(Main.this, "Internet Connection Timeout Error",
                        "Please try later.", false);
            }
        }

    }
    /**
     * Function to get timeline
     * */
    class getTimeLine extends AsyncTask<Void, Void, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
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
         * */
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
                    Log.d("Twitter","@" + status.getUser().getScreenName()
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

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            //bmImage.setImageBitmap(result);
        }
    }


    public static String encodeTobase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        Log.d("Image Log:", imageEncoded);
        return imageEncoded;
    }
}