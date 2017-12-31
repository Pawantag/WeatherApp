package weatherapp.com.weatherapp.commonUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;

/**
 * Created by prave on 30-12-2017.
 */

public class Utils {
    int statusCode;

    public static void setVolleyRetryPolicy(Request<?> req) {
        req.setRetryPolicy(new DefaultRetryPolicy(40000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }


}
