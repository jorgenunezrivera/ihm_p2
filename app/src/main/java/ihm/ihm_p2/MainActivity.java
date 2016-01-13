package ihm.ihm_p2;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends Activity implements OnFragmentInteractionListener{
    boolean tablet = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Comprobar si esta cargando lal ayout de tablet o la de telefono
        List_Fragment listapueblos = new List_Fragment();
        if(findViewById(R.id.layout_info_tablet)!=null) {
            tablet=true;
            getFragmentManager().beginTransaction().add(R.id.layout_list_tablet, listapueblos).commit();
            System.out.println("tablet");
        }
        else {
            getFragmentManager().beginTransaction().add(R.id.frag_layout_phone, listapueblos).commit();
            System.out.println("phone");
        }
    }

    @Override
    public void onFragmentInteraction(String nombre) {
        info_fragment infopueblo = new info_fragment();
        Bundle args = new Bundle();
        args.putString("name", nombre);
        infopueblo.setArguments(args);
        new modelo().execute(nombre);
        if(tablet){

            info_fragment info_frag = (info_fragment)getFragmentManager().findFragmentById(R.id.layout_info_tablet);
            if(info_frag==null || info_frag.get_name()!=nombre) {
                info_frag = info_fragment.newInstance(nombre);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.layout_info_tablet, infopueblo);
                transaction.addToBackStack(null);
                transaction.commit();
                TextView tv = (TextView) findViewById(R.id.info_toolbar_tablet);
                tv.setText(nombre);

            }
        }
        else {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.frag_layout_phone, infopueblo);
            transaction.addToBackStack(null);
            transaction.commit();
            TextView tv = (TextView) findViewById(R.id.toolbar_text);
            tv.setText(nombre);
            }
        }



    private class modelo extends AsyncTask<String,String,String> {

        public modelo(){

        }

        @Override
        protected String doInBackground(String... params) {
            String id;
                try {
                    id = get_places(params[0]);
                }catch (Exception e){
                    return(e.getLocalizedMessage());
                }
            return get_weather(id);
        }

        protected void onPostExecute(String result) {
                    updateInfotext(result);
                    }

        protected void updateInfotext(String newtext){
            TextView textview;
            if(tablet){
                textview= (TextView) getFragmentManager().findFragmentById(R.id.layout_info_tablet).getView();
            }
            else{
                textview = (TextView) getFragmentManager().findFragmentById(R.id.frag_layout_phone).getView();
            }

            textview.setText(newtext);

        }

        protected String format_info(JSONObject day) throws org.json.JSONException{
            String sky,skytext,temperature,rain,wind_module,wind_dir;
            int skyid;
            String output;
            sky=day.getJSONArray("variables").getJSONObject(0).getJSONArray("values").getJSONObject(0).getString("value");
            skyid=getResources().getIdentifier(sky, "string", MainActivity.this.getPackageName());
            skytext=getResources().getString(skyid);
            temperature=day.getJSONArray("variables").getJSONObject(1).getJSONArray("values").getJSONObject(0).getString("value");
            rain=day.getJSONArray("variables").getJSONObject(2).getJSONArray("values").getJSONObject(0).getString("value");
            wind_module=day.getJSONArray("variables").getJSONObject(3).getJSONArray("values").getJSONObject(0).getString("moduleValue");
            wind_dir=day.getJSONArray("variables").getJSONObject(3).getJSONArray("values").getJSONObject(0).getString("directionValue");
            output = getResources().getString(R.string.sky_state) + ": "+ skytext + "\n"+getResources().getString(R.string.temperature)+ "="+temperature + "º\n"+getResources().getString(R.string.rain)+"="+rain+" lm2\n"+getResources().getString(R.string.wind_speed)+"="+wind_module + " km/h\n"+getResources().getString(R.string.wind_direction)+"="+ wind_dir+ "º";
            return output;
        }


        public String get_places(String nombre) throws Exception {
            String apiurl = "http://servizos.meteogalicia.es/apiv3/";
            String apikey = "API_KEY=d29W0ZS4E1YZx6mY4pPAHF5T7kHns695nqX8sflMt1H4XhFq3AWU0v4gWZlZJ8Ov";
            String nombreurl = nombre.replace(" ","%20");
            JSONObject places_array;
            String id="";
            String address=""+apiurl+"findPlaces?location="+nombreurl+"&"+apikey;
            System.out.println(address);
            System.out.println(apiurl);
            try {
                places_array = JsonReader.readJsonFromUrl(address);
            }catch (IOException ioe){
                throw new Exception(getResources().getString(R.string.connection_error));
            }catch (JSONException je){
                throw new Exception(getResources().getString(R.string.Json_error));
            }
            try {
                System.out.println(places_array.toString());
                JSONArray pueblos = places_array.getJSONArray("features");
                //Cuando solo hay un pueblo se utiliza ese. Esto evita fallos para nombres compuestos como a guarda y poboa do caramiñal.
                //Cuando hay varios busca aquel cuyo municipio coincida con la búsqueda
                if(pueblos.length()>1) {
                    for (int i = 0; i < pueblos.length(); i++) {
                        System.out.println(pueblos.getJSONObject(i).getJSONObject("properties").getString("municipality") + " ..." + nombre);

                        if (pueblos.getJSONObject(i).getJSONObject("properties").getString("municipality").equalsIgnoreCase(nombre)) {
                            id = pueblos.getJSONObject(i).getJSONObject("properties").getString("id");

                        }
                    }
                }
                else{
                    id = pueblos.getJSONObject(0).getJSONObject("properties").getString("id");
                }
            }catch(Exception e) {
                throw new Exception(getResources().getString(R.string.Json_error));
            }
            return id;
        }

        public String get_weather(String id){
            String apiurl = "http://servizos.meteogalicia.es/apiv3/";
            String apikey = "API_KEY=d29W0ZS4E1YZx6mY4pPAHF5T7kHns695nqX8sflMt1H4XhFq3AWU0v4gWZlZJ8Ov";
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
            Date date = new Date();
            Date date2 = new Date(date.getTime()+3600000);
            String timestring = "startTime="+ format.format(date) + "&endTime="  +format.format(date2);
            String info;
            JSONObject day;
            JSONObject weather_array;
            String address=""+apiurl+"getNumericForecastInfo?locationIds="+id+"&" + timestring + "&"+apikey;
            System.out.println(address);
            System.out.println(apiurl);
            try {
               weather_array = JsonReader.readJsonFromUrl(address);
            }catch (IOException ioe){
                return getResources().getString(R.string.connection_error);
            }catch (JSONException je){
                return getResources().getString(R.string.Json_error);
            }
            try {
                System.out.println(weather_array.toString());
                day = weather_array.getJSONArray("features").getJSONObject(0).getJSONObject("properties").getJSONArray("days").getJSONObject(0);
                System.out.println(day.toString());
                info=format_info(day);
            }catch(Exception e) {
                return getResources().getString(R.string.Json_error);
            }
            return info;
        }
    }

        //cambio de layout∫
    }



