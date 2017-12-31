package weatherapp.com.weatherapp.FontFamilly;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import weatherapp.com.weatherapp.R;


/**
 * Created by cdi-user on 8/9/17.
 */

public class CalligraphyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/ReemKufi-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
