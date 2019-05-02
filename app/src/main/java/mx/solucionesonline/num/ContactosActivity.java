package mx.solucionesonline.num;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

public class ContactosActivity extends AppCompatActivity implements ServicioContactos.IServicioContactos {
    public AlertDialog.Builder alertDialog;
    private ArrayAdapter<String> arrayAdapter;
    private ListView listaContactos;
    public Singleton singleton;
    private int AGREGAR_CONTACTO = 0;
    private int ELIMINAR_CONTACTO = 1;
    // Array of strings for ListView Title
    public String[] from = {"Imagen", "Nombre", "Numero"};
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
                dialogAddContacto();
            }
        }); //agregamos contactos

        simpleAdapter = new SimpleAdapter(getBaseContext(), singleton.lista_hashmap, R.layout.listview_contactos,from,to);
        listaContactos = findViewById(R.id.listaContactos);
        listaContactos.setAdapter(simpleAdapter);

        /*arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,singleton.arrayListContactos);
        listaContactos = (ListView)findViewById(R.id.listaContactos);
        listaContactos.setAdapter(arrayAdapter);*/
        listaContactos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                singleton.posicionContacto = i;
                singleton.nombreContacto = singleton.lista_hashmap.get(singleton.posicionContacto).values().toArray()[1].toString();
                singleton.numeroContacto = singleton.lista_hashmap.get(singleton.posicionContacto).values().toArray()[2].toString();
                alertDialog = new AlertDialog.Builder(ContactosActivity.this);
                alertDialog.setTitle("Confirmación");
                alertDialog.setMessage("¿Desea eliminar al contacto " + singleton.nombreContacto + "?");
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        singleton.selectOper = ELIMINAR_CONTACTO;
                        ServicioContactos servicioContactos = new ServicioContactos(ContactosActivity.this);
                        servicioContactos.execute("");
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

    @Override
    public void servicioContactosFinished(int error, String response) {

        if (error == ServicioContactos.SUCCESS && singleton.selectOper == 0) {
            dialog.dismiss();
            Toast.makeText(this, "Contacto " + singleton.nombreContacto + " agregado", Toast.LENGTH_LONG).show();
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("Nombre", singleton.nombreContacto);
            hm.put("Numero", singleton.numeroContacto);
            hm.put("Imagen", String.valueOf(R.drawable.contacto_generico));
            singleton.lista_hashmap.add(hm);
            simpleAdapter = new SimpleAdapter(getBaseContext(), singleton.lista_hashmap, R.layout.listview_contactos,from,to);
            listaContactos.setAdapter(simpleAdapter);
        }else if (error == ServicioContactos.SUCCESS && singleton.selectOper == 1) {
            Toast.makeText(this, "Contacto " + singleton.nombreContacto +" eliminado", Toast.LENGTH_LONG).show();
            singleton.lista_hashmap.remove(singleton.posicionContacto);
            simpleAdapter.notifyDataSetChanged();
        }else {
            Toast.makeText(this, "llego: " + response, Toast.LENGTH_LONG).show();
        }

        singleton.nombreContacto = "";
        singleton.numeroContacto = "";
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
                }else if (singleton.numeroContacto.length() < 10) {
                    Toast.makeText(ContactosActivity.this, "El número debe ser de 10 digitos", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    dialogAddContacto();
                    return;
                }

                singleton.selectOper = 0;
                ServicioContactos servicioContactos = new ServicioContactos(ContactosActivity.this);
                servicioContactos.execute("");
            }
        });

        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
}
