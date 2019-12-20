package com.chaudhary.zelio;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.SignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;



import java.io.InputStream;

public class AllreadyHaveAccount extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener
{

    EditText et_phoneNumber;
    private static final String TAG = "GoogleSignInActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInOptions gso;
    private GoogleApiClient mGoogleApiClient;
    public static final String PROFILE_USER_ID = "USER_ID";
    public static final String PROFILE_DISPLAY_NAME = "PROFILE_DISPLAY_NAME";
    public static final String PROFILE_USER_EMAIL = "USER_PROFILE_EMAIL";
    public static final String PROFILE_IMAGE_URL = "PROFILE_IMAGE_URL";

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    LoginButton loginButton;
    ImageView circularImageView;

    AlertDialog.Builder alert;
    ProgressBar progressBar;
    RelativeLayout main;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_allready_have_account);
        main=(RelativeLayout)findViewById(R.id.main_rl);

        callbackManager = CallbackManager.Factory.create();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                System.out.println("-----onCurrentProfileChanged---");
            }
        };
        accessTokenTracker.startTracking();
        profileTracker.startTracking();

        loginButton = (LoginButton)findViewById(R.id.login_button);
        FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                System.out.println("-----onSuccess---");
                Toast.makeText(AllreadyHaveAccount.this, "Result", Toast.LENGTH_SHORT).show();
                Profile profile = Profile.getCurrentProfile();
                if(profile!=null)
                {
                    buildDialog(R.style.DialogAnimation_2,profile.getFirstName(),profile.getProfilePictureUri(200,200).toString());
                }
            }

            @Override
            public void onCancel()
            {
            }

            @Override
            public void onError(FacebookException e) {
            }
        };
        loginButton.setReadPermissions("user_friends");
        loginButton.registerCallback(callbackManager, callback);



        et_phoneNumber=(EditText)findViewById(R.id.et_phoneNumber);
        gso = ((MyApplication) getApplication()).getGoogleSignInOptions();
        mGoogleApiClient = ((MyApplication) getApplication()).getGoogleApiClient(AllreadyHaveAccount.this, this);
        SignInButton mSignInButton = (SignInButton)findViewById(R.id.bt_SignIn);
        assert mSignInButton != null;
        mSignInButton.setSize(SignInButton.SIZE_STANDARD);
        mSignInButton.setScopes(gso.getScopeArray());
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("--1--");
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                System.out.println("--2--");
                startActivityForResult(signInIntent, RC_SIGN_IN);
                System.out.println("--3--");
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("--4--");
        callbackManager.onActivityResult(requestCode, resultCode, data);
        System.out.println("--5--");
        System.out.println("--re--"+requestCode);
        if (requestCode == RC_SIGN_IN) {
            System.out.println("--6--");
            progressAlertDialog();
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            System.out.println("--7--"+result.isSuccess());
            handleSignInResult(result);
            System.out.println("--8--");
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        System.out.println("--9--");
        if (result.isSuccess()) {
            System.out.println("--10--");
            GoogleSignInAccount userAccount = result.getSignInAccount();
            String userId = userAccount.getId();
            String displayedUsername = userAccount.getDisplayName();
            String userEmail = userAccount.getEmail();
            //String userProfilePhoto = userAccount.getPhotoUrl().toString();
            Intent googleSignInIntent = new Intent(AllreadyHaveAccount.this, SignActivity.class);
            googleSignInIntent.putExtra(PROFILE_USER_ID, userId);
            googleSignInIntent.putExtra(PROFILE_DISPLAY_NAME, displayedUsername);
            googleSignInIntent.putExtra(PROFILE_USER_EMAIL, userEmail);
            System.out.println("--11--");
            //googleSignInIntent.putExtra(PROFILE_IMAGE_URL, userProfilePhoto);
            startActivity(googleSignInIntent);
            System.out.println("--12--");
        }
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }
    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private void buildDialog(int animationSource,String profile,String url) {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.face_book_custom_dialog, null);
        TextView tv_name=(TextView)alertLayout.findViewById(R.id.tv_firstname);
        LinearLayout rl_countinou=(LinearLayout)alertLayout.findViewById(R.id.rl_continou);
        tv_name.setText(profile);
        new AllreadyHaveAccount.DownloadImage((ImageView)alertLayout.findViewById(R.id.iv_profile)).execute(url);
        AlertDialog.Builder alert = new AlertDialog.Builder(AllreadyHaveAccount.this);
        alert.setView(alertLayout);
        alert.setCancelable(false);
        AlertDialog dialog = alert.create();
        dialog.getWindow().getAttributes().windowAnimations = animationSource;

        rl_countinou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AllreadyHaveAccount.this,SignActivity.class));
                finish();
            }
        });

        dialog.show();
    }


    public class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage; 
        public DownloadImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }


    public void progressAlertDialog()
    {
        LayoutInflater inflater = getLayoutInflater();
        View alertProcess = inflater.inflate(R.layout.progress_dialog, null);
        progressBar=(ProgressBar)alertProcess.findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(0xFFFF0000, android.graphics.PorterDuff.Mode.MULTIPLY);
        progressBar.setVisibility(View.VISIBLE);
        alert = new AlertDialog.Builder(AllreadyHaveAccount.this);
        alert.setView(alertProcess);
        alert.setCancelable(false);
        AlertDialog dialog = alert.create();
        dialog.show();
    }
}
