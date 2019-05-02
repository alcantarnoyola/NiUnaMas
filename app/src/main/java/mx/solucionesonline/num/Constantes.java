package mx.solucionesonline.num;

public class Constantes {

    //tabla alarmas
    public static final String NAME_TABLA_ALARMAS = "alarmas";
    public static final String CAMPO_ID = "id";
    public static final String CAMPO_HORA = "hora";
    public static final String CAMPO_DOMINGO = "domingo";
    public static final String CAMPO_LUNES = "lunes";
    public static final String CAMPO_MARTES = "martes";
    public static final String CAMPO_MIERCOLES = "miercoles";
    public static final String CAMPO_JUEVES = "jueves";
    public static final String CAMPO_VIERNES = "viernes";
    public static final String CAMPO_SABADO = "sabado";
    public static final String CAMPO_ACTIVADO = "activado";

    public static final String CREAR_TABLA_ALARMA = "CREATE TABLE alarmas ("+ CAMPO_ID + " INT NOT NULL PRIMARY KEY, "
                                                                            + CAMPO_HORA + " TEXT, "
                                                                            + CAMPO_DOMINGO + " INT, "
                                                                            + CAMPO_LUNES + " INT, "
                                                                            + CAMPO_MARTES + " INT, "
                                                                            + CAMPO_MIERCOLES + " INT, "
                                                                            + CAMPO_JUEVES + " INT, "
                                                                            + CAMPO_VIERNES + " INT, "
                                                                            + CAMPO_SABADO + " INT, "
                                                                            + CAMPO_ACTIVADO + " INT)";
}
