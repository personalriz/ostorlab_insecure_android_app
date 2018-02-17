package co.ostorlab.insecure_app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import co.ostorlab.insecure_app.bugs.calls.AESCipher;
import co.ostorlab.insecure_app.bugs.calls.ClearTextTraffic;
import co.ostorlab.insecure_app.bugs.calls.InsecureCommands;
import co.ostorlab.insecure_app.bugs.calls.InsecureFilePermissions;
import co.ostorlab.insecure_app.bugs.calls.DexClassLoaderCall;
import co.ostorlab.insecure_app.bugs.calls.ECBModeCipher;
import co.ostorlab.insecure_app.bugs.calls.InsecureSharedPreferences;
import co.ostorlab.insecure_app.bugs.calls.PathClassLoaderCall;
import co.ostorlab.insecure_app.bugs.calls.StaticIV;
import co.ostorlab.insecure_app.bugs.calls.TLSTraffic;
import co.ostorlab.insecure_app.bugs.calls.WebviewInsecureSettings;

public class MainActivity extends AppCompatActivity {
    private TextView outputView;
    private Button runAllButton ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.loadLibrary("native-lib");
        outputView = findViewById(R.id.runOutputId);
        runAllButton = findViewById(R.id.runAllId);


        final Button runAllButton = findViewById(R.id.runAllId);
        runAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                outputView.setText("Running \n");
                executeAllRules();
            }
        });
    }

    private void executeAllRules() {
        BugRuleCaller caller = new BugRuleCaller(getApplicationContext());
        outputView.append("Adding rules ...\n");
        caller.addRule(new ECBModeCipher());
        caller.addRule(new ClearTextTraffic());
        caller.addRule(new TLSTraffic());
        caller.addRule(new AESCipher());
        caller.addRule(new StaticIV());
        caller.addRule(new PathClassLoaderCall());
        caller.addRule(new DexClassLoaderCall());
        caller.addRule(new InsecureFilePermissions());
        caller.addRule(new InsecureSharedPreferences());
        caller.addRule(new InsecureCommands());
        caller.addRule(new WebviewInsecureSettings());
        // caller.addRule(new MemoryCorruption());

        try {
            caller.callRules();
            outputView.append(caller.listBugRules());

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
