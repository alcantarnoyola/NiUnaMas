package mx.solucionesonline.num;

import com.google.android.gms.maps.GoogleMap;

public class Singleton {
    private static Singleton singleton = new Singleton();
    public GoogleMap mMap = null;
    public double coordLat = 0;
    public double coordLon = 0;
    public boolean islogued = false;
    public String deviceId = "";

    //creamos solamente una vez la clase
    public Singleton(){

    }

    public synchronized static void createInstance(){
        if (singleton == null) {
            singleton = new Singleton();
        }
    }

    public static Singleton getInstance() {
        createInstance();
        return singleton;
    }
    //fin
}
