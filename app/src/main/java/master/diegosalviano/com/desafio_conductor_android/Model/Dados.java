package master.diegosalviano.com.desafio_conductor_android.Model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Dados {

    public static String API_USUARIO = "https://2hm1e5siv9.execute-api.us-east-1.amazonaws.com/dev/users/profile";
    public static String API_SALDO = "https://2hm1e5siv9.execute-api.us-east-1.amazonaws.com/dev/resume";
    public static String API_EXTRATO = "https://2hm1e5siv9.execute-api.us-east-1.amazonaws.com/dev/card-statement?";


    public static final String data(String s) {
        SimpleDateFormat dateFormat =  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        Date date = null;
        try {
            date = dateFormat.parse(s);
            dateFormat.applyPattern("d MMM");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateFormat.format(date);

    }

    public static  final String valor(String s){
        String valor = "R$ "+String.format("%.2f", Double.parseDouble(s));
        return valor;
    }
}
