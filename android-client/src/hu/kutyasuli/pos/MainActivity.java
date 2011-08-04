package hu.kutyasuli.pos;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends Activity implements OnItemSelectedListener {
//    private static final String URL_BASE = "http://192.168.1.71/rhornig/ks/paymentsys";
    private static final String URL_BASE = "http://kutyasuli.hu/ps";
    private static final int TX_QUERY = 0;
    private static final int TX_TOPUP = 1;
    private static final int TX_TRANSFER = 2;
    private static final int TX_REGISTRATION_KLUB = 3;
    private static final int TX_REGISTRATION_OV = 4;
    private static final int TX_REGISTRATION_AGILITY = 5;
    private static final int TX_REGISTRATION_NYOM = 6;
    private static final String ACC_ID_HAZIPENZTAR = "2";
    private static final String ACC_ID_EGYESULET = "1";
    private DefaultHttpClient httpClient;
    private EditText from_accountCtrl;
    private EditText to_accountCtrl;
    private EditText amountCtrl;
    private EditText commentCtrl;
    private ToggleButton optSpeakCtrl;
    private TextToSpeech tts;
    private Spinner spinner;
    private int tx_type = 0;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        from_accountCtrl=(EditText)findViewById(R.id.from);
        to_accountCtrl=(EditText)findViewById(R.id.to);
        amountCtrl=(EditText)findViewById(R.id.amount);
        commentCtrl=(EditText)findViewById(R.id.comment);
        findViewById(R.id.send).setOnClickListener(onSend);
        findViewById(R.id.scan).setOnClickListener(onScan);
        optSpeakCtrl = (ToggleButton)findViewById(R.id.optSpeak);
        spinner = ((Spinner)findViewById(R.id.txtype));
        spinner.setSelection(tx_type);
        spinner.setOnItemSelectedListener(this);
        
        httpClient=new DefaultHttpClient();
        
        tts = new TextToSpeech(this, new OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS)
                    tts.setLanguage(new Locale("hu", "HU"));
                else
                    tts.shutdown();
            }
        });
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        tts.shutdown();
        httpClient.getConnectionManager().shutdown(); 
    }
    
    private String[] pay(String from, String to, String amount, String comment) {
        try {
            HttpPost post=new HttpPost(URL_BASE+"/pay.php");
            
            // post.addHeader("Authorization", "Basic "+getCredentials());
            
            List<NameValuePair> form=new ArrayList<NameValuePair>();
            form.add(new BasicNameValuePair("to", to));
            form.add(new BasicNameValuePair("from", from));
            form.add(new BasicNameValuePair("amount", amount));
            form.add(new BasicNameValuePair("comment", comment));
            post.setEntity(new UrlEncodedFormEntity(form, HTTP.ISO_8859_1));
            ResponseHandler<String> responseHandler=new BasicResponseHandler();
            String result = httpClient.execute(post, responseHandler);
            return result.split("#");
            // JSONObject response=new JSONObject(responseBody);
        }
        catch (Throwable t) {
            Log.e("KutyaSuli POS", "Exception in pay()", t);
            errorDialog(t);
        }
        return null;
    }
    
    String[] query(String id) {
        try {
            HttpGet get=new HttpGet(URL_BASE+"/account.php?id="+id);
            ResponseHandler<String> responseHandler=new BasicResponseHandler();
            return httpClient.execute(get, responseHandler).split("#");
        }
        catch (Throwable t) {
            Log.e("KutyaSuli POS", "Exception in query()", t);
            errorDialog(t);
        }
        return null;
    }

    private void userFeedback(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        if (optSpeakCtrl.isChecked())
            tts.speak(message.replace(" -", " mínusz "), TextToSpeech.QUEUE_ADD, null);
    }

    private void executeTransaction(String id) {
        String result[];
        String message = "Hiba!";
        switch (tx_type) {
        case TX_QUERY:
            result = query(id);
            if (result != null)
                message = result[1] + " egyenlege: " + result[2] + " pont."; 
            break;
        case TX_TOPUP:
            result = pay(ACC_ID_HAZIPENZTAR, id, "10", "Feltöltés");
            if (result != null)
                message = "Kész. "+result[2] + " új egyenlege: " + result[3] + " pont."; 
            break;
        case TX_TRANSFER:
            result = pay(id, to_accountCtrl.getText().toString(), amountCtrl.getText().toString(), commentCtrl.getText().toString());
            if (result != null)
                message = "Kész."; 
            break;
        case TX_REGISTRATION_KLUB:
            result = pay(id, ACC_ID_EGYESULET, "0", "Klub - regisztráció");
            if (result != null)
                message = result[0] + ": " + result[1] + " pont."; 
            break;
        case TX_REGISTRATION_OV:
            result = pay(id, ACC_ID_EGYESULET, "1", "ÖV - regisztráció");
            if (result != null)
                message = result[0] + ": " + result[1] + " pont."; 
            break;
        case TX_REGISTRATION_NYOM:
            result = pay(id, ACC_ID_EGYESULET, "0", "Nyom - regisztráció");
            if (result != null)
                message = result[0] + ": " + result[1] + " pont."; 
            break;
        case TX_REGISTRATION_AGILITY:
            result = pay(id, ACC_ID_EGYESULET, "3", "Agility - regisztráció");
            if (result != null)
                message = result[0] + ": " + result[1] + " pont."; 
            break;

        default:
            break;
        }
        
        userFeedback(message);
    }
    
    private void executeCommand(String id) {
        if (id.equals("cmd-lekerdez")) {
            tx_type = TX_QUERY;
            userFeedback("Lekérdezés üzemmód.");
        } else if (id.equals("cmd-feltoltes")) {
            tx_type = TX_TOPUP;
            userFeedback("Feltöltés üzemmód.");
        } else if (id.equals("cmd-fiz-klub")) {
            tx_type = TX_REGISTRATION_KLUB;
            userFeedback("Klub regisztráció.");
        } else if (id.equals("cmd-fiz-agility")) {
            tx_type = TX_REGISTRATION_AGILITY;
            userFeedback("Agility regisztráció.");
        } else if (id.equals("cmd-fiz-ov")) {
            tx_type = TX_REGISTRATION_OV;
            userFeedback("Őrző védő regisztráció.");
        }
        
        spinner.setSelection(tx_type);
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            String id = scanResult.getContents();
            if (id != null && !"".equals(id)) {
                if (id.startsWith("cmd-")) 
                    executeCommand(id);
                else
                    executeTransaction(id);
                
                // restart the scanner again
                IntentIntegrator.initiateScan(MainActivity.this,"Barcode Scanner Download","A Barcode Scanner alkalmazás szökséges a működéshez. Letöltsem?","Igen","Nem",IntentIntegrator.QR_CODE_TYPES);
            }
        }
    }

    private View.OnClickListener onScan=new View.OnClickListener() {
        public void onClick(View v) {
            IntentIntegrator.initiateScan(MainActivity.this,"Barcode Scanner Download","A Barcode Scanner alkalmazás szökséges a működéshez. Letöltsem?","Igen","Nem",IntentIntegrator.QR_CODE_TYPES);
        }
    };

    private View.OnClickListener onSend=new View.OnClickListener() {
        public void onClick(View v) {
            executeTransaction(from_accountCtrl.getText().toString());
        }
    };

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
        tx_type = pos;
        to_accountCtrl.setText("");
        amountCtrl.setText("");
        commentCtrl.setText("");
        to_accountCtrl.setEnabled(tx_type == TX_TRANSFER);
        amountCtrl.setEnabled(tx_type == TX_TRANSFER);
        commentCtrl.setEnabled(tx_type == TX_TRANSFER);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }
    
    private void errorDialog(Throwable t) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        
        builder
            .setTitle("Exception!")
            .setMessage(t.toString())
            .setPositiveButton("OK", null)
            .show();
    }

}