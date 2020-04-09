package mx.solucionesonline.num;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.support.design.widget.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mx.solucionesonline.num.Servicios.ServicioContactos;

public class ContactosActivity extends AppCompatActivity {
    public AlertDialog.Builder alertDialog;
    private ArrayAdapter<String> arrayAdapter;
    private ListView listaContactos;
    public Singleton singleton;
    private int AGREGAR_CONTACTO = 0;
    private int ELIMINAR_CONTACTO = 1;
    // Array of strings for ListView Title
    public String[] from = {"imagen", "nombre", "numero"};
    public int[] to = {R.id.listview_image,R.id.txt_nombre, R.id.txt_numero};
    public SimpleAdapter simpleAdapter;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactos);
        singleton = Singleton.getInstance();
        //Creamos accion al dar click en boton flotante add contacto
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                singleton.nombreContacto = "";
                singleton.numeroContacto = "";
                dialogAddContacto();
            }
        }); //agregamos contactos

        //simpleAdapter = new SimpleAdapter(getBaseContext(), singleton.lista_hashmap, R.layout.listview_contactos,from,to);
        listaContactos = findViewById(R.id.listaContactos);
        //listaContactos.setAdapter(simpleAdapter);

        getContactos();
        listaContactos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                singleton.posicionContacto = i;
                singleton.nombreContacto = singleton.lista_hashmap_contactos.listIterator(singleton.posicionContacto).next().get(Constantes.CAMPO_NOMBRE);
                singleton.numeroContacto = singleton.lista_hashmap_contactos.listIterator(singleton.posicionContacto).next().get(Constantes.CAMPO_NUMERO);
                alertDialog = new AlertDialog.Builder(ContactosActivity.this);
                alertDialog.setTitle("Confirmación");
                alertDialog.setMessage("¿Desea eliminar al contacto " + singleton.nombreContacto + "?");
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        singleton.selectOper = ELIMINAR_CONTACTO;
                        deleteContacto();
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

    public void deleteContacto(){
        SQLiteDatabase db = singleton.conn.getWritableDatabase();
        String[] parametros = {singleton.lista_hashmap_contactos.listIterator(singleton.posicionContacto).next().get(Constantes.CAMPO_ID)};
        try {
            String id = singleton.lista_hashmap_contactos.listIterator(singleton.posicionContacto).next().get(Constantes.CAMPO_ID);
            int result = db.delete(Constantes.NAME_TABLA_CONTACTOS,Constantes.CAMPO_ID+"=?", parametros);
            //creamos lista de alarmas
            if(result != -1) {
                Toast.makeText(this, "Contacto Eliminado", Toast.LENGTH_SHORT).show();
                getContactos();
            }else{

            }

        }catch (Exception e){
            Toast.makeText(this, "Ha ocurrrido un error, intenta más tarde", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        db.close();

    }

    private void getContactos() {
        singleton.lista_hashmap_contactos.clear();
        SQLiteDatabase db = singleton.conn.getWritableDatabase();
        Cursor c = db.rawQuery(" SELECT * FROM "+ Constantes.NAME_TABLA_CONTACTOS+" order by "+Constantes.CAMPO_ID, null);

        //Nos aseguramos de que existe al menos un registro
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put(Constantes.CAMPO_ID, c.getString(0));
                hm.put(Constantes.CAMPO_NOMBRE, c.getString(1));
                hm.put(Constantes.CAMPO_NUMERO, c.getString(2));
                hm.put("imagen", String.valueOf(R.drawable.contacto_generico));
                singleton.lista_hashmap_contactos.add(hm);
            } while(c.moveToNext());
        }

        simpleAdapter = new SimpleAdapter(getBaseContext(), singleton.lista_hashmap_contactos, R.layout.listview_contactos,from,to);
        listaContactos.setAdapter(simpleAdapter);
    }

    public void dialogAddContacto(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ContactosActivity.this);
        builder.setView(R.layout.dialog_add_contacto);
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(null);
        final Button btn_aceptar = dialog.findViewById(R.id.btn_aceptar);
        final Button btn_cancelar = dialog.findViewById(R.id.btn_cancelar);

        EditText name = dialog.findViewById(R.id.contacto_name);
        EditText number = dialog.findViewById(R.id.contacto_numero);
        if (!singleton.nombreContacto.equals("")) {
            name.setText(singleton.nombreContacto);
        }else if (!singleton.numeroContacto.equals("")) {
            number.setText(singleton.numeroContacto);
        }

        btn_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText name = dialog.findViewById(R.id.contacto_name);
                EditText number = dialog.findViewById(R.id.contacto_numero);
                singleton.nombreContacto = name.getText().toString();
                singleton.numeroContacto = number.getText().toString();

                if (singleton.nombreContacto.equals("")) {
                    Toast.makeText(ContactosActivity.this, "Añade un nombre de contacto", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    dialogAddContacto();
                    return;
                }else if (singleton.numeroContacto.length() != 10 ) {
                    Toast.makeText(ContactosActivity.this, "El número debe ser de 10 digitos", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    dialogAddContacto();
                    return;
                }

                /*singleton.selectOper = 0;
                ServicioContactos servicioContactos = new ServicioContactos(ContactosActivity.this);
                servicioContactos.execute("");*/
                insertarContacto();
                dialog.dismiss();
            }
        });

        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public void insertarContacto() {
        int obtenerIdLista = obtenerIdLista();

        SQLiteDatabase db = singleton.conn.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constantes.CAMPO_ID,obtenerIdLista);
        values.put(Constantes.CAMPO_NOMBRE,singleton.nombreContacto);
        values.put(Constantes.CAMPO_NUMERO,singleton.numeroContacto);
        Long result = Long.valueOf(-1);
        try {
            result = db.insert(Constantes.NAME_TABLA_CONTACTOS, Constantes.CAMPO_ID, values);
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();

        if(result != -1){ //REGISTRO EXITOSO
            //creamos lista de alarmas
            getContactos();
            singleton.nombreContacto = "";
            singleton.numeroContacto = "";
            Toast.makeText(this, "Contacto Guardado", Toast.LENGTH_SHORT).show();
        }else{
            singleton.activado = false; //regresamos bandera
            Toast.makeText(this, "Ha ocurrrido un error, intenta más tarde", Toast.LENGTH_SHORT).show();
        }
    }

    private int obtenerIdLista() {
        int id = 0;
        int auxid = 0;
        boolean salir = false;
        for(int i=0; i<singleton.lista_hashmap_contactos.size(); i++){
            id = Integer.parseInt(singleton.lista_hashmap_contactos.listIterator(i).next().get(Constantes.CAMPO_ID));
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
}
