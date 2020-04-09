package mx.solucionesonline.num.Servicios;

import android.os.AsyncTask;

import mx.solucionesonline.num.Constantes;
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

public class ServicioEnvioSms extends AsyncTask<String, Integer, String> {

    private TCP tcp;
    private ServicioEnvioSms.IServicioEnvioSms delegate;
    private int error;
    private String response;
    public static String mResponseDesc;
    private Singleton singleton;
    public static final int SUCCESS = 0;
    public static final int CONEXION_BD = 1;
    public static final int ERROR_COMM = 99;
    public HttpClient client;
    public String messageId = "";
    public String estatus = "";
    HttpPost post;

    public ServicioEnvioSms(ServicioEnvioSms.IServicioEnvioSms ctx) {
        singleton = Singleton.getInstance();
        delegate = ctx;
        /*tcp = new TCP(singelton.ip, singelton.port);
        tcp.setTimeOut(25000);
        tcp.setSoTimeOut(25000);*/
        client = new DefaultHttpClient();
        String nombre = singleton.lista_hashmap_contactos.listIterator(singleton.posicionContacto).next().get(Constantes.CAMPO_NOMBRE);
        String numero = singleton.lista_hashmap_contactos.listIterator(singleton.posicionContacto).next().get(Constantes.CAMPO_NUMERO);
        String url="http://sms-tecnomovil.com/SvtSendSms?username=DIAZDELEON&password=SOON.1418&message="+nombre+"&numbers="+numero;
        post = new HttpPost(url);
    }

    /**
     * Interface para manejar respuesta
     */
    public interface IServicioEnvioSms {
        /**
         * Se mandó paquete para Loguearse en la aplicación
         *
         * @param error
         * @param response
         */
        void servicioEnvioSmsFinished(int error, String response);
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            if (delegate != null) {
                if(messageId != "" && estatus == "OK"){
                    error = SUCCESS;
                }
                delegate.servicioEnvioSmsFinished(error, response);
            }
        }catch (Exception e){
            e.printStackTrace();
            error = ERROR_COMM;
            response = "Error de comunicación con servidor 2";
            delegate.servicioEnvioSmsFinished(error, response);
        }
    }

    @Override
    protected String doInBackground(String... params) {
        return sms();
    }

    private String sms() {
        //Date fecha = new Date();
        //String fechahora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(fecha);
        error = ERROR_COMM;
        response = "Error de comunicación con servidor";
        try {
            HttpResponse httpResponse = client.execute(post);
            int status = httpResponse.getStatusLine().getStatusCode();

            if (status == 200) {
                HttpEntity e = httpResponse.getEntity();
                String[] data = EntityUtils.toString(e).split("\n");
                try {
                    messageId = data[2].split(">")[1].split("<")[0]; //obtenemos identificador de <messageId>2711544</messageId>
                    estatus = data[3].split(">")[1].split("<")[0];
                }catch (Exception ex){
                    ex.printStackTrace();
                    error = ERROR_COMM;
                    response = "Error de comunicación con servidor";
                    messageId = "";
                    estatus = "";
                }
                response = "OK";
                return response;

            }
        }catch (Exception e){
            e.printStackTrace();
            error = ERROR_COMM;
            response = "Error de comunicación con servidor";
            messageId = "";
            estatus = "";
        }
        return null;
    }
}

