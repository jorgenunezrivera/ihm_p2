package ihm.ihm_p2;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;


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
        args.putString("name",nombre);
        infopueblo.setArguments(args);

        if(tablet){

            info_fragment info_frag = (info_fragment)getFragmentManager().findFragmentById(R.id.layout_info_tablet);
            if(info_frag==null || info_frag.get_name()!=nombre) {
                info_frag = info_fragment.newInstance(nombre);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.layout_info_tablet, infopueblo);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        }
        else{
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.frag_layout_phone, infopueblo);
                transaction.addToBackStack(null);
                transaction.commit();
            }

        }


        //cambio de layout∫
    }



