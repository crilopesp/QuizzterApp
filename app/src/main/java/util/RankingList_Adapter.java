package util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import limeandlemon.knowyourtimeline.R;
import logica.Usuario;
import twitter4j.Twitter;

/**
 * Created by marcos on 28/10/2014.
 */
public class RankingList_Adapter extends ArrayAdapter<Usuario>{

    private Context _context;
    private List<Usuario> usuarios;
    private int pos;
    private Typeface planetbenson2;

    public RankingList_Adapter(Context context, int resource) {
        super(context, resource);
        _context = context;
        planetbenson2 = Typeface.createFromAsset(_context.getAssets(),"fonts/gnuolane rg.ttf");
    }

    public RankingList_Adapter(Context context, int resource, List<Usuario> objects) {
        super(context, resource, objects);
        usuarios = objects;
        _context = context;
        planetbenson2 = Typeface.createFromAsset(_context.getAssets(),"fonts/gnuolane rg.ttf");
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null){
            LayoutInflater vi = LayoutInflater.from(_context);
            view = vi.inflate(R.layout.item_ranking,null);
        }

        final Usuario user = getItem(position);

        if(user != null){
            //if(user.getIdusuario() == Preferencias.getID...) //mi usuario distinto.. (otro color la puntuacion?)
            LinearLayout linearMain = (LinearLayout) view.findViewById(R.id.linearMain);
            RelativeLayout relativePosicion = (RelativeLayout) view.findViewById(R.id.relativePosicion);
            TextView tv_posicion = (TextView) view.findViewById(R.id.tv_posicion);
            TextView tv_aciertos = (TextView) view.findViewById(R.id.tv_aciertos);
            TextView tv_fallos = (TextView) view.findViewById(R.id.tv_fallos);
            TextView tv_puntuacion = (TextView) view.findViewById(R.id.tv_puntuacion);
            TextView tv_nombreCompleto = (TextView) view.findViewById(R.id.tv_nombreCompleto);
            TextView tv_nombreUsuario = (TextView) view.findViewById(R.id.tv_nombreUsuario);
            CircularImageView iv_photo = (CircularImageView) view.findViewById(R.id.iv_photo);

            linearMain.setBackgroundColor(Preferencias.getColor3(_context));
            relativePosicion.setBackgroundColor(Preferencias.getColor5(_context));
            iv_photo.setBorderColor(Preferencias.getColor4(_context));
            tv_posicion.setText("" + (position + 1));
            tv_posicion.setTypeface(planetbenson2);
            tv_aciertos.setText(""+user.getAciertos());
            tv_fallos.setText(""+user.getFallos());
            String puntuacion = new DecimalFormat("###,###").format(user.getPuntuacion())+ "p";
            tv_puntuacion.setText(""+puntuacion);
            tv_puntuacion.setTypeface(planetbenson2);
            tv_puntuacion.setTextColor(Preferencias.getColor2(_context));
            tv_nombreCompleto.setText(user.getNombre());
            tv_nombreCompleto.setTextColor(Preferencias.getColor4(_context));
            tv_nombreUsuario.setText("@"+user.getUsuario());

            TwitterUtil.descargarFotos(_context,user.getIdusuario(),iv_photo);

        }

        return view;
    }



}
