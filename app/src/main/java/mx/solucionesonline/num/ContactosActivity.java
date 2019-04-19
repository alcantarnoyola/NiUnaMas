package mx.solucionesonline.num;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.support.design.widget.FloatingActionButton;

import mx.solucionesonline.num.Servicios.ServicioContactos;

public class ContactosActivity extends AppCompatActivity implements ServicioContactos.IServicioContactos {
    public AlertDialog.Builder alertDialog;
    private ArrayAdapter<String> arrayAdapter;
    private ListView listaContactos;
    public Singleton singleton;
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


        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,singleton.arrayListContactos);
        listaContactos = (ListView)findViewById(R.id.listaContactos);
        listaContactos.setAdapter(arrayAdapter);
        listaContactos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int posicion=i;

                alertDialog = new AlertDialog.Builder(ContactosActivity.this);
                alertDialog.setTitle("Confirmación");
                alertDialog.setMessage("¿Desea eliminar este contacto?");
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        singleton.selectOper = 1;
                        singleton.posicionContacto = posicion;
                        singleton.nombreContacto = singleton.arrayListContactos.get(posicion).split(":")[0];
                        singleton.numeroContacto = singleton.arrayListContactos.get(posicion).split(":")[1].split(" ")[1];
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
            Toast.makeText(this, "Contacto " + singleton.nombreContacto + " agregado", Toast.LENGTH_SHORT).show();
            singleton.arrayListContactos.add(singleton.nombreContacto + " : " + singleton.numeroContacto);
            arrayAdapter = new ArrayAdapter<String>(ContactosActivity.this,android.R.layout.simple_list_item_1,singleton.arrayListContactos);
            listaContactos.setAdapter(arrayAdapter);
        }else if (error == ServicioContactos.SUCCESS && singleton.selectOper == 1) {
            Toast.makeText(this, "Contacto " + singleton.nombreContacto +" eliminado", Toast.LENGTH_SHORT).show();
            singleton.arrayListContactos.remove(singleton.posicionContacto);
            arrayAdapter.notifyDataSetChanged();
        }else {
            Toast.makeText(this, "llego: " + response, Toast.LENGTH_LONG).show();
        }

        singleton.nombreContacto = "";
        singleton.numeroContacto = "";
    }

    public void dialogAddContacto(){
        alertDialog = new AlertDialog.Builder(ContactosActivity.this);
        final LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_add_contacto, null);
        EditText name = dialogView.findViewById(R.id.contacto_name);
        EditText number = dialogView.findViewById(R.id.contacto_numero);

        if (!singleton.nombreContacto.equals("")) {
            name.setText(singleton.nombreContacto);
        }else if (!singleton.numeroContacto.equals("")) {
            number.setText(singleton.numeroContacto);
        }
        alertDialog.setView(dialogView)
                // Add action buttons
                .setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText name = dialogView.findViewById(R.id.contacto_name);
                        EditText number = dialogView.findViewById(R.id.contacto_numero);
                        singleton.nombreContacto = name.getText().toString();
                        singleton.numeroContacto = number.getText().toString();

                        if (singleton.nombreContacto.equals("")) {
                            Toast.makeText(ContactosActivity.this, "Añade un nombre de contacto", Toast.LENGTH_SHORT).show();
                            dialogAddContacto();
                            return;
                        }else if (singleton.numeroContacto.length() < 10) {
                            Toast.makeText(ContactosActivity.this, "El número debe ser de 10 digitos", Toast.LENGTH_SHORT).show();
                            dialogAddContacto();
                            return;
                        }

                        singleton.selectOper = 0;
                        ServicioContactos servicioContactos = new ServicioContactos(ContactosActivity.this);
                        servicioContactos.execute("");
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }
}
