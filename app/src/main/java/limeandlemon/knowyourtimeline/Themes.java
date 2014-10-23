package limeandlemon.knowyourtimeline;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import util.Preferencias;


public class Themes extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_themes);
        Button btnaqua = (Button) findViewById(R.id.btnAqua);
        Button btncafe = (Button) findViewById(R.id.btnCafe);
        Button btnmiel = (Button) findViewById(R.id.btnMiel);
        Button btnrosa = (Button) findViewById(R.id.btnRosa);
        Button btnelegance = (Button) findViewById(R.id.btnElegance);
        RelativeLayout relative = (RelativeLayout) findViewById(R.id.relative);
        relative.setBackgroundColor(Preferencias.getColor3(this));

        btnaqua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Preferencias.setColor1(getApplicationContext(), getResources().getColor(R.color.color1aqua));
                Preferencias.setColor2(getApplicationContext(), getResources().getColor(R.color.color2aqua));
                Preferencias.setColor3(getApplicationContext(), getResources().getColor(R.color.color3aqua));
                Preferencias.setColor4(getApplicationContext(), getResources().getColor(R.color.colorLinkaqua));
                Preferencias.setColor5(getApplicationContext(), getResources().getColor(R.color.colorTweetaqua));

                finish();
            }
        });
        btncafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Preferencias.setColor1(getApplicationContext(), getResources().getColor(R.color.color1cafe));
                Preferencias.setColor2(getApplicationContext(), getResources().getColor(R.color.color2cafe));
                Preferencias.setColor3(getApplicationContext(), getResources().getColor(R.color.color3cafe));
                Preferencias.setColor4(getApplicationContext(), getResources().getColor(R.color.colorLinkcafe));
                Preferencias.setColor5(getApplicationContext(), getResources().getColor(R.color.colorTweetcafe));

                finish();
            }
        });
        btnmiel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Preferencias.setColor1(getApplicationContext(), getResources().getColor(R.color.color1miel));
                Preferencias.setColor2(getApplicationContext(), getResources().getColor(R.color.color2miel));
                Preferencias.setColor3(getApplicationContext(), getResources().getColor(R.color.color3miel));
                Preferencias.setColor4(getApplicationContext(), getResources().getColor(R.color.colorLinkmiel));
                Preferencias.setColor5(getApplicationContext(), getResources().getColor(R.color.colorTweetmiel));

                finish();
            }
        });
        btnrosa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Preferencias.setColor1(getApplicationContext(), getResources().getColor(R.color.color1rosa));
                Preferencias.setColor2(getApplicationContext(), getResources().getColor(R.color.color2rosa));
                Preferencias.setColor3(getApplicationContext(), getResources().getColor(R.color.color3rosa));
                Preferencias.setColor4(getApplicationContext(), getResources().getColor(R.color.colorLinkrosa));
                Preferencias.setColor5(getApplicationContext(), getResources().getColor(R.color.colorTweetrosa));

                finish();
            }
        });
        btnelegance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Preferencias.setColor1(getApplicationContext(), getResources().getColor(R.color.color1elegance));
                Preferencias.setColor2(getApplicationContext(), getResources().getColor(R.color.color2elegance));
                Preferencias.setColor3(getApplicationContext(), getResources().getColor(R.color.color3elegance));
                Preferencias.setColor4(getApplicationContext(), getResources().getColor(R.color.colorLinkelegance));
                Preferencias.setColor5(getApplicationContext(), getResources().getColor(R.color.colorTweetelegance));

                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.themes, menu);
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
}
