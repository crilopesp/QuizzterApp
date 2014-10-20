package limeandlemon.knowyourtimeline;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;
import util.Preferencias;


public class Profile extends Activity {
    static String TWITTER_CONSUMER_KEY = "o4YaT3H0SgmjQFSkGJy1A";
    static String TWITTER_CONSUMER_SECRET = "uxCIVsaPSsvckIBpSfZCLYGli0jHus4xMkE5sgk";
    ImageView imgperfil;
    ImageView imgback;
    // Preference Constants
    static String PREFERENCE_NAME = "twitter_oauth";
    // Shared Preferences
    private static SharedPreferences mSharedPreferences;

    @Override
    public void onResume(){
        super.onResume();
        onCreate(null);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        final ImageView imgperfil = (ImageView) findViewById(R.id.iv_photoUser);
        final ImageView imgback = (ImageView) findViewById(R.id.imgBack);
        final Button btnjugar = (Button) findViewById(R.id.bntJugar);
        imgperfil.setImageBitmap(decodeBase64(Preferencias.getPhoto(this)));
        imgback.setImageBitmap(decodeBase64(Preferencias.getBanner(this)));

        moveViewDown(imgperfil);
        moveViewDown(imgback);
        btnjugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(),Game.class);
                startActivity(intent);
            }
        });
        Button btnclasificacion = (Button) findViewById(R.id.btnClasificacion);
        Button btnopciones = (Button) findViewById(R.id.btnOpciones);
        Button btnsalir = (Button) findViewById(R.id.btnSalir);

        Button btnlogout = (Button) findViewById(R.id.btnLogout);


        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutFromTwitter();
            }
        });
        int color = Preferencias.getProfileColor(this);
        String fontPath = "fonts/Cicle Semi.ttf";
        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
        btnjugar.setTypeface(tf);
        btnjugar.setTextColor(color);
        btnclasificacion.setTypeface(tf);
        btnclasificacion.setTextColor(color);
        btnopciones.setTypeface(tf);
        btnopciones.setTextColor(color);
        btnsalir.setTypeface(tf);
        btnsalir.setTextColor(color);
        btnsalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    this.finalize();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    private void logoutFromTwitter() {
        // Clear the shared preferences
        Preferencias.setOauthSecret(this,"");

        Preferencias.setOauthToken(this,"");

        Preferencias.setLogged(this, false);
        Preferencias.setBanner(this,"");
        Preferencias.setBannerURL(this,"");
        Preferencias.setPhotoURL(this,"");
        Preferencias.setPhoto(this,"");

        Intent intent = new Intent(this,Main.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setActivityBackgroundColor(int color) {
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(color);
    }


    private void moveViewUp( final View view )
    {
        Animation slide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);

        view.startAnimation(slide);

    }
    private void moveViewDown( final View view )
    {
        Animation slide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        view.startAnimation(slide);
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}
