package mx.edu.cenidet.drivingapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import mx.edu.cenidet.cenidetsdk.entities.Campus;
import mx.edu.cenidet.drivingapp.R;
import www.fiware.org.ngsi.datamodel.entity.Zone;

/**
 * Created by Cipriano on 3/18/2018.
 */

public class MyAdapterCampus extends BaseAdapter {
    private Context context;
    private int layout;
    private List<Zone> listZone;

    public MyAdapterCampus(Context context, int layout, List<Zone> listZone){
        this.context = context;
        this.layout = layout;
        this.listZone = listZone;
    }
    @Override
    public int getCount() {
        return this.listZone.size();
    }

    @Override
    public Object getItem(int position) {
        return this.listZone.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Copiamos la vista
        View view = convertView;
        //Inflamos la vista que nos ha llegado con nuestro layout personalizado
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);
        view = layoutInflater.inflate(R.layout.list_zone, null);

        //Nos traemos el valor dependiente de la posición
        String id = listZone.get(position).getIdZone();
        String name = listZone.get(position).getName().getValue();

        //Referenciamos el elemento a modificar y lo rellenamos.
        TextView textViewCampus = (TextView) view.findViewById(R.id.textViewZone);
        textViewCampus.setText(name);

        return view;
    }
}
