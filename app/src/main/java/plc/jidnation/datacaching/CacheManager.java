package plc.jidnation.datacaching;

import android.content.Context;
import android.content.SharedPreferences;

public class CacheManager {
    SharedPreferences sharedPreferences;

    public CacheManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences("jidnation", Context.MODE_PRIVATE);
    }

    public void storeData(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public String retrieveData(String key) {
        return sharedPreferences.getString(key, null);
    }
}
