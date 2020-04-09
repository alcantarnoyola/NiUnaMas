package mx.solucionesonline.num;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mx.solucionesonline.num.SQLite.ConexionSQLite;
import mx.solucionesonline.num.Servicios.ServicioContactos;
import mx.solucionesonline.num.Servicios.ServicioEnvioSms;
import mx.solucionesonline.num.Servicios.ServicioSoporteSugerencias;
import mx.solucionesonline.num.Servicios.UbicacionGps;

public class MainActivity extends AppCompatActivity implements ServicioEnvioSms.IServicioEnvioSms, ServicioSoporteSugerencias.IServicioSoporteSugerencias {
    private static final int PERMISSION_SEND_SMS = 1;
    private static final int PERMISSION_GPS = 2;
    public Singleton singleton;
    public UbicacionGps ubicacionGps;
    private AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        singleton = Singleton.getInstance();
        ubicacionGps = new UbicacionGps(MainActivity.this);
        requestSmsPermission();
        uid();
        singleton.context = MainActivity.this;

        singleton.conn = new ConexionSQLite(this, "num",null,1);
        /*try {
            requestGpsPermission();
        }catch (Exception e){
            e.printStackTrace();
        }*/
    }

    public void contactos(View view){
        Intent intent = new Intent (MainActivity.this, ContactosActivity.class);
        startActivity(intent);
    }

    public void mapa(View view){
        if(!singleton.locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER )){
            showAlert();
        }else{
            Intent intent = new Intent (MainActivity.this, MapsActivity.class);
            startActivity(intent);
        }
    }
    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Gps desactivado")
                .setMessage("Por favor active su ubicación")
                .setPositiveButton("Activar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).
                setCancelable(false);
        dialog.show();
    }


    public void alarmas(View view){
        Intent intent = new Intent (MainActivity.this, AlarmaActivity.class);
        startActivity(intent);
    }

    public void send_mensaje(View view){
        dialogSendMensaje();
    }

    public void soporte_sugerencias(View view){
        dialogSoporteSugerencias();
    }

    public void configuracion(View view){
        Intent intent = new Intent (MainActivity.this, ConfigActivity.class);
        startActivity(intent);
    }
    public void uid(){
        try {
            singleton.deviceId = Settings.Secure.getString(this.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            if (singleton.deviceId == "") {
                uid();
            }
        }catch (Exception e){
            uid();
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
            requestGpsPermission();
        }
    }
    //METODO PARA PEDIR PERMISOS DE UBICACIÓN
    private void requestGpsPermission() {
        // check permission is given
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // request permission (see result in onRequestPermissionsResult() method)
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_GPS);
        } else {
            // permission already granted
            singleton.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            singleton.locationListener = new UbicacionGps(MainActivity.this);
            singleton.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 9000, 0, singleton.locationListener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_SEND_SMS: {
                //SOLICITAR PERMISOS SMS
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    requestGpsPermission();
                } else {
                    //VOLVER A SOLICITAR PERMISOS SMS SI ES QUE LOS NIEGA
                     requestSmsPermission();
                }
                return;
            }
            case PERMISSION_GPS: {
                //SOLICITAR PERMISOS SMS
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                } else {
                    //VOLVER A SOLICITAR PERMISOS GPS SI ES QUE LOS NIEGA
                    requestGpsPermission();
                }
                return;
            }
        }
    }

    public void dialogSendMensaje(){

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(R.layout.dialog_send_mensaje);
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(null);
        final Button btn_aceptar = dialog.findViewById(R.id.btn_aceptar);
        final Button btn_cancelar = dialog.findViewById(R.id.btn_cancelar);
        final Spinner spinner = dialog.findViewById(R.id.contactos_spinner);
        llenarListaMensajes();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, singleton.lista_contactos);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        btn_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = spinner.getSelectedItem().toString();
                singleton.posicionContacto = Integer.parseInt(data.split(".-")[0]) -1;
                ServicioEnvioSms servicioEnvioSms = new ServicioEnvioSms(MainActivity.this);
                servicioEnvioSms.execute("");
            }
        });

        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    private void llenarListaMensajes() {
        //sumamos uno para darle vista
        if (singleton.lista_contactos.size() > 0){
            singleton.lista_contactos.clear();
        }
        for(int i=0; i<singleton.lista_hashmap_contactos.size(); i++){
            singleton.lista_contactos.add((i+1)+".- " + singleton.lista_hashmap_contactos.listIterator(i).next().get(Constantes.CAMPO_NOMBRE) + " : " +
                    singleton.lista_hashmap_contactos.listIterator(i).next().get(Constantes.CAMPO_NUMERO));
        }
    }

    public void dialogSoporteSugerencias(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(R.layout.dialog_soporte_sugerencia);
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(null);
        final Button btn_aceptar = dialog.findViewById(R.id.btn_aceptar);
        final Button btn_cancelar = dialog.findViewById(R.id.btn_cancelar);
        final EditText name = dialog.findViewById(R.id.edit_name);
        final EditText message = dialog.findViewById(R.id.edit_message);
        btn_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                singleton.nombreSoporte = name.getText().toString();
                singleton.mensajeSoporte = message.getText().toString();
                ServicioSoporteSugerencias servicioSoporteSugerencias = new ServicioSoporteSugerencias(MainActivity.this);
                servicioSoporteSugerencias.execute("");
            }
        });

        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    @Override
    public void servicioEnvioSmsFinished(int error, String response) {
        dialog.dismiss();
        if (error == ServicioEnvioSms.SUCCESS){
            Toast.makeText(this, "Sms enviado correctamente, en breve recibira su ubicación", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "Ha ocurrrido un error, intenta más tarde", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void servicioSoporteSugerenciasFinished(int error, String response) {
        dialog.dismiss();
        if (error == ServicioSoporteSugerencias.SUCCESS){
            Toast.makeText(this, "Gracias por contactarnos", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "Ha ocurrrido un error, intenta más tarde", Toast.LENGTH_SHORT).show();
        }
    }
}
