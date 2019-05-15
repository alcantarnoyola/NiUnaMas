package mx.solucionesonline.num;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ConfigActivity extends AppCompatActivity {

    public TextView txt_titulo;
    public MiAdapter simpleadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        ArrayList<String> values =  new ArrayList<String>();
        values.add("Completa tu perfil");
        values.add("Aviso de privacidad");
        values.add("Acerca de");
        values.add("Equipo Soon");

        ListView listView = findViewById(R.id.listaConfig);
        simpleadapter = new MiAdapter(this, R.layout.listview_config, values);

        listView.setAdapter(simpleadapter);

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
