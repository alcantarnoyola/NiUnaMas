package mx.solucionesonline.num;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ViewSwitcher;

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
    private MiAdapter simpleAdapter;
    public TextView txt_hora;
    public Switch btn_switch;

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
        listaAlarmas = findViewById(R.id.listaAlarmas);
        getAlarmas();

        listaAlarmas.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                singleton.posicionAlarma = position;
                alertDialog = new AlertDialog.Builder(AlarmaActivity.this);
                alertDialog.setTitle("Confirmación");
                alertDialog.setMessage("¿Desea eliminar esta alarma?");
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        deleteAlarma();
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
        
        listaAlarmas.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(AlarmaActivity.this, "se presiono " + position, Toast.LENGTH_SHORT).show();
                singleton.posicionAlarma = position;
                dialogEditAlarma();
            }
        });

    }

    private void getAlarmas() {
        singleton.lista_hashmap_alarmas.clear();
        singleton.lista_alarmas.clear();
        SQLiteDatabase db = singleton.conn.getWritableDatabase();
        Cursor c = db.rawQuery(" SELECT * FROM "+ Constantes.NAME_TABLA_ALARMAS+" order by "+Constantes.CAMPO_ID, null);

        //Nos aseguramos de que existe al menos un registro
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put(Constantes.CAMPO_ID, c.getString(0));
                hm.put(Constantes.CAMPO_HORA, c.getString(1));
                hm.put(Constantes.CAMPO_DOMINGO, c.getString(2));
                hm.put(Constantes.CAMPO_LUNES, c.getString(3));
                hm.put(Constantes.CAMPO_MARTES, c.getString(4));
                hm.put(Constantes.CAMPO_MIERCOLES, c.getString(5));
                hm.put(Constantes.CAMPO_JUEVES, c.getString(6));
                hm.put(Constantes.CAMPO_VIERNES, c.getString(7));
                hm.put(Constantes.CAMPO_SABADO, c.getString(8));
                hm.put(Constantes.CAMPO_ACTIVADO, c.getString(9));
                singleton.lista_hashmap_alarmas.add(hm);
            } while(c.moveToNext());
        }

        if (singleton.lista_hashmap_alarmas.size() > 0){
            llenarLista();
        }

        simpleAdapter = new MiAdapter(this, R.layout.listview_alarmas, singleton.lista_alarmas);
        listaAlarmas.setAdapter(simpleAdapter);


    }

    private void llenarLista() {

        for(int i=0; i<singleton.lista_hashmap_alarmas.size(); i++){
            singleton.lista_alarmas.add(singleton.lista_hashmap_alarmas.listIterator(i).next().get(Constantes.CAMPO_HORA));
        }
    }

    public int obtenerIdLista(){
        int id = 0;
        int auxid = 0;
        boolean salir = false;
        for(int i=0; i<singleton.lista_hashmap_alarmas.size(); i++){
            id = Integer.parseInt(singleton.lista_hashmap_alarmas.listIterator(i).next().get(Constantes.CAMPO_ID));
            if(salir){
                if(id != auxid+1)
                    break;
                else{
                    auxid = id;
                }
            }
            if (id == 0 && !salir) {
                auxid = id;
                salir = true;
            }else if(!salir){
                return auxid; //retornamos un cero en caso de que el primer numero del arreglo sea diferente
            }

        }

        if(auxid == 0 && !salir){ //para la primera insercion
            return auxid;
        }
        return auxid+1; //retornamos id no encontrado en BD (se hace para no llenar la bd de basura)
    }

    public void deleteAlarma(){
        SQLiteDatabase db = singleton.conn.getWritableDatabase();
        String[] parametros = {singleton.lista_hashmap_alarmas.listIterator(singleton.posicionAlarma).next().get(Constantes.CAMPO_ID)};
        try {
            String id = singleton.lista_hashmap_alarmas.listIterator(singleton.posicionAlarma).next().get(Constantes.CAMPO_ID);
            int result = db.delete(Constantes.NAME_TABLA_ALARMAS,Constantes.CAMPO_ID+"=?", parametros);
            //creamos lista de alarmas
            if(result != -1) {
                Toast.makeText(this, "Alerta Eliminada", Toast.LENGTH_SHORT).show();
                getAlarmas();
            }else{

            }

        }catch (Exception e){
            Toast.makeText(this, "Ha ocurrrido un error, intenta más tarde", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        db.close();

    }



    public void updateAlarma(){
        SQLiteDatabase db = singleton.conn.getWritableDatabase();
        String[] parametros = {singleton.lista_hashmap_alarmas.listIterator(singleton.posicionAlarma).next().get(Constantes.CAMPO_ID)};
        ContentValues values = new ContentValues();
        values.put(Constantes.CAMPO_ACTIVADO, singleton.activado);
        try{
            int result = db.update(Constantes.NAME_TABLA_ALARMAS,values,Constantes.CAMPO_ID+"=?", parametros);


            if (result != -1){
                Toast.makeText(this, "update", Toast.LENGTH_SHORT).show();
                getAlarmas();
            }else{
                Toast.makeText(this, "Ha ocurrrido un error, intenta más tarde", Toast.LENGTH_SHORT).show();
                if (singleton.activado)
                    singleton.activado = false;
                else
                    singleton.activado = true;

                btn_switch.setChecked(singleton.activado);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
    }


    public void dialogAddAlarma(){
        limpiarDias();
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

    public void dialogEditAlarma() {
        singleton.diasSemana.put("domingos",Integer.parseInt(singleton.lista_hashmap_alarmas.listIterator(singleton.posicionAlarma).next().get(Constantes.CAMPO_DOMINGO)));
        singleton.diasSemana.put("lunes",Integer.parseInt(singleton.lista_hashmap_alarmas.listIterator(singleton.posicionAlarma).next().get(Constantes.CAMPO_LUNES)));
        singleton.diasSemana.put("martes",Integer.parseInt(singleton.lista_hashmap_alarmas.listIterator(singleton.posicionAlarma).next().get(Constantes.CAMPO_MARTES)));
        singleton.diasSemana.put("miercoles",Integer.parseInt(singleton.lista_hashmap_alarmas.listIterator(singleton.posicionAlarma).next().get(Constantes.CAMPO_MIERCOLES)));
        singleton.diasSemana.put("jueves",Integer.parseInt(singleton.lista_hashmap_alarmas.listIterator(singleton.posicionAlarma).next().get(Constantes.CAMPO_JUEVES)));
        singleton.diasSemana.put("viernes",Integer.parseInt(singleton.lista_hashmap_alarmas.listIterator(singleton.posicionAlarma).next().get(Constantes.CAMPO_VIERNES)));
        singleton.diasSemana.put("sabados",Integer.parseInt(singleton.lista_hashmap_alarmas.listIterator(singleton.posicionAlarma).next().get(Constantes.CAMPO_SABADO)));

        alertDialog = new AlertDialog.Builder(AlarmaActivity.this, R.style.DialogAlarm);
        final LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_add_alarma, null);
        Button btnGuardarAlarma = dialogView.findViewById(R.id.btnGuardarAlarma);
        Button btnCancelarAlarma = dialogView.findViewById(R.id.btnCancelarAlarma);

        final TimePicker timePicker = dialogView.findViewById(R.id.timepicker);
        final AlertDialog dialog = alertDialog.setView(dialogView).create();
        dialog.show();

        //llenamos datos
        String datoHora = singleton.lista_hashmap_alarmas.listIterator(singleton.posicionAlarma).next().get(Constantes.CAMPO_HORA);
        int hora = Integer.parseInt(datoHora.split(":")[0]);
        int minuto = Integer.parseInt(datoHora.split(":")[1]);;
        timePicker.setCurrentHour(hora);
        timePicker.setCurrentMinute(minuto);

        llenarDatosCheck(dialogView);
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
                singleton.horaAlarma = String.format("%02d",hora) + ":" + String.format("%02d", minutos);
                if(hora < 10 || minutos < 10)
                    singleton.horaAlarma = String.format("%02d",hora) + ":" + String.format("%02d", minutos);
                updateEditAlarma();
                getAlarmas();
                dialog.dismiss();
            }
        });
    }

    public void updateEditAlarma() {
        int domingo = singleton.diasSemana.get("domingos");
        int lunes = singleton.diasSemana.get("lunes");
        int martes = singleton.diasSemana.get("martes");
        int miercoles = singleton.diasSemana.get("miercoles");
        int jueves = singleton.diasSemana.get("jueves");
        int viernes = singleton.diasSemana.get("viernes");
        int sabado = singleton.diasSemana.get("sabados");
        SQLiteDatabase db = singleton.conn.getWritableDatabase();
        String[] parametros = {singleton.lista_hashmap_alarmas.listIterator(singleton.posicionAlarma).next().get(Constantes.CAMPO_ID)};
        ContentValues values = new ContentValues();
        values.put(Constantes.CAMPO_HORA, singleton.horaAlarma);
        values.put(Constantes.CAMPO_DOMINGO, domingo);
        values.put(Constantes.CAMPO_LUNES, lunes);
        values.put(Constantes.CAMPO_MARTES, martes);
        values.put(Constantes.CAMPO_MIERCOLES, miercoles);
        values.put(Constantes.CAMPO_JUEVES, jueves);
        values.put(Constantes.CAMPO_VIERNES, viernes);
        values.put(Constantes.CAMPO_SABADO, sabado);
        try{
            int result = db.update(Constantes.NAME_TABLA_ALARMAS,values,Constantes.CAMPO_ID+"=?", parametros);
            if (result != -1){
                Toast.makeText(this, "update", Toast.LENGTH_SHORT).show();
                getAlarmas();
            }else{
                Toast.makeText(this, "Ha ocurrrido un error, intenta más tarde", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
    }

    public void llenarDatosCheck(View dialogView) {
        CheckedTextView domingo = dialogView.findViewById(R.id.domingo);
        CheckedTextView lunes = dialogView.findViewById(R.id.lunes);
        CheckedTextView martes = dialogView.findViewById(R.id.martes);
        CheckedTextView miercoles = dialogView.findViewById(R.id.miercoles);
        CheckedTextView jueves = dialogView.findViewById(R.id.jueves);
        CheckedTextView viernes = dialogView.findViewById(R.id.viernes);
        CheckedTextView sabado = dialogView.findViewById(R.id.sabado);

        if(Integer.parseInt(singleton.lista_hashmap_alarmas.listIterator(singleton.posicionAlarma).next().get(Constantes.CAMPO_DOMINGO)) == 1){
            domingo.setCheckMarkDrawable(R.drawable.check_size); //icono
        }else{
            domingo.setCheckMarkDrawable(null); //icono
        }
        if(Integer.parseInt(singleton.lista_hashmap_alarmas.listIterator(singleton.posicionAlarma).next().get(Constantes.CAMPO_LUNES)) == 1){
            lunes.setCheckMarkDrawable(R.drawable.check_size); //icono
        }else{
            lunes.setCheckMarkDrawable(null); //icono
        }
        if(Integer.parseInt(singleton.lista_hashmap_alarmas.listIterator(singleton.posicionAlarma).next().get(Constantes.CAMPO_MARTES)) == 1){
            martes.setCheckMarkDrawable(R.drawable.check_size); //icono
        }else{
            martes.setCheckMarkDrawable(null); //icono
        }
        if(Integer.parseInt(singleton.lista_hashmap_alarmas.listIterator(singleton.posicionAlarma).next().get(Constantes.CAMPO_MIERCOLES)) == 1){
            miercoles.setCheckMarkDrawable(R.drawable.check_size); //icono
        }else{
            miercoles.setCheckMarkDrawable(null); //icono
        }
        if(Integer.parseInt(singleton.lista_hashmap_alarmas.listIterator(singleton.posicionAlarma).next().get(Constantes.CAMPO_JUEVES)) == 1){
            jueves.setCheckMarkDrawable(R.drawable.check_size); //icono
        }else{
            jueves.setCheckMarkDrawable(null); //icono
        }
        if(Integer.parseInt(singleton.lista_hashmap_alarmas.listIterator(singleton.posicionAlarma).next().get(Constantes.CAMPO_VIERNES)) == 1){
            viernes.setCheckMarkDrawable(R.drawable.check_size); //icono
        }else{
            viernes.setCheckMarkDrawable(null); //icono
        }
        if(Integer.parseInt(singleton.lista_hashmap_alarmas.listIterator(singleton.posicionAlarma).next().get(Constantes.CAMPO_SABADO)) == 1){
            sabado.setCheckMarkDrawable(R.drawable.check_size); //icono
        }else{
            sabado.setCheckMarkDrawable(null); //icono
        }

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
        int obtenerIdLista = obtenerIdLista();

        int domingo = singleton.diasSemana.get("domingos");
        int lunes = singleton.diasSemana.get("lunes");
        int martes = singleton.diasSemana.get("martes");
        int miercoles = singleton.diasSemana.get("miercoles");
        int jueves = singleton.diasSemana.get("jueves");
        int viernes = singleton.diasSemana.get("viernes");
        int sabado = singleton.diasSemana.get("sabados");
        singleton.activado = true;
        singleton.horaAlarma = String.format("%02d",hora) + ":" + String.format("%02d", minutos);
        if(hora < 10 || minutos < 10)
            singleton.horaAlarma = String.format("%02d",hora) + ":" + String.format("%02d", minutos);

        SQLiteDatabase db = singleton.conn.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constantes.CAMPO_ID,obtenerIdLista);
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
            getAlarmas();
            Toast.makeText(this, "Alerta Guardada", Toast.LENGTH_SHORT).show();
        }else{
            singleton.activado = false; //regresamos bandera
            Toast.makeText(this, "Ha ocurrrido un error, intenta más tarde", Toast.LENGTH_SHORT).show();
        }

    }

    public void limpiarDias(){
        //Asignar dias de la semana
        singleton.diasSemana.put("domingos",0);
        singleton.diasSemana.put("lunes",0);
        singleton.diasSemana.put("martes",0);
        singleton.diasSemana.put("miercoles",0);
        singleton.diasSemana.put("jueves",0);
        singleton.diasSemana.put("viernes",0);
        singleton.diasSemana.put("sabados",0);
    }
    private class MiAdapter extends ArrayAdapter<String>{
        private int layout;
        public MiAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            layout = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                txt_hora = (TextView) convertView.findViewById(R.id.txt_hora_alarma);
                btn_switch = (Switch) convertView.findViewById(R.id.switch_alarma);
                llenarSwitch(position);
                btn_switch.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Switch aSwitch = ((Switch) v);
                        singleton.posicionAlarma = position;
                        singleton.activado = ((Switch) v).isChecked();
                        updateAlarma();
                    }
                });

            }
            txt_hora.setText(getItem(position));
            return convertView;
        }

        private void llenarSwitch(int position) {
            boolean activado = false;
            if (Integer.parseInt(singleton.lista_hashmap_alarmas.listIterator(position).next().get(Constantes.CAMPO_ACTIVADO)) == 1) activado = true;
            btn_switch.setChecked(activado);
        }
    }
}
