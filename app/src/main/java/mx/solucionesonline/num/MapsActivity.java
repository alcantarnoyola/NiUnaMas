package mx.solucionesonline.num;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.text.Layout;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import mx.solucionesonline.num.MapsDireccion.DownloadTask;
import mx.solucionesonline.num.Servicios.ServicioMaps;
import mx.solucionesonline.num.Servicios.UbicacionGps;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ServicioMaps.IServicioMaps {
    private String [] coordsReceived = null;
    private String response = ""; //recibimos respuesta de http
    public Singleton singleton;
    private Thread coord;
    private boolean first= false;
    private int seconds = 0;
    //private Marker marcador;
    private Marker marcadorCuidador;
    private Handler handler;
    private  CameraPosition cameraPosition;
    private boolean moverCamara = true;
    private Polyline polyline;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        singleton = Singleton.getInstance();
        uid();
        activarProcesoGps();
        singleton.mMap = null;
        polyline = null;

        //codigo para recoger lo recibido desde link
        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            coordsReceived = intent.getData().toString().split("/");
            singleton.coordLat = Double.parseDouble(coordsReceived[4].split(",")[0]);
            singleton.coordLon = Double.parseDouble(coordsReceived[4].split(",")[1]);
            singleton.deviceIdRecibido = String.valueOf(coordsReceived[4].split(",")[2]);
            singleton.statusAlertSms = true;
            moverCamara = true;
            Toast.makeText(this, "Coordenadas recibidas: " + singleton.coordLat + "*****" + singleton.coordLon + "*****" + singleton.deviceIdRecibido, Toast.LENGTH_LONG).show();
        }//fin de recoger lo recibido por activity

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                execThread();//realizamos todo el proceso de generacion de markers
                handler.postDelayed(this, 1000);//se ejecutara cada 1 segundos
            }
        }, 0);//empezara a ejecutarse después de 0 milisegundos

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void activarProcesoGps() {
        try {
            // permission already granted
            singleton.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            singleton.locationListener = new UbicacionGps((MainActivity) singleton.context);
            singleton.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, singleton.locationListener);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void centrarCamara(){
        cameraPosition = CameraPosition.builder().target(
                new LatLng(singleton.lat, singleton.lon)).zoom(18).bearing(0).tilt(45).build(); //asignamos coords a centrar el mapa
        singleton.mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        moverCamara = false;
        Log.v("exception", "Centrando");
    }

    public void execThread(){
        if (singleton.mMap != null && singleton.statusAlertSms) {
            // al abrir activity mostramos coordenada recogida por sms
            seconds += 1000;
            if (seconds > 15000 && singleton.statusAlertSms || !first) {
                if (singleton.markerObjetivo != null) {//borramos markador
                    singleton.markerObjetivo.remove();
                }

                first = true;
                seconds = 0;
                coordsReceived();

            }

        }
        if (singleton.mMap != null) {
            if (marcadorCuidador != null) {//borramos markador
                marcadorCuidador.remove();
                Log.v("exception", "borramos cuidador");
            }
            if(polyline != null)
                polyline.remove();


            if(singleton.lat != 0 && singleton.lon != 0) {
                marcadorCuidador = singleton.mMap.addMarker(new MarkerOptions().position(new LatLng(singleton.lat, singleton.lon)).title("Cuidador")); //creamos marker
                /*polyline = singleton.mMap.addPolyline(new PolylineOptions().add(new LatLng(singleton.lat,singleton.lon), new LatLng(singleton.coordLat, singleton.coordLon))
                                                        .width(5).color(Color.RED));*/
                if(moverCamara)
                    centrarCamara();
                Log.v("exception" ,"** Entro bien");
            }
        }
    }

    public void coordsReceived(){
        ServicioMaps servicioMaps = new ServicioMaps(MapsActivity.this);
        servicioMaps.execute("");
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
        singleton.mMap = googleMap;
        singleton.mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        marcadorCuidador = singleton.mMap.addMarker(new MarkerOptions().position(new LatLng(singleton.lat, singleton.lon)).title("Cuidador")); //creamos marker



        /*if(singleton.statusAlertSms){
            singleton.mMap = googleMap;
            singleton.mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            //centrarCamara();
        }*/
    }
    // method definition
    public BitmapDescriptor getMarkerIcon(String color) {
        float[] hsv = new float[3];
        Color.colorToHSV(Color.parseColor(color), hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeMessages(0); //detener hilo
        singleton.locationManager.removeUpdates(singleton.locationListener);//detener location
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

    @Override
    public void servicioMapsFinished(int error, String response) {
        if (error == ServicioMaps.SUCCESS){
            String url = obtenerDireccionesURL(marcadorCuidador.getPosition(), new LatLng(singleton.coordLat, singleton.coordLon));
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(url);
            singleton.markerObjetivo = singleton.mMap.addMarker(new MarkerOptions().position(new LatLng(singleton.coordLat, singleton.coordLon)).title("Objetivo")); //creamos marker

            Toast.makeText(this, "Ejecutando...", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
        }



    }

    private String obtenerDireccionesURL(LatLng origin,LatLng dest){

        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        String sensor = "sensor=false";
        String key = "key=AIzaSyBB4pqVjDSsDtsoipSzwElBB4TMiK9sX-g";

        String parameters = str_origin+"&"+str_dest+"&"+sensor+"&"+key;

        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }
}
