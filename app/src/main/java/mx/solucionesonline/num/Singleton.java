package mx.solucionesonline.num;

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

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
    public GoogleMap auxMap = null;
    public double coordLat = 0;
    public double coordLon = 0;
    public boolean statusAlertSms = false;
    public Polyline polyline = null;
    public Marker markerObjetivo = null;

    //variables para contactos
    public String nombreContacto = "";
    public String numeroContacto = "";
    public int selectOper;
    public int posicionContacto;
    public List<String> lista_contactos = new ArrayList<String>();;
    public List<HashMap<String, String>> lista_hashmap_contactos = new ArrayList<HashMap<String, String>>();


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
    public LocationManager locationManager;
    public LocationListener locationListener;
    public int opcionUbicacion = 0;
    //variables para soporte
    public String nombreSoporte = "";
    public String mensajeSoporte = "";

    //variables para config
    public ArrayList<String> menu =  new ArrayList<String>();
    public String nombre = "";
    public String apellidos = "";
    public String email = "";
    public String colonia = "";
    public String municipio = "";
    public String estado = "";
    public String gradoestudios = "";
    public ArrayList<String> lista_gradoestudios =  new ArrayList<String>();

    public Context context;

    //sqlite
    public ConexionSQLite conn;

    //creamos solamente una vez la clase
    public Singleton(){

        //Asignar dias de la semana
        diasSemana.put("domingos",0);
        diasSemana.put("lunes",0);
        diasSemana.put("martes",0);
        diasSemana.put("miercoles",0);
        diasSemana.put("jueves",0);
        diasSemana.put("viernes",0);
        diasSemana.put("sabados",0);

        //Asignar menu
        menu.add("Completa tu perfil");
        menu.add("Aviso de privacidad");
        menu.add("Acerca de");
        menu.add("Equipo Soon");

        //asignar grados de estudio
        lista_gradoestudios.add("Seleccione una opción");
        lista_gradoestudios.add("1.- Primaria");
        lista_gradoestudios.add("2.- Secundaria");
        lista_gradoestudios.add("3.- Bachillerato");
        lista_gradoestudios.add("4.- Profecional técnica");
        lista_gradoestudios.add("5.- Técnico superior");
        lista_gradoestudios.add("6.- Licenciatura");
        lista_gradoestudios.add("7.- Posgrado");
        lista_gradoestudios.add("8.- Especialidad");
        lista_gradoestudios.add("9.- Maestría");
        lista_gradoestudios.add("10.- Doctorado");

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
