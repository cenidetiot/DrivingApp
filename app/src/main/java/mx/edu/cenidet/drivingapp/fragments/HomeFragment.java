package mx.edu.cenidet.drivingapp.fragments;

import android.Manifest;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import mx.edu.cenidet.drivingapp.R;
import mx.edu.cenidet.drivingapp.activities.HomeActivity;
import mx.edu.cenidet.drivingapp.services.SendDataService;
import www.fiware.org.ngsi.controller.AlertController;
import www.fiware.org.ngsi.datamodel.entity.Alert;
import www.fiware.org.ngsi.datamodel.entity.Zone;
import www.fiware.org.ngsi.httpmethodstransaction.Response;
import www.fiware.org.ngsi.utilities.DevicePropertiesFunctions;
import www.fiware.org.ngsi.utilities.Functions;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements SendDataService.SendDataMethods {
    private View rootView;
    private double latitude, longitude;
    private double speedMS, speedKmHr;
    private IntentFilter filter;
    private static final String STATUS = "Status";
    private Context context;
    private SendDataService sendDataService;
    private TextView tvDetailCampus;
    private ImageView imagenViewDetailCampus;
    //private FloatingActionButton btnFloating;
    private AlertController alertController;

    //Foto de contacto
    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private Uri uriContact;
    private String contactID; // contacts unique ID
    private String id;

    public HomeFragment() {
        context = HomeActivity.MAIN_CONTEXT;
        sendDataService = new SendDataService(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        tvDetailCampus = (TextView) rootView.findViewById(R.id.tvDetailCampus);
        imagenViewDetailCampus = (ImageView) rootView.findViewById(R.id.imagenViewDetailCampus);

        //contactPhoto = (ImageView) rootView.findViewById(R.id.contactPhoto);
        /*if (contactPhoto != null)
            contactPhoto.setImageBitmap(retrieveContactPhoto(context));*/

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void sendLocationSpeed(double latitude, double longitude, double speedMS, double speedKmHr) {
        this.latitude = latitude;
        this.longitude = longitude;
        // Log.i("STATUS: ","HomeFragment-sendLocationSpeed");
    }

    @Override
    public void detectZone(Zone zone, boolean statusLocation) {
        if (statusLocation == true) {
            //Log.i("STATUS: ","Campus Name: "+campus.getName()+" statusLocation: "+statusLocation);
            if (imagenViewDetailCampus != null && tvDetailCampus != null) {
                imagenViewDetailCampus.setImageResource(R.mipmap.ic_inside_foreground);
                tvDetailCampus.setText(context.getString(R.string.message_name_campus) + ": " + zone.getName().getValue() + "\n" + context.getString(R.string.message_address_campus) + ": " + zone.getAddress().getValue());
            }
        } else {
            Log.i("STATUS 1: ", "DetectCampus...!" + statusLocation);
            if (imagenViewDetailCampus != null && tvDetailCampus != null) {
                imagenViewDetailCampus.setImageResource(R.mipmap.ic_outside_foreground);
                tvDetailCampus.setText(context.getString(R.string.message_any_campus));
            }
            //tvDetailCampus.setText("No te encuentras en ningun campus");
        }
    }

    @Override
    public void sendDataAccelerometer(double ax, double ay, double az) {
        //Log.i("STATUS 1: ","ax: "+ax+" ay: "+ay+" az: "+az);
    }

    @Override
    public void sendEvent(String event) {
    }



    //Android - Get Contact Photo from phone number


    /*public Bitmap retrieveContactPhoto(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        String contactId = null;
        //String number = "7471054389";
        String number = "7471154097";

        String phone = ContactsContract.CommonDataKinds.Phone.NUMBER;
        Log.i("PHONE: ","---------------------------------: "+phone);
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID};

        Cursor cursor =
                contentResolver.query(
                        uri,
                        projection,
                        null,
                        null,
                        null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
            }
            cursor.close();
        }

        Bitmap photo = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_alert_critical);

        try {
            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(contactId)));

            if (inputStream != null) {
                photo = BitmapFactory.decodeStream(inputStream);
            }

            assert inputStream != null;
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return photo;

    }*/

}
