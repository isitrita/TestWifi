package edu.home.testwifi;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Button;
import android.widget.TextView;

import com.koushikdutta.async.*;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.http.AsyncHttpClient;

import org.xml.sax.InputSource;

import java.io.StringReader;

import java.util.Timer;
import java.util.TimerTask;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;


public class MainActivity extends Activity{

    AsyncServer server = new AsyncServer();
    AsyncHttpClient client = new AsyncHttpClient(server);

    int[] lids = {R.id.led0,R.id.led1,R.id.led2,R.id.led3,R.id.led4};
    int[] leds = {0,0,0,0,0};


    private void updateStatus(){
        ((TextView) findViewById(R.id.textView)).setText("timer run");

        client.getString("http://shrouded-mesa-5173.herokuapp.com/status.xml")
                .setCallback(new FutureCallback<String>() {
                    @TargetApi(Build.VERSION_CODES.FROYO)
                    @Override
                    public void onCompleted(Exception e, String result) {


                        XPathFactory xpathFactory = XPathFactory.newInstance();
                        XPath xpath = xpathFactory.newXPath();



                        try {
                            for(int i=0;i<leds.length;i++){
                                InputSource source = new InputSource(new StringReader(result)); //TODO: optimize
                                leds[i] = Integer.parseInt(xpath.evaluate("/response/led"+i, source, XPathConstants.STRING).toString());

                                if (leds[i]==1) {
                                    switchLedOn(findViewById(lids[i]), i);
                                } else {
                                    switchLedOff(findViewById(lids[i]), i);
                                }
                            }
                        } catch (XPathExpressionException ex) {
                            //((TextView) findViewById(R.id.textView)).setText("status error");

                            ex.printStackTrace();
                        }

                    }
                });
    };



    private void switchLed(View v, int id) {
        if (leds[id]==0) {
            leds[id]=1;
            v.setBackgroundColor(0xFFFFFF33);
        } else {
            leds[id]=0;
            v.setBackgroundColor(0xFFAAAAAA);
        }
    }
    private void switchLedOn(View v, int id) {
        leds[id]=1;
        v.setBackgroundColor(0xFFFFFF33);
    }
    private void switchLedOff(View v, int id) {
        leds[id]=0;
        v.setBackgroundColor(0xFFAAAAAA);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        updateStatus();


        Timer myTimer = new Timer();
        final Handler uiHandler = new Handler();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateStatus();
                    }
                });
            };
        }, 500, 2L * 1000);

        OnClickListener onLed = new OnClickListener() {

            @Override
            public void onClick(View v) {

                ((TextView) findViewById(R.id.textView)).setText("hello");

                int id = 0;

                switch(v.getId())  {
                    case  R.id.led0:
                        id = 0;
                        break;
                    case  R.id.led1:
                        id = 1;
                        break;
                    case  R.id.led2:
                        id = 2;
                        break;
                    case  R.id.led3:
                        id = 3;
                        break;
                    case  R.id.led4:
                        id = 4;
                        break;
                }

                switchLed(v,id);

                client.getString("http://shrouded-mesa-5173.herokuapp.com/leds.cgi?led="+String.valueOf(id))
                        .setCallback(new FutureCallback<String>() {
                            @Override
                            public void onCompleted(Exception e, String result) {

                                ((TextView) findViewById(R.id.textView)).setText(result);
                            }
                        });



                /*
                class bgStuff extends AsyncTask<Void, Void, Void>{

                    String translatedText = "";
                    @Override
                    protected Void doInBackground(Void... params) {
                        // TODO Auto-generated method stub
                        try {
                            String text = ((EditText) findViewById(R.id.editText)).getText().toString();
                            translatedText = translate(text);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            translatedText = e.toString();
                        }

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        // TODO Auto-generated method stub
                        ((TextView) findViewById(R.id.textView)).setText(translatedText);
                        super.onPostExecute(result);
                    }

                }

                new bgStuff().execute();
                */
            }
        };

        ((Button) findViewById(R.id.led0)).setOnClickListener(onLed);
        ((Button) findViewById(R.id.led1)).setOnClickListener(onLed);
        ((Button) findViewById(R.id.led2)).setOnClickListener(onLed);
        ((Button) findViewById(R.id.led3)).setOnClickListener(onLed);
        ((Button) findViewById(R.id.led4)).setOnClickListener(onLed);
    }
}

