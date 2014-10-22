package limeandlemon.knowyourtimeline;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import util.Constants;
import util.Preferencias;

public class TwitterLoginActivity extends Activity {

    public static final int TWITTER_LOGIN_RESULT_CODE_SUCCESS = 1;
    public static final int TWITTER_LOGIN_RESULT_CODE_FAILURE = 2;

    static String TWITTER_CONSUMER_KEY = "o4YaT3H0SgmjQFSkGJy1A";
    static String TWITTER_CONSUMER_SECRET = "uxCIVsaPSsvckIBpSfZCLYGli0jHus4xMkE5sgk";

    private WebView twitterLoginWebView;
    private ProgressDialog mProgressDialog;
    public String twitterConsumerKey;
    public String twitterConsumerSecret;

    private static Twitter twitter;
    private static RequestToken requestToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter_login);

        twitterConsumerKey = TWITTER_CONSUMER_KEY;
        twitterConsumerSecret = TWITTER_CONSUMER_SECRET;
        if (twitterConsumerKey == null || twitterConsumerSecret == null) {
            TwitterLoginActivity.this.setResult(TWITTER_LOGIN_RESULT_CODE_FAILURE);
            TwitterLoginActivity.this.finish();
        }

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        twitterLoginWebView = (WebView) findViewById(R.id.twitter_login_web_view);
        twitterLoginWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains(Constants.TWITTER_CALLBACK_URL)) {
                    Uri uri = Uri.parse(url);
                    TwitterLoginActivity.this.saveAccessTokenAndFinish(uri);
                    return true;
                }
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO Auto-generated method stub
                super.onPageFinished(view, url);
                if (mProgressDialog != null) mProgressDialog.dismiss();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO Auto-generated method stub
                super.onPageStarted(view, url, favicon);

                if (mProgressDialog != null) mProgressDialog.show();
            }
        });


        askOAuth();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        if (mProgressDialog != null) mProgressDialog.dismiss();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    private void saveAccessTokenAndFinish(final Uri uri) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                String verifier = uri.getQueryParameter(Constants.IEXTRA_OAUTH_VERIFIER);
                try {
                    AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
                    Preferencias.setOauthToken(getApplicationContext(), accessToken.getToken());
                    Preferencias.setOauthSecret(getApplicationContext(), accessToken.getTokenSecret());
                    Log.d(Constants.TAG, "TWITTER LOGIN SUCCESS!!!");
                    descargarFotos(twitter);
                    TwitterLoginActivity.this.setResult(TWITTER_LOGIN_RESULT_CODE_SUCCESS);
                } catch (Exception e) {
                    e.printStackTrace();
                    if (e.getMessage() != null) Log.e(Constants.TAG, e.getMessage());
                    else Log.e(Constants.TAG, "ERROR: Twitter callback failed");
                    TwitterLoginActivity.this.setResult(TWITTER_LOGIN_RESULT_CODE_FAILURE);
                }
                TwitterLoginActivity.this.finish();
            }
        }).start();
    }

    //====== TWITTER HELPER METHODS ======

    public static boolean isConnected(Context ctx) {
        SharedPreferences sharedPrefs = ctx.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPrefs.getString(Constants.PREF_KEY_TOKEN, null) != null;
    }

    public static void logOutOfTwitter(Context ctx) {
        SharedPreferences sharedPrefs = ctx.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        Editor e = sharedPrefs.edit();
        e.putString(Constants.PREF_KEY_TOKEN, null);
        e.putString(Constants.PREF_KEY_SECRET, null);
        e.commit();
    }

    public static String getAccessToken(Context ctx) {
        return Preferencias.getOauthToken(ctx);
    }

    public static String getAccessTokenSecret(Context ctx) {
        return Preferencias.getOauthSecret(ctx);
    }

    private void askOAuth() {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setOAuthConsumerKey(twitterConsumerKey);
        configurationBuilder.setOAuthConsumerSecret(twitterConsumerSecret);
        Configuration configuration = configurationBuilder.build();
        twitter = new TwitterFactory(configuration).getInstance();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    requestToken = twitter.getOAuthRequestToken(Constants.TWITTER_CALLBACK_URL);
                } catch (Exception e) {
                    final String errorString = e.toString();
                    TwitterLoginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressDialog.cancel();
                            Toast.makeText(TwitterLoginActivity.this, errorString.toString(), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                    e.printStackTrace();
                    return;
                }

                TwitterLoginActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(Constants.TAG, "LOADING AUTH URL");
                        twitterLoginWebView.loadUrl(requestToken.getAuthenticationURL());
                    }
                });
            }
        }).start();
    }


    private void descargarFotos(Twitter twitter) {
        String urlfoto = "";
        String nombreuser = "";
        // Update status
        try {
            User user = twitter.showUser(twitter.getId());
            urlfoto = user.getOriginalProfileImageURL();
            String color = user.getProfileLinkColor();
            if (color.equals("FFFFFF")) color = "111111";
            Preferencias.setProfileColor(this, Color.parseColor("#" + color));
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        if (urlfoto != "")
            try {
                if (!urlfoto.equals(Preferencias.getPhotoURL(this))) {
                    Bitmap profilepic = new DownloadImageTask((ImageView) findViewById(R.id.iv_photoUser))
                            .execute(urlfoto).get();
                    Preferencias.setPhoto(this, encodeTobase64(profilepic));
                    Preferencias.setPhotoURL(this, urlfoto);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
/*        TextView txtuser = (TextView) findViewById(R.id.txtUser);
        txtuser.setText(nombreuser);*/
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