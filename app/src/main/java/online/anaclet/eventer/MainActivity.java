package online.anaclet.eventer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button scnBtn;

    TextView event, valid, connected;

    public String baseURL = "http://192.168.0.102/events/";

    boolean isValid = false;

    RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scnBtn = findViewById(R.id.start_scann_btn);
        event = findViewById(R.id.event_id);
        valid = findViewById(R.id.valid_id);
        connected = findViewById(R.id.connect_txt);

        scnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
                intentIntegrator.setPrompt("For flash use press volume up key!");
                intentIntegrator.setBeepEnabled(true);
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.setCaptureActivity(Capture.class);
                intentIntegrator.initiateScan();
            }
        });

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        if(isNetworkAvailable()){
            connected.setTextColor(MainActivity.this.getResources().getColor(R.color.green));
        }else{
            connected.setTextColor(MainActivity.this.getResources().getColor(R.color.red));
            connected.setText("Connect to internet and restart app.");
        }
//        SyncThread thread = new SyncThread();
//        thread.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(
                requestCode, resultCode, data
        );

        if(intentResult.getContents() != null){
            String json = intentResult.getContents();
            try {
                JSONObject jsonObject = new JSONObject(json);
                checkValid(jsonObject.getString("ticket_no"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(getApplicationContext(), "OOPS... You didn't scann anything!", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void checkValid(String ticket_no) {
        String url = baseURL + "check-ticket-valid?ticket_no=" + ticket_no;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("valid").equals("yes")){
                                valid.setText("VALID");
                                valid.setTextColor(MainActivity.this.getResources().getColor(R.color.green));
                                event.setText(response.getString("event"));
                                event.setTextColor(MainActivity.this.getResources().getColor(R.color.green));
                            }else{
                                valid.setText("NOT VALID");
                                valid.setTextColor(MainActivity.this.getResources().getColor(R.color.red));
                                event.setText(response.getString("event"));
                                event.setTextColor(MainActivity.this.getResources().getColor(R.color.red));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error", error.toString());
            }
        });
        requestQueue.add(jsonObjectRequest);
    }


    class SyncThread extends Thread{
        RequestQueue requestQueue;
        public String baseURL = "http://192.168.0.102/events/";
        ArrayList<Ticket> responseArray = new ArrayList<>();

        @Override
        public void run() {
            DBHandler dbHandler = new DBHandler(MainActivity.this);
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            Ticket ticket = dbHandler.getLastTicket();
            getUnSyncedTickets(ticket);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void getUnSyncedTickets(Ticket ticket) {
            String ticket_no = "";
            if(ticket == null){
                ticket_no = "none";
            }else{
                ticket_no = ticket.getTicket_no();
            }
            String url = baseURL + "get-tickets?last=" + ticket_no;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                DBHandler dbHandler = new DBHandler(MainActivity.this);
                                JSONArray jsonArray = response.getJSONArray("tickets");
                                for(int o = 0; o < jsonArray.length(); o++){
                                    JSONObject jsonObject = jsonArray.getJSONObject(o);
                                    Ticket t =new Ticket(
                                            jsonObject.getInt("id"),
                                            jsonObject.getString("ticket_no"),
                                            jsonObject.getInt("event_id"),
                                            jsonObject.getInt("tickets_nbr"),
                                            1,
                                            jsonObject.getString("status"),
                                            jsonObject.getInt("used")
                                            );
                                    if(t.getPayed().equals("PAYED")){
                                        Log.d("Inserting", "Ok");
                                        dbHandler.addNewTicket(t.getTicket_no(), t.getEvent_id(), t.getTicket_nbr(), 1, t.getPayed(), t.getUsed());
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error", error.toString());
                }
            });
            requestQueue.add(jsonObjectRequest);
        }
    }
}