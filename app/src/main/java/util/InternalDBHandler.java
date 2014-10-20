package util;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class InternalDBHandler extends SQLiteOpenHelper {

    public InternalDBHandler(Context contexto) {
        super(contexto, "dbWelcomeIncoming.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //	Create	here	the	DB	by	code
        Log.e("bd", "Creamos la base de datos");
        db.execSQL("CREATE  TABLE main.Puntuacion (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, Aciertos INTEGER NOT NULL , Fallos INTEGER NOT NULL );");
        }

    @Override
    public void onUpgrade(
            SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
