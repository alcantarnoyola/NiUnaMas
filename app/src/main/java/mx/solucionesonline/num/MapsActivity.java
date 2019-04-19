package mx.solucionesonline.num;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    public String [] coordsReceived = null;
    public String response = ""; //recibimos respuesta de http
    public Singleton singleton;
    public Thread coord;
    public boolean firstMarker = false;
    public int seconds = 0;
    public Marker marcador;
    public Handler handler;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        singleton = Singleton.getInstance();
        uid();
        //codigo para recoger lo recibido desde link
        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            coordsReceived = intent.getData().toString().split("/");
            singleton.coordLat = Double.parseDouble(coordsReceived[4].split(",")[0]);
            singleton.coordLon = Double.parseDouble(coordsReceived[4].split(",")[1]);
            Toast.makeText(this, "Coordenadas recibidas: " + singleton.coordLat + "*****" + singleton.coordLon, Toast.LENGTH_LONG).show();
        }//fin de recoger lo recibido por activity
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                execThread();//realizamos todo el proceso de generacion de markers
                handler.postDelayed(this, 1000);//se ejecutara cada 1 segundos
            }
        }, 0);//empezara a ejecutarse después de 5 milisegundos

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void execThread(){
        if (singleton.mMap != null && !firstMarker) {
            firstMarker = true;
            // al abrir activity mostramos coordenada recogida por sms
            marcador = singleton.mMap.addMarker(new MarkerOptions().position(new LatLng(singleton.coordLat, singleton.coordLon)).title("Objetivo")); //creamos marker
        }else if (firstMarker){
            seconds += 1000;
            if (seconds > 15000) {
                if (marcador != null){//borramos markador
                    marcador.remove();
                }
                seconds = 0;
                coordsReceived();
                marcador = singleton.mMap.addMarker(new MarkerOptions().position(new LatLng(singleton.coordLat, singleton.coordLon)).title("Objetivo3")); //creamos marker
            }
        }
        /*
        coord = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    if (singleton.mMap != null && !firstMarker) {
                        firstMarker = true;
                        // al abrir activity mostramos coordenada recogida por sms
                        marcador = singleton.mMap.addMarker(new MarkerOptions().position(new LatLng(singleton.coordLat, singleton.coordLon)).title("Objetivo")); //creamos marker
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    seconds += 1000;
                    if (seconds > 15000){
                        Toast.makeText(MapsActivity.this, "Remove", Toast.LENGTH_SHORT).show();
                        seconds = 0;
                        marcador.remove();
                        //Realizamos peticion http
                        coordsReceived();
                    }
                }
            }
        });
        coord.start();*/
    }

    public void coordsReceived(){
        URL url = null;
        try {
            url = new URL("https://solucionesonline.mx/apps_moviles/monitoreoNum/coordsMapaApp.php?device="+singleton.deviceId);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream()));
            // Acciones a realizar con el flujo de datos
            String inputLine;
            StringBuffer stringBuffer = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                stringBuffer.append(inputLine);
            }
            response = String.valueOf(stringBuffer);
            String [] res = response.split("#");

            if(!res[0].equals("-1")){ //si es diferente de -1 es que todo esta bien
                singleton.coordLat = Double.parseDouble(res[1]);
                singleton.coordLon = Double.parseDouble(res[2]);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            urlConnection.disconnect();
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (singleton.mMap == null) {
            singleton.mMap = googleMap;
            singleton.mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            CameraPosition cameraPosition = CameraPosition.builder().target(
                    new LatLng(singleton.coordLat,singleton.coordLon)).zoom(16).bearing(0).tilt(45).build(); //asignamos coords a centrar el mapa
            singleton.mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeMessages(0); //detener hilo
        Intent intent = new Intent (MapsActivity.this, MainActivity.class);
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
}
