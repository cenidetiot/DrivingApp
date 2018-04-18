package www.fiware.org.ngsi.utilities;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Cipriano on 10/19/2017.
 */

public class ApplicationPreferences {

    public ApplicationPreferences (){

    }

    public void saveOnPreferenceBoolean(Context context, String preferenceName, String preferenceKey, boolean valor) {
        SharedPreferences settings = context.getSharedPreferences(preferenceName, context.MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putBoolean(preferenceKey, valor);
        editor.commit();
        editor.apply();
    }

    public void saveOnPreferenceString(Context context, String preferenceName, String preferenceKey, String valor){
        SharedPreferences settings = context.getSharedPreferences(preferenceName, context.MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putString(preferenceKey, valor);
        editor.commit();
        editor.apply();
    }

    public boolean getPreferenceBoolean(Context context, String preferenceName, String preferenceKey) {
        SharedPreferences preferences = context.getSharedPreferences(preferenceName, context.MODE_PRIVATE);
        return  preferences.getBoolean(preferenceKey, false);
    }

    public String getPreferenceString(Context context, String preferenceName, String preferenceKey) {
        SharedPreferences preferences = context.getSharedPreferences(preferenceName, context.MODE_PRIVATE);
        return  preferences.getString(preferenceKey, "");
    }

    public void removeSharedPreferences(Context context, String  preferenceName){
        SharedPreferences preferences = context.getSharedPreferences(preferenceName, context.MODE_PRIVATE);
        preferences.edit().clear().apply();
    }

}
