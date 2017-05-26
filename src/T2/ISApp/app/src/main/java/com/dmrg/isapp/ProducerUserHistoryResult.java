package com.dmrg.isapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ProducerUserHistoryResult extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    String ipAddress;
    String port = "3000";
    Toast Pass;
    String username, name, device_types, device_models, date, email, password, requestReply;
    DrawerLayout mDrawerLayout;
    Toolbar mToolbar;
    ActionBarDrawerToggle mToggle;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producer_user_history_result);

        mToolbar = (Toolbar) findViewById(R.id.producer_checkUserH_res_nav_action);
        setSupportActionBar(mToolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.check_user_h_res_drawerProducer);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            name = extras.getString("name");
            username = extras.getString("username");
            password = extras.getString("password");
            email = extras.getString("email");
            date = extras.getString("date");
            device_types = extras.getString("device_types");
            device_models = extras.getString("device_models");
            ipAddress = extras.getString("ip");
            requestReply = extras.getString("requestReply");
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.producer_check_user_h_res_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.inflateHeaderView(R.layout.user_navigation_header);
        tv = (TextView) header.findViewById(R.id.UserName);
        tv.setText(username);


        ListView lv;
        lv = (ListView) findViewById(R.id.producer_UserHistoryList);

        StringTokenizer st = new StringTokenizer(requestReply, ",");
        StringTokenizer st2;

        ArrayList<String> lst = new ArrayList<String>();
        String tokenSt1, tokenSt2;

        while (st.hasMoreTokens()) {
            tokenSt1 = st.nextToken();
            st2 = new StringTokenizer(tokenSt1, "\"[");

            while (st2.hasMoreTokens()) {
                tokenSt2 = st2.nextToken();

                if (!tokenSt2.equals("]")) {
                    lst.add("[" + tokenSt2);
                }
            }
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplication(), android.R.layout.simple_list_item_1, lst);

        lv.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.toString()) {
            case "Home":
                Intent mainIntent1 = new Intent(ProducerUserHistoryResult.this, ProducerMainActivity.class);

                mainIntent1.putExtra("name", name);
                mainIntent1.putExtra("username", username);
                mainIntent1.putExtra("password", password);
                mainIntent1.putExtra("email", email);
                mainIntent1.putExtra("date", date);
                mainIntent1.putExtra("device_types", device_types);
                mainIntent1.putExtra("device_models", device_models);
                mainIntent1.putExtra("ip", ipAddress);

                ProducerUserHistoryResult.this.startActivity(mainIntent1);
                ProducerUserHistoryResult.this.finish();
                break;

            case "Register Device Type":
                Intent mainIntent2 = new Intent(ProducerUserHistoryResult.this, ProducerRegisterDeviceType.class);

                mainIntent2.putExtra("name", name);
                mainIntent2.putExtra("username", username);
                mainIntent2.putExtra("password", password);
                mainIntent2.putExtra("email", email);
                mainIntent2.putExtra("date", date);
                mainIntent2.putExtra("device_types", device_types);
                mainIntent2.putExtra("device_models", device_models);
                mainIntent2.putExtra("ip", ipAddress);

                ProducerUserHistoryResult.this.startActivity(mainIntent2);
                ProducerUserHistoryResult.this.finish();
                break;

            case "Remove Device Type":
                Intent mainIntent3 = new Intent(ProducerUserHistoryResult.this, ProducerUnregisterDeviceType.class);

                mainIntent3.putExtra("name", name);
                mainIntent3.putExtra("username", username);
                mainIntent3.putExtra("password", password);
                mainIntent3.putExtra("email", email);
                mainIntent3.putExtra("date", date);
                mainIntent3.putExtra("device_types", device_types);
                mainIntent3.putExtra("device_models", device_models);
                mainIntent3.putExtra("ip", ipAddress);

                ProducerUserHistoryResult.this.startActivity(mainIntent3);
                ProducerUserHistoryResult.this.finish();
                break;

            case "Register Device Model":
                Intent mainIntent4 = new Intent(ProducerUserHistoryResult.this, ProducerRegisterDeviceModel.class);

                mainIntent4.putExtra("name", name);
                mainIntent4.putExtra("username", username);
                mainIntent4.putExtra("password", password);
                mainIntent4.putExtra("email", email);
                mainIntent4.putExtra("date", date);
                mainIntent4.putExtra("device_types", device_types);
                mainIntent4.putExtra("device_models", device_models);
                mainIntent4.putExtra("ip", ipAddress);

                ProducerUserHistoryResult.this.startActivity(mainIntent4);
                ProducerUserHistoryResult.this.finish();
                break;

            case "Remove Device Model":
                Intent mainIntent5 = new Intent(ProducerUserHistoryResult.this, ProducerUnregisterDeviceModel.class);

                mainIntent5.putExtra("name", name);
                mainIntent5.putExtra("username", username);
                mainIntent5.putExtra("password", password);
                mainIntent5.putExtra("email", email);
                mainIntent5.putExtra("date", date);
                mainIntent5.putExtra("device_types", device_types);
                mainIntent5.putExtra("device_models", device_models);
                mainIntent5.putExtra("ip", ipAddress);

                ProducerUserHistoryResult.this.startActivity(mainIntent5);
                ProducerUserHistoryResult.this.finish();
                break;

            case "Check Device History":
                Pass = Toast.makeText(getApplicationContext(), "In future patches", Toast.LENGTH_SHORT);
                Pass.show();
                break;

            case "Check Device Type History":
                Pass = Toast.makeText(getApplicationContext(), "In future patches", Toast.LENGTH_SHORT);
                Pass.show();
                break;

            case "Logoff":
                Intent mainIntent8 = new Intent(ProducerUserHistoryResult.this, ProducerLogoutActivity.class);

                mainIntent8.putExtra("ip", ipAddress);
                mainIntent8.putExtra("username", username);

                ProducerUserHistoryResult.this.startActivity(mainIntent8);
                ProducerUserHistoryResult.this.finish();
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Description: Send an HTTP Post request to a specified ip address and port.
     * Also send a parameter "parameterName" with the value of "parameterValue".
     *
     * @param _uri     URI to post
     * @param username the username to send
     * @return The ip address' reply text, or an ERROR message is it fails to receive one
     */
    public String sendRequest(String _uri, String username) {
        String serverResponse = "ERROR";

        try {
            HttpClient httpclient = new DefaultHttpClient(); // create an HTTP client
            // define the URL e.g. http://myIpaddress:myport/?pin=13 (to toggle pin 13 for example)
            URI uri = new URI(_uri);
            //System.out.println(uri);
            HttpPost postRequest = new HttpPost(); // create an HTTP GET object
            postRequest.setURI(uri); // set the URL of the GET request
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("platform", "Android"));

            nameValuePairs.add(new BasicNameValuePair("username", username));

            postRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(postRequest); // execute the request
            // get the ip address server's reply
            InputStream content = null;
            content = response.getEntity().getContent();
            BufferedReader in = new BufferedReader(new InputStreamReader(content));
            serverResponse = in.readLine();
            //System.err.println(serverResponse);
            // Close the connection
            content.close();
        } catch (ClientProtocolException e) {
            // HTTP error
            serverResponse = e.getMessage();
            e.printStackTrace();
        } catch (IOException e) {
            // IO error
            serverResponse = e.getMessage();
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // URL syntax error
            serverResponse = e.getMessage();
            e.printStackTrace();
        }
        // return the server's reply/response text
        return serverResponse;
    }
}
