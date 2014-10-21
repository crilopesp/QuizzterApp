package limeandlemon.knowyourtimeline;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.IDs;
import twitter4j.Paging;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;
import util.InternalDBHandler;
import util.Preferencias;
import util.TextViewEx;


public class Game extends Activity {

    static String TWITTER_CONSUMER_KEY = "o4YaT3H0SgmjQFSkGJy1A";
    static String TWITTER_CONSUMER_SECRET = "uxCIVsaPSsvckIBpSfZCLYGli0jHus4xMkE5sgk";
    RelativeLayout pregunta, answers;
    LinearLayout layoutRespuestas;
    List<twitter4j.Status> statuses;
    List<Long> friends;
    User respuesta;
    SQLiteDatabase db;
    int preguntaLength;
    ArrayList<User> respuestas;
    int aciertos, fallos;
    TextViewEx txtPregunta;
    Typeface tf;
    Animation left_to_right_animation, right_to_left_animation, scale;
    Twitter twitter;
    ProgressBar pDialog;

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        db = new InternalDBHandler(this).getWritableDatabase();
        left_to_right_animation = AnimationUtils.loadAnimation(this, R.anim.left_to_right);
        right_to_left_animation = AnimationUtils.loadAnimation(this, R.anim.right_to_left);
        scale = AnimationUtils.loadAnimation(this, R.anim.scale);


        String fontPath = "fonts/Cicle Semi.ttf";
        tf = Typeface.createFromAsset(getAssets(), fontPath);
        if (Preferencias.getFirstHome(this) == 1) {
            String sql = "INSERT OR IGNORE INTO \"main\".\"Puntuacion\" (\"Aciertos\",\"Fallos\") VALUES (0,0);";
            db.execSQL(sql);
            Preferencias.setFirstHome(this, 0);
        }

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        friends = new ArrayList<Long>();
        respuestas = new ArrayList<User>();
        pDialog = (ProgressBar) findViewById(R.id.progressBar);
        pregunta = (RelativeLayout) findViewById(R.id.relativePregunta);
        answers = (RelativeLayout) findViewById(R.id.relativeLayout);
        layoutRespuestas = (LinearLayout) findViewById(R.id.linearRespuestas);
        final ImageView imgperfil = (ImageView) findViewById(R.id.iv_photoUser);
        txtPregunta = (TextViewEx) findViewById(R.id.txtPregunta);

