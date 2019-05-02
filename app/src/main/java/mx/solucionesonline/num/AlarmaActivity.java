package mx.solucionesonline.num;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mx.solucionesonline.num.SQLite.ConexionSQLite;

import static android.widget.AdapterView.*;

public class AlarmaActivity extends AppCompatActivity {
    public Singleton singleton;
    private ArrayAdapter<String> arrayAdapter;
    private ListView listaAlarmas;
    public AlertDialog.Builder alertDialog;
    public String[] from = {"Hora"};
    public int[] to = {R.id.txt_hora_alarma};
    private SimpleAdapter simpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarma);

        singleton = Singleton.getInstance();

        //Creamos accion al dar click en boton flotante add alarma
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAddAlarma();
            }
        });


        //creamos lista de alarmas
        simpleAdapter = new SimpleAdapter(getBaseContext(), singleton.lista_hashmap_alarmas, R.layout.listview_alarmas,from,to);
        listaAlarmas = findViewById(R.id.listaAlarmas);
        listaAlarmas.setAdapter(simpleAdapter);

        listaAlarmas.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                singleton.posicionContacto = i;
                /*singleton.nombreContacto = singleton.lista_hashmap.get(singleton.posicionContacto).values().toArray()[1].toString();
                singleton.numeroContacto = singleton.lista_hashmap.get(singleton.posicionContacto).values().toArray()[2].toString();*/
                alertDialog = new AlertDialog.Builder(AlarmaActivity.this);
                alertDialog.setTitle("Confirmación");
                alertDialog.setMessage("¿Desea eliminar esta alerta " + singleton.nombreContacto + " ?");
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        dialogo1.dismiss();
                    }
                });
                alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        dialogo1.dismiss();
                    }
                });
                alertDialog.show();

                return false;
            }
        });

    }
    
    public void tos(View view){
        final TextView textView = ((TextView) view);
        Toast.makeText(this, "Me presiono " + textView.getText(), Toast.LENGTH_SHORT).show();
    }

    public void dialogAddAlarma(){
        alertDialog = new AlertDialog.Builder(AlarmaActivity.this, R.style.DialogAlarm);
        final LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_add_alarma, null);
        Button btnGuardarAlarma = dialogView.findViewById(R.id.btnGuardarAlarma);
        Button btnCancelarAlarma = dialogView.findViewById(R.id.btnCancelarAlarma);
        final TimePicker timePicker = dialogView.findViewById(R.id.timepicker);



        final AlertDialog dialog = alertDialog.setView(dialogView).create();
        dialog.show();


        btnCancelarAlarma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnGuardarAlarma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hora = timePicker.getCurrentHour();
                int minutos = timePicker.getCurrentMinute();
                insertarAlarma(hora, minutos);
                dialog.dismiss();
            }
        });
    }

    public void onCheckboxClicked(View view) {

        final CheckedTextView checkedTextView = ((CheckedTextView) view);
        String dia = checkedTextView.getText().toString().split(" ")[2];
        checkedTextView.setChecked(!checkedTextView.isChecked()); //indica si se puede presionar (obligatorio)
        //checkedTextView.setCheckMarkDrawable(checkedTextView.isChecked() ? android.R.drawable.checkbox_on_background : android.R.drawable.checkbox_off_background);

        if(checkedTextView.isChecked()){
            checkedTextView.setCheckMarkDrawable(R.drawable.check_size); //icono
            singleton.diasSemana.put(dia,1);
        }else{
            checkedTextView.setCheckMarkDrawable(null); //icono
            singleton.diasSemana.put(dia,0);
        }
    }

    public void insertarAlarma(int hora, int minutos){
        singleton.guardarPosicion = singleton.lista_hashmap_alarmas.size() + 1;
        int domingo = singleton.diasSemana.get("domingos");
        int lunes = singleton.diasSemana.get("lunes");
        int martes = singleton.diasSemana.get("martes");
        int miercoles = singleton.diasSemana.get("miercoles");
        int jueves = singleton.diasSemana.get("jueves");
        int viernes = singleton.diasSemana.get("viernes");
        int sabado = singleton.diasSemana.get("sabados");
        singleton.activado = 1;
        singleton.horaAlarma = String.format("%02d",hora) + ":" + String.format("%02d", minutos);
        if(hora < 10 || minutos < 10)
            singleton.horaAlarma = String.format("%02d",hora) + ":" + String.format("%02d", minutos);

        SQLiteDatabase db = singleton.conn.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constantes.CAMPO_ID,singleton.guardarPosicion);
        values.put(Constantes.CAMPO_HORA,singleton.horaAlarma);
        values.put(Constantes.CAMPO_DOMINGO,domingo);
        values.put(Constantes.CAMPO_LUNES,lunes);
        values.put(Constantes.CAMPO_MARTES,martes);
        values.put(Constantes.CAMPO_MIERCOLES,miercoles);
        values.put(Constantes.CAMPO_JUEVES,jueves);
        values.put(Constantes.CAMPO_VIERNES,viernes);
        values.put(Constantes.CAMPO_SABADO,sabado);
        values.put(Constantes.CAMPO_ACTIVADO,singleton.activado);
        Long result = Long.valueOf(-1);
        try {
            result = db.insert(Constantes.NAME_TABLA_ALARMAS, Constantes.CAMPO_ID, values);
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();

        if(result != -1){ //REGISTRO EXITOSO
            //creamos lista de alarmas
            HashMap<String, String> hm_alarma = new HashMap<String, String>();
            hm_alarma.put("Hora", singleton.horaAlarma);
            singleton.lista_hashmap_alarmas.add(hm_alarma);
            simpleAdapter = new SimpleAdapter(getBaseContext(), singleton.lista_hashmap_alarmas, R.layout.listview_alarmas,from,to);
            listaAlarmas = findViewById(R.id.listaAlarmas);
            listaAlarmas.setAdapter(simpleAdapter);
            Toast.makeText(this, "Alerta Guardada", Toast.LENGTH_SHORT).show();
        }else{
            singleton.activado = 0; //regresamos bandera
            Toast.makeText(this, "Ha ocurrrido un error, intenta más tarde", Toast.LENGTH_SHORT).show();
        }

    }
}
