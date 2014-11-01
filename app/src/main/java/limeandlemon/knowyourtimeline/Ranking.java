package limeandlemon.knowyourtimeline;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;

import logica.Usuario;
import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import util.RankingList_Adapter;
import util.TwitterUtil;
import util.servidor.Controlador;


public class Ranking extends Activity {

    ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        listview = (ListView) findViewById(R.id.listView);
        new CargarRanking(this).execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.ranking, menu);
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


    class CargarRanking extends AsyncTask<Void,Void,Void>{

        private Context _context;
        private ArrayList<Usuario> listaUsuarios;
        private Controlador control;

        CargarRanking(Context _context) {
            this._context = _context;
            control = new Controlador();
            listaUsuarios = new ArrayList<Usuario>();
        }

        @Override
        protected void onPostExecute(Void v){
            super.onPostExecute(v);
            if(!listaUsuarios.isEmpty()){
                listview.setAdapter(new RankingList_Adapter(_context,0,listaUsuarios));

            }else{

            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Twitter twitter = TwitterUtil.getTwitter(_context);
            try {
                String stringIds= twitter.getId() + ", ";
                long cursor = -1;
                IDs ids;
                do {
                    ids = twitter.getFriendsIDs(cursor);
                    for (long id : ids.getIDs()) {
                        if (id != -1)
                            stringIds += id + ", ";
                    }
                } while ((cursor = ids.getNextCursor()) != 0);
                stringIds = stringIds.substring(0,stringIds.length()-2);
                stringIds = "12,23,233,493862374,607281049,6809022,234891,496278502"; //descomentar para rellenar mas la lista
                listaUsuarios = control.getAmigos(stringIds);
            } catch (TwitterException e) {
                e.printStackTrace();
            }

            //obterner un string con los id de los amigos.

            return null;
        }
    }
}
