package mx.solucionesonline.num;

import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;

public class Singleton {
    private static Singleton singleton = new Singleton();
    public boolean islogued = false;
    public String deviceId = "";
    public String ip = Configuracion.ip;
    public int port = Configuracion.port;

    //variables para mapa
    public GoogleMap mMap = null;
    public double coordLat = 0;
    public double coordLon = 0;
    public boolean statusAlertSms = false;

    //variables para contactos
    public String nombreContacto = "";
    public String numeroContacto = "";
    public int selectOper;
    public int posicionContacto;
    public ArrayList<String> arrayListContactos;

    //variables para alarmas
    //variables para contactos
    public String horaAlarma = "";
    public String diasAlarma = "";
    public ArrayList<String> arrayListAlarma;

    //creamos solamente una vez la clase
    public Singleton(){
        arrayListContactos = new ArrayList<String>();
        arrayListAlarma = new ArrayList<String>();
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
