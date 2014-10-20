package limeandlemon.knowyourtimeline;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import twitter4j.IDs;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;
import util.InternalDBHandler;
import util.Preferencias;


public class Game extends Activity {

    static String TWITTER_CONSUMER_KEY = "o4YaT3H0SgmjQFSkGJy1A";
    static String TWITTER_CONSUMER_SECRET = "uxCIVsaPSsvckIBpSfZCLYGli0jHus4xMkE5sgk";
    FrameLayout pregunta;
    LinearLayout layoutRespuestas;
    List<twitter4j.Status> statuses;
    List<Long> friends;
    User respuesta;
    SQLiteDatabase db;
    ArrayList<User> respuestas;
    int aciertos, fallos;
    TextView txtPregunta;
    Twitter twitter;
    ProgressBar pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        db = new InternalDBHandler(this).getWritableDatabase();
        friends = new ArrayList<Long>();
        respuestas = new ArrayList<User>();
        pDialog = (ProgressBar) findViewById(R.id.progressBar);
        pregunta = (FrameLayout) findViewById(R.id.frameLayoutPregunta);
        layoutRespuestas = (LinearLayout) findViewById(R.id.linearRespuestas);
        final ImageView imgperfil = (ImageView) findViewById(R.id.iv_photoUser);
        txtPregunta = (TextView) findViewById(R.id.txtPregunta);
        imgperfil.setImageBitmap(decodeBase64(Preferencias.getPhoto(this)));
        FrameLayout frame1 = (FrameLayout) findViewById(R.id.frameLayout);
        FrameLayout frame2 = (FrameLayout) findViewById(R.id.frameLayout2);
        moveViewDown(frame1);
        moveViewDownSlow(frame2);
        moveViewDownSlow(imgperfil);
        getAciertosFallos();
        pregunta.setVisibility(View.GONE);
        new getTimeLine().execute();
    }

    private String seleccionarPregunta() {
        Random randomizer = new Random();
        int num = randomizer.nextInt(statuses.size());
        respuesta = statuses.get(num).getUser();
        respuestas.add(respuesta);
        return statuses.get(num).getText();
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void mostrarRespuestas() {
        Collections.shuffle(respuestas);
        for(int i = 0;i < respuestas.size();i++){
        final Button resp = new Button(this);
        resp.setText(respuestas.get(i).getName());
        resp.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        resp.setTextSize(24);
            resp.setBackground(getResources().getDrawable(R.drawable.selector_button));
            resp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(((Button)view).getText().toString().equals(respuesta.getName())){

                        updateAciertos(aciertos+1);
                        getAciertosFallos();
                        cambiarPuntuacion();
                        try {
                            mostrarOtraPregunta();

                        } catch (TwitterException e) {
                            e.printStackTrace();
                        }
                    }else {
                        updateFallos(fallos+1);
                        getAciertosFallos();
                        cambiarPuntuacion();
                        try {
                            mostrarOtraPregunta();
                        } catch (TwitterException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        layoutRespuestas.addView(resp);
            addDividier();
        }
    }

    private void cambiarPuntuacion() {
        TextView puntuacion = (TextView) findViewById(R.id.txtScore);
        if(fallos==0)  puntuacion.setText((aciertos/2)*100+"");
        if(aciertos==0) puntuacion.setText(0+"");
        else puntuacion.setText((aciertos/fallos)*100+"");
    }

    private void mostrarOtraPregunta() throws TwitterException {

        respuestas.clear();
        layoutRespuestas.removeAllViews();
        txtPregunta.setText(seleccionarPregunta());
        seleccionarRespuestaAzar();
    }
    private void seleccionarRespuestaAzar() throws TwitterException {
        Random randomizer = new Random();
        for(int i = 0;i<3;i++) {
            int num = randomizer.nextInt(friends.size());
            User user = twitter.showUser(friends.get(num));
            if(!respuestas.contains(user)){
                respuestas.add(user);
            }
        }
        mostrarRespuestas();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game, menu);
        return true;
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

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    private void moveViewDown(final View view) {
        Animation slide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        view.startAnimation(slide);
    }

    private void moveViewDownSlow(final View view) {
        Animation slide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down_slow);
        view.startAnimation(slide);
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
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute("");
            pDialog.setVisibility(View.GONE);
            txtPregunta.setText(seleccionarPregunta());
            new getFriendList().execute();
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
                twitter = new TwitterFactory(builder.build())
                        .getInstance(accessToken);


                //Twitter twitter = new TwitterFactory().getInstance();
                User user = twitter.verifyCredentials();
                Paging paging = new Paging();
                paging.setCount(100);
                statuses = twitter.getHomeTimeline(paging);
                for (Iterator<twitter4j.Status> iterator = statuses.iterator(); iterator.hasNext();) {
                    twitter4j.Status tweet = iterator.next();
                    if (tweet.isRetweet()) {
                        iterator.remove();
                    }
                }

            } catch (TwitterException e) {
                // Error in updating status
                Log.d("Twitter Update Error", e.getMessage());
            }
            return null;
        }

    }
    class getFriendList extends AsyncTask<Void, Void, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setIndeterminate(true);
            pDialog.setVisibility(View.VISIBLE);
        }
        protected void onPostExecute(String file_url) {
            super.onPostExecute("");
            pDialog.setVisibility(View.GONE);
            pregunta.setVisibility(View.VISIBLE);
            try {
                seleccionarRespuestaAzar();
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        }
        /**
         * getting Places JSON
         */
        protected String doInBackground(Void... args) {

            try {
                    long cursor = -1;
                    IDs ids;
                    System.out.println("Listing following ids.");
                    do {
                        ids = twitter.getFriendsIDs(cursor);
                        for (long id : ids.getIDs()) {
                            if(id!=-1)
                            friends.add(id);
                        }
                    } while ((cursor = ids.getNextCursor()) != 0);
                } catch (TwitterException te) {
                    te.printStackTrace();
                    System.out.println("Failed to get friends' ids: " + te.getMessage());
                }
            return null;
        }

    }
    private void addDividier() {
        ImageView v = new ImageView(this);
        v.setImageResource(R.drawable.divisor);
        layoutRespuestas.addView(v);
    }
    public void updateAciertos(int aciertos) {
        db.execSQL("UPDATE Puntuacion SET Aciertos="+aciertos+";");
    }
    public void updateFallos(int fallos) {
        db.execSQL("UPDATE Puntuacion SET Fallos="+fallos+";");
    }
    public void getAciertosFallos() {
        String sql = "SELECT * FROM Puntuacion;";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            int aciertos = cursor.getInt(cursor.getColumnIndex("Aciertos"));
            this.aciertos = aciertos;

            int fallos = cursor.getInt(cursor.getColumnIndex("Fallos"));
            this.fallos = fallos;
            cursor.close();
        }
        cursor.close();
    }
}
