package mx.edu.cenidet.drivingapp.adapters;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import mx.edu.cenidet.drivingapp.R;
import www.fiware.org.ngsi.datamodel.entity.Alert;

/**
 * Created by Cipriano on 4/8/2018.
 */

public class MyAdapterAlerts extends BaseAdapter {

    private Context context;
    private int layout;
    private List<Alert> listAlerts;

    public MyAdapterAlerts(Context context, int layout, List<Alert> listAlerts){
        this.context = context;
        this.layout = layout;
        this.listAlerts = listAlerts;
    }
    @Override
    public int getCount() {
        return this.listAlerts.size();
    }

    @Override
    public Object getItem(int position) {
        return this.listAlerts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        //Inflamos la vista que nos ha llegado con nuestro layout personalizado
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);
        view = layoutInflater.inflate(R.layout.list_alerts, null);

        //Nos traemos el valor dependiente de la posición
        String category = listAlerts.get(position).getCategory().getValue();
        String description = listAlerts.get(position).getDescription().getValue();
        String severity = listAlerts.get(position).getSeverity().getValue();

        //Referenciamos el elemento a modificar y lo rellenamos.
        ImageView imageViewAlerts = (ImageView) view.findViewById(R.id.imageViewAlerts);
        switch (severity){
            case "informational":
                imageViewAlerts.setImageResource(R.drawable.ic_alert_informational);
                break;
            case "low":
                imageViewAlerts.setImageResource(R.drawable.ic_alert_low);
                break;
            case "medium":
                imageViewAlerts.setImageResource(R.drawable.ic_alert_medium);
                break;
            case "high":
                imageViewAlerts.setImageResource(R.drawable.ic_alert_high);
                break;
            case "critical":
                imageViewAlerts.setImageResource(R.drawable.ic_alert_critical);
                break;
        }

        TextView textViewCategory = (TextView) view.findViewById(R.id.textViewCategory);
        textViewCategory.setText(category);
        TextView textViewDescription = (TextView) view.findViewById(R.id.textViewDescription);
        textViewDescription.setText(description);


        return view;
    }
}
