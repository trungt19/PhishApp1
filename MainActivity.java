package trungt.tcss450.uw.edu.phishapp1;

import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import trungt.tcss450.uw.edu.phishapp1.login.LoginFragment;
import trungt.tcss450.uw.edu.phishapp1.login.RegisterFragment;
import trungt.tcss450.uw.edu.phishapp1.model.Credentials;

public class MainActivity extends AppCompatActivity implements LoginFragment.OnLoginFragmentInteractionListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            if (findViewById(R.id.fragmentContainer) != null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragmentContainer, new LoginFragment())
                        .commit();
            }
        }
    }

    @Override
    public void onLoginFragmentInteraction(Credentials creds, String jwt) {

        LoginFragment loginFragment;

        loginFragment = (LoginFragment) getSupportFragmentManager()
                .findFragmentById(R.id.button_login_signin);

        EditText emailEdit = findViewById(R.id.et_login_email);
        EditText passwordEdit = findViewById(R.id.et_login_password);

        boolean hasError = false;
        if (emailEdit.getText().length() == 0) {
            hasError = true;
            emailEdit.setError("Field must not be empty.");
        } else if (emailEdit.getText().toString().chars().filter(ch -> ch == '@').count() != 1) {
            hasError = true;
            emailEdit.setError("Field must contain a valid email address");
        }
        if (passwordEdit.getText().length() == 0) {
            hasError = true;
            passwordEdit.setError("Field must not be empty");
        }
        if (!hasError) {
            /*
             *  Send credentials to webservice then wait for the results.
             */
            // remove when using AsyncTask and web service
            Credentials credentials = new Credentials.Builder(
                    emailEdit.getText().toString(),
                    passwordEdit.getText().toString())
                    .build();
            onLoginFragmentInteraction(credentials, emailEdit.getText().toString());
        }

        if(loginFragment != null) {
            final Bundle args = new Bundle();
            args.putString("key for jwt", jwt);
            args.putSerializable("key for creds", creds);
            SuccessFragment fragment = new SuccessFragment();
            fragment.setArguments(args);

            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment);

            transaction.commit();
        }
    }

}

