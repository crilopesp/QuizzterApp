package util.servidor;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import logica.Usuario;

/**
 * Created by marcos on 27/10/2014.
 */
public class Controlador {
    public Controlador() {
    }

    public String insertarUusario(long idUsuario, java.lang.String usuario, java.lang.String nombre){
        final String NAMESPACE = "http://UsersWS/";
        final String URL = "https://quizzter-limeandlemon.rhcloud.com:443/Quizzter/UsersWS?wsdl";
        final String SOAP_ACTION ="http://UsersWS/insertarUsuario";
        final String METHOD_NAME = "insertarUsuario";

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("idUsuario",idUsuario);
        request.addProperty("nombre",nombre);
        request.addProperty("usuario",usuario);
        SoapSerializationEnvelope envelope =
                new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        try {
            androidHttpTransport.call(SOAP_ACTION, envelope);
            SoapPrimitive  resultsRequestSOAP = (SoapPrimitive) envelope.getResponse();
            String res = resultsRequestSOAP.toString();
            //String res2 = resultsRequestSOAP.getValue().toString(); //sirve igual aparentemente
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "" + -1;

    }


    public String updatePuntuacion(long idUsuario, int aciertos, int fallos, double puntuacion) {
        final String NAMESPACE = "http://UsersWS/";
        final String URL = "https://quizzter-limeandlemon.rhcloud.com:443/Quizzter/UsersWS?wsdl";
        final String SOAP_ACTION ="http://UsersWS/updatePuntuacion";
        final String METHOD_NAME = "updatePuntuacion";


        Log.e("punt", "subiendo, metodo");
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("idUsuario",idUsuario);
        request.addProperty("aciertos",aciertos);
        request.addProperty("fallos",fallos);
        request.addProperty("puntuacion",puntuacion);
        SoapSerializationEnvelope envelope =
                new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        try {
            androidHttpTransport.call(SOAP_ACTION, envelope);
            SoapPrimitive  resultsRequestSOAP = (SoapPrimitive) envelope.getResponse();
            String res = resultsRequestSOAP.toString();

            Log.e("punt", "subiendo, try");
            //String res2 = resultsRequestSOAP.getValue().toString(); //sirve igual aparentemente
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "" + -1;
    }

    public ArrayList<Usuario> getAmigos(java.lang.String listaUsuariosString) {
        final String NAMESPACE = "http://UsersWS/";
        final String URL = "https://quizzter-limeandlemon.rhcloud.com:443/Quizzter/UsersWS?wsdl";
        final String SOAP_ACTION ="http://UsersWS/getAmigos";
        final String METHOD_NAME = "getAmigos";

        ArrayList<Usuario> listaUsuarios = new ArrayList<Usuario>();
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("listaUsuariosString",listaUsuariosString);
        SoapSerializationEnvelope envelope =
                new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        try {
            androidHttpTransport.call(SOAP_ACTION, envelope);
            SoapPrimitive  resultsRequestSOAP = (SoapPrimitive) envelope.getResponse();
            String res = resultsRequestSOAP.toString();
            //String res2 = resultsRequestSOAP.getValue().toString(); //sirve igual aparentemente
            Gson gson = new Gson();
            Type tipoObjeto = new TypeToken<List<Usuario>>(){}.getType();
            listaUsuarios = gson.fromJson(res,tipoObjeto);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return listaUsuarios;

    }

    public Usuario getUsuario(long idUsuario) {
        final String NAMESPACE = "http://UsersWS/";
        final String URL = "https://quizzter-limeandlemon.rhcloud.com:443/Quizzter/UsersWS?wsdl";
        final String SOAP_ACTION = "http://UsersWS/getAmigos";
        final String METHOD_NAME = "getAmigos";

        ArrayList<Usuario> listaUsuarios = new ArrayList<Usuario>();
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("listaUsuariosString", idUsuario);
        SoapSerializationEnvelope envelope =
                new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        try {
            androidHttpTransport.call(SOAP_ACTION, envelope);
            SoapPrimitive resultsRequestSOAP = (SoapPrimitive) envelope.getResponse();
            String res = resultsRequestSOAP.toString();
            //String res2 = resultsRequestSOAP.getValue().toString(); //sirve igual aparentemente
            Gson gson = new Gson();
            Type tipoObjeto = new TypeToken<List<Usuario>>() {
            }.getType();
            listaUsuarios = gson.fromJson(res, tipoObjeto);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaUsuarios.get(0);
    }

}
