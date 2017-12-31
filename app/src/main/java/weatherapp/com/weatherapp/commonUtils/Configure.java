package weatherapp.com.weatherapp.commonUtils;

/**
 * Created by prave on 30-12-2017.
 */

public class Configure {
    public static String APPID = "Add Your API KEY HERE";
    public static String BASE_URL = "http://api.openweathermap.org/data/2.5/";
    public static String GET_WEATHER_DETAILS_BY_ZIP_URL = BASE_URL + "forecast?zip=%s&APPID=" + APPID + "&units=metric";
    public static String GET_WEATHER_DETAILS_BY_NAME_URL = BASE_URL + "forecast?q=%s&APPID=" + APPID + "&units=metric";
    public static String GET_ICON = "http://openweathermap.org/img/w/";

}
