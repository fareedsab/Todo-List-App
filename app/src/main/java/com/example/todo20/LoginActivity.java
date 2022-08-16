package com.example.todo20;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.todo20.Models.AppDatabase;
import com.example.todo20.Models.Task_Interface;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;
    public static String accid;
    SharedPreferences sharedPreferences ;
    List<com.example.todo20.Models.Task> tasks ;

    boolean temp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        temp= true;
        mAuth=FirebaseAuth.getInstance();
        rootNode = FirebaseDatabase.getInstance();
        sharedPreferences = getSharedPreferences("Accountdetails",MODE_PRIVATE);
        createRequest();
    }
    private void createRequest() {


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();


        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    public void btnsignin(View view) {
        signIn();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.e("TAG", "onActivityResult: "+e );
                // Google Sign In failed, update UI appropriately
                // ...
                Toast.makeText(this, e.getMessage() + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onStart() {
        super.onStart();


        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null){
            Log.d("TAG", "onStart: "+user.getDisplayName());
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }


    }
    DatabaseReference reference;
    FirebaseDatabase rootNode;
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {


        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            SharedPreferences.Editor myEdit = sharedPreferences.edit();
                            myEdit.putString("accountid", acct.getId());
                            myEdit.commit();
                            reference = rootNode.getReference(acct.getId());
                            accid=acct.getId();
                            reference.child("UserDetails").child("username").setValue(acct.getDisplayName());
                            reference.child("UserDetails").child("email").setValue(acct.getEmail());
                            reference.child("UserDetails").child("email").setValue(acct.getEmail());
                            synconlinedata();

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);


                        } else {
                            Toast.makeText(LoginActivity.this, "Sorry auth failed.", Toast.LENGTH_SHORT).show();



                        }


                        // ...
                    }
                });
    }
    public void synconlinedata()
    {
        AppDatabase db= AppDatabase.getInstance(LoginActivity.this);
        Task_Interface task_interface = db.task_interface();
        tasks = task_interface.getalltasks();
        if(tasks.size()==0) {
            DatabaseReference ref = reference.child("TodoDetails");
            ref.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    if(snapshot.exists())
                    {
                        tasks= task_interface.getalltasks();
                    com.example.todo20.Models.Task t = snapshot.getValue(com.example.todo20.Models.Task.class);
                    if(!task_interface.is_exist(t.getId())){
                    task_interface.add(t.isDelete_status(),t.getId(),t.getTask_date(),t.getTask_description(),t.getTask_priority(),t.getTask_status(),t.getTask_sync(),t.getTask_time(),t.getTask_title(),t.getTaskimage(),false);
                    Log.d("TAG", "onChildAdded:1  " + t.toString());}


                }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }// dialog.dismiss();
    }
}