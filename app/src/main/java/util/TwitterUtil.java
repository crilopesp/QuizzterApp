package util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by marcos on 28/10/2014.
 */
public class TwitterUtil {

    public final static String TWITTER_CONSUMER_KEY = "o4YaT3H0SgmjQFSkGJy1A";
    public final static String TWITTER_CONSUMER_SECRET = "uxCIVsaPSsvckIBpSfZCLYGli0jHus4xMkE5sgk";

    public static Twitter getTwitter(Context _context){
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
        builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);

        String access_token = Preferencias.getOauthToken(_context);
        String access_token_secret = Preferencias.getOauthSecret(_context);

        AccessToken accessToken = new AccessToken(access_token,access_token_secret);
        return new TwitterFactory(builder.build()).getInstance(accessToken);
    }

    public static void descargarFotos(Context _context, long userid, CircularImageView iv) {
        try {
            Twitter twitter = getTwitter(_context);
            User user = twitter.showUser(userid);
            String urlfoto = user.getOriginalProfileImageURL();
            if (urlfoto != "") new DownloadImageTask(iv).execute(urlfoto);
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    public static void descargarFotoMenorCalidad(Context _context, long userid, CircularImageView iv) {
        try {
            Twitter twitter = getTwitter(_context);
            User user = twitter.showUser(userid);
            String urlfoto = user.getMiniProfileImageURL();
            if (urlfoto != "") new DownloadImageTask(iv).execute(urlfoto);
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        CircularImageView bmImage;

        public DownloadImageTask(CircularImageView bmImage) {
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
            bmImage.setImageBitmap(result);
        }
    }


}
