package com.ihuhao.app.httpdns;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;


public class MainActivity extends ActionBarActivity {

    private ProgressDialog m_progressDialog;
    private Bitmap m_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn = (Button)findViewById(R.id.btnTest);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(networkTask).start();
                m_progressDialog = ProgressDialog.show(MainActivity.this, "Loading...", "Please wait...", true, false);
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            m_progressDialog.dismiss();

            super.handleMessage(msg);
            Bundle data = msg.getData();

            TextView txtIp = (TextView)findViewById(R.id.txtIp);
            txtIp.setText(data.getString("ip"));

            TextView txtJSON = (TextView)findViewById(R.id.txtJSON);
            txtJSON.setText(data.getString("json"));

            ImageView imageView =(ImageView)findViewById(R.id.imageView);
            imageView.setImageBitmap(m_img);

        }
    };

    Runnable networkTask = new Runnable() {
        @Override
        public void run() {
            Message msg = new Message();
            Bundle data = new Bundle();

            String ip = HttpDNS.getAddressByName("www.dnspod.cn");
            Log.i("httpdns", String.format("getAddressByName: %s", ip));

            String json = HttpDNS.getStrWithHttpDNS("http://image.baidu.com/user/msg");
            Log.i("httpdns", String.format("getStrWithHttpDNS: %s", json));

            Bitmap img = HttpDNS.getBitmapWithHttpDNS("http://img0.bdstatic.com/static/common/widget/search_box_search/logo/logo_3b6de4c.png");
            Log.i("httpdns", String.format("getBitmapWithHttpDNS: %d", img.getByteCount()));

            data.putString("ip", ip);
            data.putString("json", json);
            m_img = img;

            msg.setData(data);
            handler.sendMessage(msg);
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
