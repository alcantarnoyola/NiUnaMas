package mx.solucionesonline.num;

import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mx.solucionesonline.num.SQLite.ConexionSQLite;

public class Singleton {
    private static Singleton singleton = new Singleton();
    public boolean islogued = false;
    public String deviceId = "";
    public String deviceIdRecibido = "";
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
    public List<HashMap<String, String>> lista_hashmap = new ArrayList<HashMap<String, String>>();


    //variables para alarmas
    public List<HashMap<String, String>> lista_hashmap_alarmas = new ArrayList<HashMap<String, String>>();
    public HashMap<String, Integer> diasSemana = new HashMap<String, Integer>();
    public String horaAlarma = "";
    public boolean activado = false;
    public int posicionAlarma;
    public ArrayList<String> lista_alarmas = new ArrayList<String>();

    //Variables para ubicacion gps
    public double lat = 0;
    public double lon = 0;

    //sqlite
    public ConexionSQLite conn;

    //creamos solamente una vez la clase
    public Singleton(){
        arrayListContactos = new ArrayList<String>();
        arrayListContactos.add("Contacto 1566656");
        //CONTACTO DE PRUEBA, SE QUITARA CUANDO SE AGREGUE EL PRIMER CONTACTO EN SPLASH
        HashMap<String, String> hm = new HashMap<String, String>();
        hm.put("Nombre", "Alcantar");
        hm.put("Numero", "4441242655");
        hm.put("Imagen", String.valueOf(R.drawable.contacto_generico));
        lista_hashmap.add(hm);

        //Asignar dias de la semana
        diasSemana.put("domingos",0);
        diasSemana.put("lunes",0);
        diasSemana.put("martes",0);
        diasSemana.put("miercoles",0);
        diasSemana.put("jueves",0);
        diasSemana.put("viernes",0);
        diasSemana.put("sabados",0);


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
