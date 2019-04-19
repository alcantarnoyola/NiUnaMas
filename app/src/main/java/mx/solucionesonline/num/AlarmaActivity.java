package mx.solucionesonline.num;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.DialogInterface;
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
import android.widget.Toast;

public class AlarmaActivity extends AppCompatActivity {
    public Singleton singleton;
    private ArrayAdapter<String> arrayAdapter;
    private ListView listaAlarmas;
    public AlertDialog.Builder alertDialog;
    public ListView listViewCheckedTextView;
    String [] superStarNames = { "John Cena" , "Randy Orton" , "Triple H" , "Roman Reign" , "Sheamus" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarma);

        singleton = Singleton.getInstance();

        //Creamos accion al dar click en boton flotante add contacto
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAddAlarma();
            }
        }); //agregamos contactos
        singleton.arrayListAlarma.add("patatas");
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,singleton.arrayListAlarma);
        listaAlarmas = (ListView)findViewById(R.id.listaAlarmas);
        listaAlarmas.setAdapter(arrayAdapter);

    }

    public void dialogAddAlarma(){
        alertDialog = new AlertDialog.Builder(AlarmaActivity.this, R.style.DialogAlarm);
        final LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_add_alarma, null);
        /*EditText name = dialogView.findViewById(R.id.contacto_name);
        EditText number = dialogView.findViewById(R.id.contacto_numero);*/
        Button btnGuardarAlarma = (Button)dialogView.findViewById(R.id.btnGuardarAlarma);
        Button btnCancelarAlarma = (Button)dialogView.findViewById(R.id.btnCancelarAlarma);

        alertDialog.setView(dialogView).create();
        alertDialog.show();
        
        btnCancelarAlarma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AlarmaActivity.this, "Me presionaste Cancelar", Toast.LENGTH_SHORT).show();
            }
        });

        btnGuardarAlarma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AlarmaActivity.this, "Me presionaste Guardar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onCheckboxClicked(View view) {

        final CheckedTextView checkedTextView = ((CheckedTextView) view);

        checkedTextView.setChecked(!checkedTextView.isChecked()); //indica si se puede presionar (obligatorio)
        checkedTextView.setCheckMarkDrawable(checkedTextView.isChecked() ? android.R.drawable.checkbox_on_background : android.R.drawable.checkbox_off_background);

        String msg = getString(R.string.pre_msg) + " " + (checkedTextView.isChecked() ? getString(R.string.checked) : getString(R.string.unchecked));
        Toast.makeText(AlarmaActivity.this, msg + "::" + checkedTextView.getText(), Toast.LENGTH_SHORT).show();

    }
}
