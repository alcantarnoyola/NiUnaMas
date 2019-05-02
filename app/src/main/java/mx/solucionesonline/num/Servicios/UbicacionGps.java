package mx.solucionesonline.num.Servicios;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

import mx.solucionesonline.num.MainActivity;
import mx.solucionesonline.num.Singleton;

public class UbicacionGps extends MainActivity implements LocationListener {

    public Context mainContext = null;
    public Singleton singleton;

    public UbicacionGps(MainActivity mainActivity) {
        super();
        mainContext = mainActivity;
        singleton = Singleton.getInstance();
    }

    public void onLocationChanged(Location loc) {
        try {
            singleton.lat = loc.getLatitude();
            singleton.lon = loc.getLongitude();
            String coordenadas = "Latitud = " + singleton.lon + "Longitud = " + singleton.lon;
            //Toast.makeText(mainContext, coordenadas, Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
            singleton.lat = 22.152292;
            singleton.lon = -100.936622;
        }
    }

    public void onProviderDisabled(String provider) {
    }

    public void onProviderEnabled(String provider) {
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

}
