package trungt.tcss450.uw.edu.phishapp1.login;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import trungt.tcss450.uw.edu.phishapp1.SuccessFragment;
import trungt.tcss450.uw.edu.phishapp1.model.Credentials;
import trungt.tcss450.uw.edu.phishapp1.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnLoginFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnLoginFragmentInteractionListener mListener;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
/*
 * In fragments, we inflate the layout from XML in the onCreateView method, not onCreate as we do in Activities.
 * Thus, once Android Studio implements the onClick method, we need to attach the listener to each of the
 * button.
 */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        view.findViewById(R.id.button_login_signin)
                .setOnClickListener(this::attemptLogin);

        Button b = (Button) view.findViewById(R.id.button_login_register);
        b.setOnClickListener(this); // add this Fragment Object as the OnClickListener

     //   view.findViewById(R.id.button_login_register)
          //      .setOnClickListener(b -> mListener.onRegisterClicked());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if(getArguments() != null
                && getArguments().containsKey(getString(R.string.key_credentials))) {
            final Credentials creds =
                    (Credentials) getArguments().getSerializable(
                            getString(R.string.key_credentials));
            ((EditText) getActivity().findViewById(R.id.et_login_email))
                    .setText(creds.getEmail());
            ((EditText) getActivity().findViewById(R.id.et_login_password))
                    .setText(creds.getPassword());
        }
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginFragmentInteractionListener) {
            mListener = (OnLoginFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param button The button that was clicked.
     */
    @Override
    public void onClick(View button) {
        if (mListener != null) {
            switch (button.getId()) {
                case R.id.button_login_register:
                    onRegisterClicked();
                    Log.d("Register Button clicked", "Register button is clicked!");
                    break;
                case R.id.button_login_signin:
                    attemptLogin(button);
                    break;
                default:
                    Log.wtf("", "Didn't expect to see me...");

            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnLoginFragmentInteractionListener {
        // TODO: Update argument type and name
        void onLoginFragmentInteraction(Credentials creds, String jwt);
    }


    /**
     * This method is that action that the user clicked the Register button
     * which will bring up the Register fragment page.
     * @author trung thai
     */
    public void onRegisterClicked() {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, new RegisterFragment())
                .addToBackStack(null);

        transaction.commit();

    }

    /**
     * This method will take the Credentials built and send it to the LoginFragment where the
     * credentials will be displayed.
     * @param credentials the credentials sent to the LoginFragment to display in order to login.
     */
    public void onRegisterSuccess(Credentials credentials) {

        getActivity().getSupportFragmentManager().popBackStack();

        final Bundle args = new Bundle();
        args.putSerializable("key for creds", credentials);
        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);

        FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment);

        transaction.commit();
    }

    private void attemptLogin(final View theButton) {

        EditText emailEdit = getActivity().findViewById(R.id.et_login_email);
        EditText passwordEdit = getActivity().findViewById(R.id.et_login_password);

        boolean hasError = false;
        if (emailEdit.getText().length() == 0) {
            hasError = true;
            emailEdit.setError("Field must not be empty.");
        } else if (emailEdit.getText().toString().chars().filter(ch -> ch == '@').count() != 1) {
            hasError = true;
            emailEdit.setError("Field must contain a valid email address");

            if (!hasError) {
                /**
                 * Send credentials to webservice then wait for the results.
                 */
                // remove when using AsyncTask and web service
                Credentials credentials = new Credentials.Builder(
                        emailEdit.getText().toString(),
                        passwordEdit.getText().toString())
                        .build();
                mListener.onLoginFragmentInteraction(credentials, emailEdit.getText().toString());
               // mListener.onLoginSuccess(credentials, emailEdit.getText().toString());
            }

        }

    }

    /**
     * This method signifies that the login was successful and takes you to a page that shows the
     * email address used to login.
     *
     * @author trung thai
     */
    public void onLoginSuccess(Credentials creds, final String jwt) {

        getActivity().getSupportFragmentManager().popBackStack();

        final Bundle args = new Bundle();
        args.putString("key for jwt", jwt);
        args.putSerializable("key for creds", creds);
        SuccessFragment fragment = new SuccessFragment();
        fragment.setArguments(args);

        FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment);

        transaction.commit();
    }
}
