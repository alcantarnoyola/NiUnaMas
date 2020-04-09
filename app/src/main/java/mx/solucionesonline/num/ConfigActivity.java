package mx.solucionesonline.num;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mx.solucionesonline.num.Servicios.ServicioFormularioPerfil;

public class ConfigActivity extends AppCompatActivity implements ServicioFormularioPerfil.IServicioFormularioPerfil {

    public TextView txt_titulo;
    public MiAdapter simpleadapter;
    public Singleton singleton;
    private ListView listView;
    private EditText nombre;
    private EditText apellidos;
    private EditText email;
    private EditText colonia;
    private EditText municipio;
    private EditText estado;
    private Spinner gradoestudios;
    private AlertDialog dialog;

    public AlertDialog.Builder alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        singleton = Singleton.getInstance();

        listView = findViewById(R.id.listaConfig);
        simpleadapter = new MiAdapter(this, R.layout.listview_config, singleton.menu);

        listView.setAdapter(simpleadapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch(position){
                    case 0://completa perfil
                        Toast.makeText(ConfigActivity.this, ""+position + "***", Toast.LENGTH_SHORT).show();
                        completaPerfil();
                        break;
                    case 1://aviso de privacidad
                        Toast.makeText(ConfigActivity.this, ""+position + "***", Toast.LENGTH_SHORT).show();
                        break;
                    case 2://acerca de
                        Toast.makeText(ConfigActivity.this, ""+position + "***", Toast.LENGTH_SHORT).show();
                        break;
                    case 3://Equipo Soon
                        Toast.makeText(ConfigActivity.this, ""+position + "***", Toast.LENGTH_SHORT).show();
                        break;
                    case 4://darse de baja
                        Toast.makeText(ConfigActivity.this, ""+position + "***", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });


    }

    private void completaPerfil() {
        alertDialog = new AlertDialog.Builder(ConfigActivity.this, R.style.DialogAlarm);
        final LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_form_perfil, null);
        Button btnActualizarPerfil = dialogView.findViewById(R.id.btn_actualizar);
        Button btnCancelarPerfil = dialogView.findViewById(R.id.btn_cancelar);
        nombre = dialogView.findViewById(R.id.txt_nombre);
        apellidos = dialogView.findViewById(R.id.txt_apellidos);
        email = dialogView.findViewById(R.id.txt_mail);
        colonia = dialogView.findViewById(R.id.txt_colonia);
        municipio = dialogView.findViewById(R.id.txt_municipio);
        estado = dialogView.findViewById(R.id.txt_estado);
        gradoestudios = dialogView.findViewById(R.id.gradoEstudios);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, singleton.lista_gradoestudios);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gradoestudios.setAdapter(dataAdapter);
        dialog = alertDialog.setView(dialogView).create();
        dialog.show();

        btnActualizarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llenarVariablesPerfil();
                ServicioFormularioPerfil servicioFormularioPerfil = new ServicioFormularioPerfil(ConfigActivity.this);
                servicioFormularioPerfil.execute("");
            }
        });

        btnCancelarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void llenarVariablesPerfil() {
        singleton.nombre = nombre.getText().toString();
        singleton.apellidos = apellidos.getText().toString();
        singleton.email = email.getText().toString();
        singleton.colonia = colonia.getText().toString();
        singleton.municipio = municipio.getText().toString();
        singleton.estado = estado.getText().toString();
        singleton.gradoestudios = gradoestudios.getSelectedItem().toString();
    }

    @Override
    public void servicioFormularioPerfilFinished(int error, String response) {
        dialog.dismiss();
        if (error == ServicioFormularioPerfil.SUCCESS){
            Toast.makeText(this, "Actualización de perfil correcta", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
        }
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
                txt_titulo = (TextView) convertView.findViewById(R.id.txt_titulo);
            }

            txt_titulo.setText(getItem(position));
            return convertView;
        }
    }
}
