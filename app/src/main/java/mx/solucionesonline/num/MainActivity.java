package mx.solucionesonline.num;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_SEND_SMS = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestSmsPermission();

        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            Toast.makeText(this, "Entro link : " + intent.getData().toString(), Toast.LENGTH_LONG).show();
        }
    }


    //METODO PARA PEDIR PERMISOS SMS
    private void requestSmsPermission() {
        // check permission is given
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            // request permission (see result in onRequestPermissionsResult() method)
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    PERMISSION_SEND_SMS);
        } else {
            // permission already granted run num send
            //sendSms(phone, message);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_SEND_SMS: {
                //SOLICITAR PERMISOS SMS
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                } else {
                    //VOLVER A SOLICITAR PERMISOS SMS SI ES QUE LOS NIEGA
                     requestSmsPermission();
                }
                return;
            }
        }
    }


}
