package mx.solucionesonline.num.Servicios;

import android.os.AsyncTask;
import mx.solucionesonline.num.Singleton;
import mx.solucionesonline.num.TCP;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class ServicioMaps extends AsyncTask<String, Integer, JSONObject> {

    private TCP tcp;
    private ServicioMaps.IServicioMaps delegate;
    private int error;
    private String response;
    public static String mResponseDesc;
    private Singleton singleton;
    public static final int SUCCESS = 0;
    public static final int CONEXION_BD = 1;
    public static final int ERROR_COMM = 99;
    public HttpClient client;
    HttpPost post;

    public ServicioMaps(ServicioMaps.IServicioMaps ctx) {
        singleton = Singleton.getInstance();
        delegate = ctx;
        /*tcp = new TCP(singelton.ip, singelton.port);
        tcp.setTimeOut(25000);
        tcp.setSoTimeOut(25000);*/
        client = new DefaultHttpClient();
        String url="https://solucionesonline.mx/apps_moviles/monitoreoNum/storedProcedure/coordsMapaApp.php";
        post = new HttpPost(url);
    }

    /**
     * Interface para manejar respuesta
     */
    public interface IServicioMaps {
        /**
         * Se mandó paquete para Loguearse en la aplicación
         *
         * @param error
         * @param response
         */
        void servicioMapsFinished(int error, String response);
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        try {
            if (delegate != null) {
                int code_error = Integer.parseInt(result.getString("code_error"));
                if (code_error == ERROR_COMM){
                    error = ERROR_COMM;
                    response = "Error de comunicación con servidor";
                }
                singleton.coordLat = Double.parseDouble(result.getString("lat"));
                singleton.coordLon = Double.parseDouble(result.getString("lon"));
                delegate.servicioMapsFinished(error, response);
            }
        }catch (Exception e){
            e.printStackTrace();
            error = ERROR_COMM;
            response = "Error de comunicación con servidor 2";
            delegate.servicioMapsFinished(error, response);
        }
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        return obtenerCoordenadas();
    }

    private JSONObject obtenerCoordenadas() {
        //Date fecha = new Date();
        //String fechahora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(fecha);
        error = ERROR_COMM;
        response = "Error de comunicación con servidor";
        try {
            List<NameValuePair> pairs = new ArrayList<NameValuePair>();
            pairs.add(new BasicNameValuePair("device",singleton.deviceIdRecibido));
            pairs.add(new BasicNameValuePair("accion",String.valueOf(singleton.opcionUbicacion)));
            pairs.add(new BasicNameValuePair("lat",String.valueOf(singleton.coordLat)));
            pairs.add(new BasicNameValuePair("lon",String.valueOf(singleton.coordLon)));
            post.setEntity(new UrlEncodedFormEntity(pairs, "UTF-8"));
            HttpResponse httpResponse = client.execute(post);
            int status = httpResponse.getStatusLine().getStatusCode();

            if (status == 200) {
                HttpEntity e = httpResponse.getEntity();
                String data = EntityUtils.toString(e);
                JSONObject last = new JSONObject(data);
                error = SUCCESS;
                response = "OK";
                return last;
            }
        }catch (Exception e){
            e.printStackTrace();
            error = ERROR_COMM;
            response = "Error de comunicación con servidor";
        }
        return null;
    }
}

