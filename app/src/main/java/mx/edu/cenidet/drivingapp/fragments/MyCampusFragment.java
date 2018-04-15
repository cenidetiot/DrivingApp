package mx.edu.cenidet.drivingapp.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import mx.edu.cenidet.cenidetsdk.db.SQLiteDrivingApp;
import mx.edu.cenidet.cenidetsdk.entities.Campus;
import mx.edu.cenidet.drivingapp.R;
import mx.edu.cenidet.drivingapp.activities.HomeActivity;
import mx.edu.cenidet.drivingapp.activities.MapDetailActivity;
import mx.edu.cenidet.drivingapp.adapters.MyAdapterCampus;
import www.fiware.org.ngsi.datamodel.entity.Zone;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyCampusFragment extends Fragment {
    private View rootView;
    private ListView listViewZone;
    private List<Zone> listZone;
    private SQLiteDrivingApp sqLiteDrivingApp;
    private Context context;
    private MyAdapterCampus myAdapterCampus;
    private AdapterView.AdapterContextMenuInfo info;
    private String name, location, centerPoint;

    public MyCampusFragment() {
        // Required empty public constructor
        context = HomeActivity.MAIN_CONTEXT;
        sqLiteDrivingApp = new SQLiteDrivingApp(context);
        listZone = sqLiteDrivingApp.getAllZone();
        /*if(listCampus.size() > 0){
            for(int i=0; i<listCampus.size(); i++){
                Log.i("Status: ", "My Campus name: "+listCampus.get(i).getName());
            }
        }*/
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_campus, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listViewZone = (ListView) rootView.findViewById(R.id.listViewZone);
        myAdapterCampus = new MyAdapterCampus(context, R.layout.list_zone, listZone);
        listViewZone.setAdapter(myAdapterCampus);
        registerForContextMenu(listViewZone);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getActivity().getMenuInflater();

        info =  (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(listZone.get(info.position).getName().getValue());
        menuInflater.inflate(R.menu.campus_map_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_see_map:
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                name = listZone.get(info.position).getName().getValue();
                location = listZone.get(info.position).getLocation().getValue();
                centerPoint = listZone.get(info.position).getCenterPoint().getValue();
                Intent intent = new Intent(context, MapDetailActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("location", location);
                intent.putExtra("centerPoint", centerPoint);
                startActivity(intent);
                //Toast.makeText(getContext(), name, Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_delete_campus:
                Toast.makeText(getContext(), "Delete Campus", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onContextItemSelected(item);
    }
}