        txtPregunta.setBackgroundColor(getResources().getColor(R.color.text));
        txtPregunta.setMovementMethod(new ScrollingMovementMethod());
        txtPregunta.setTypeface(tf);
        imgperfil.setImageBitmap(decodeBase64(Preferencias.getPhoto(this)));
        RelativeLayout frame1 = (RelativeLayout) findViewById(R.id.frameLayout);
        FrameLayout frame2 = (FrameLayout) findViewById(R.id.frameLayout2);
        moveViewDown(frame1);
        moveViewDownSlow(frame2);
        moveViewDownSlow(imgperfil);
        getAciertosFallos();
        cambiarPuntuacion();
        pregunta.setVisibility(View.GONE);
        new getTimeLine().execute();
    }

    private String seleccionarPregunta() {
        while (!statuses.isEmpty()) {
            Random randomizer = new Random();
            int num = randomizer.nextInt(statuses.size());
            respuesta = statuses.get(num).getUser();
            if (comprobarRespondido(statuses.get(num).getId())) statuses.remove(num);
            else {
                respuestas.add(respuesta);
                registrarPregunta(statuses.get(num).getId());
                preguntaLength = statuses.get(num).getText().length();
                return removeUrl(statuses.get(num).getText());
            }
        }
        this.finish();
        return "Ya no quedan más tweets...";
    }

    private void registrarPregunta(long id) {
        String sql = "INSERT OR IGNORE INTO \"main\".\"Preguntas\" (\"id\") VALUES (" + id + ");";
        db.execSQL(sql);
    }

    private boolean comprobarRespondido(long id) {
        String sql = "SELECT id FROM Preguntas where id=" + id + ";";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() == 1) {
            cursor.close();

            return true;
        }
        cursor.close();

        return false;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void mostrarRespuestas() {
        Collections.shuffle(respuestas);

        for (int i = 0; i < respuestas.size(); i++) {
            final Button resp = new Button(this);
            resp.setText(respuestas.get(i).getName());
            resp.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            resp.setTypeface(tf);
            resp.setTextSize(20);
            resp.setBackground(getResources().getDrawable(R.drawable.selector_button));
            resp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    layoutRespuestas.removeAllViews();
                    if (((Button) view).getText().toString().equals(respuesta.getName())) {
                        aciertos = aciertos + 1;
                        updateAciertos(aciertos);
                        cambiarPuntuacion();
                        pregunta.startAnimation(right_to_left_animation);

                        right_to_left_animation.setAnimationListener(new Animation.AnimationListener() {

                            @Override
                            public void onAnimationStart(Animation animation) {
                                // TODO Auto-generated method stub
                                txtPregunta.setBackgroundColor(getResources().getColor(R.color.acierto));
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                                // TODO Auto-generated method stub

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                try {

                                    moveViewDownQuick(pregunta);
                                    txtPregunta.setBackgroundColor(getResources().getColor(R.color.text));
                                    mostrarOtraPregunta();

                                } catch (TwitterException e) {
                                    e.printStackTrace();
                                }
                            }
                        });


                    } else {

                        fallos = fallos + 1;
                        updateFallos(fallos);
                        cambiarPuntuacion();
                        pregunta.startAnimation(left_to_right_animation);

                        left_to_right_animation.setAnimationListener(new Animation.AnimationListener() {

                            @Override
                            public void onAnimationStart(Animation animation) {
                                // TODO Auto-generated method stub

                                txtPregunta.setBackgroundColor(getResources().getColor(R.color.fallo));
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                                // TODO Auto-generated method stub

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                try {

                                    txtPregunta.setBackgroundColor(getResources().getColor(R.color.text));
                                    moveViewDownQuick(pregunta);
                                    mostrarOtraPregunta();
                                } catch (TwitterException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            });
            layoutRespuestas.addView(resp);
            if (i != 3)
                addDividier();
        }
    }

    private void cambiarPuntuacion() {
        TextView puntuacion = (TextView) findViewById(R.id.txtScore);
        TextView txtaciertos = (TextView) findViewById(R.id.txtAciertos);
        TextView txtfallos = (TextView) findViewById(R.id.txtFallos);

        txtaciertos.setText(aciertos + "");
        txtfallos.setText(fallos + "");
        if (aciertos == 0 && fallos == 0)
            puntuacion.setText(0 + "%");
        else {
            double porcentaje = (aciertos / (aciertos + fallos)) * 100;

            DecimalFormat df = new DecimalFormat("#.00");
            String punt = df.format(porcentaje);
            puntuacion.setText(punt + "%");
        }

        puntuacion.startAnimation(scale);
    }

    private void mostrarOtraPregunta() throws TwitterException {

        respuestas.clear();
        layoutRespuestas.removeAllViews();

        txtPregunta.setText(seleccionarPregunta() + "\n", true);
        if (preguntaLength < 120)
            txtPregunta.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        else txtPregunta.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        seleccionarRespuestaAzar();
    }

    private void seleccionarRespuestaAzar() throws TwitterException {
        Random randomizer = new Random();
        int i = 1;
        while (i < 4) {
            int num = randomizer.nextInt(friends.size());
            User user = twitter.showUser(friends.get(num));
            if (!respuestas.contains(user)) {
                respuestas.add(user);
                i++;
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

    private void moveViewDown(final View view) {
        Animation slide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        view.startAnimation(slide);
    }

    private void moveViewDownSlow(final View view) {
        Animation slide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down_slow);
        view.startAnimation(slide);
    }

    private void moveViewDownQuick(final View view) {
        Animation slide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down_quick);
        view.startAnimation(slide);
    }

    private void addDividier() {
        ImageView v = new ImageView(this);
        v.setImageResource(R.drawable.divisor);
        layoutRespuestas.addView(v);
    }

    public void updateAciertos(double aciertos) {
        db.execSQL("UPDATE Puntuacion SET Aciertos=" + aciertos + ";");
    }

    public void updateFallos(double fallos) {
        db.execSQL("UPDATE Puntuacion SET Fallos=" + fallos + ";");
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
            txtPregunta.setText(seleccionarPregunta() + "\n", true);
            if (preguntaLength < 120)
                txtPregunta.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
            else txtPregunta.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
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
                for (Iterator<twitter4j.Status> iterator = statuses.iterator(); iterator.hasNext(); ) {
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
                        if (id != -1)
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

    private String removeUrl(String commentstr) {
        String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern p = Pattern.compile(urlPattern, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(commentstr);
        int i = 0;
        while (m.find()) {
            commentstr = commentstr.replaceAll(m.group(i), "").trim();
            i++;
        }
        return commentstr;
    }
}
