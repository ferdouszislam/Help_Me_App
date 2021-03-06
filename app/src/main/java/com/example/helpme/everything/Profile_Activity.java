package com.example.helpme.everything;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.helpme.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;


public class Profile_Activity extends AppCompatActivity {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference reference;
    TextView emailTV,usernameTV,fullnameTV,addressTV,phoneTV;
    ImageButton editProfileBtn;
    Toolbar toolbar;
    ProgressBar progressBar;
    CircleImageView circleImageView;
    LinearLayout linearLayout;
    String photopath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_);

        reference = FirebaseDatabase.getInstance().getReference("Profiles");
        progressBar = findViewById(R.id.progressBar_profileView);
        linearLayout = findViewById(R.id.profileView_linear_layout);
        editProfileBtn = findViewById(R.id.profileEditButton);
        usernameTV = findViewById(R.id.profileUsername);
        emailTV = findViewById(R.id.profileEmail);
        fullnameTV = findViewById(R.id.profileFullName);
        addressTV = findViewById(R.id.profileAddress);
        phoneTV = findViewById(R.id.profilePhone);
        toolbar = findViewById(R.id.toolbar_profile);
        circleImageView = findViewById(R.id.profile_image);


        linearLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        Query query = FirebaseDatabase.getInstance().getReference("Profiles")
                .orderByChild("userId")
                .equalTo(user.getUid());
        query.addListenerForSingleValueEvent(valueEventListener);



        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(),Edit_Profile_Activity.class));
            }
        });

    }
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists())
            {
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {

                    UserInfo userInfo = snapshot.getValue(UserInfo.class);

                    String fullName = fullnameTV.getText()+userInfo.getFirstName()+" "+userInfo.getLastName();
                    fullnameTV.setText(fullName);

                    String email = emailTV.getText()+userInfo.getEmail();
                    emailTV.setText(email);

                    String userName = usernameTV.getText()+userInfo.getUserName();
                    usernameTV.setText(userName);

                    String phone = phoneTV.getText()+userInfo.getPhone();
                    phoneTV.setText(phone);

                    String address = addressTV.getText()+userInfo.getAddress();
                    addressTV.setText(address);

                    if(userInfo.getPhoto_link().compareTo("link:")!=0)
                    {
                        photopath = userInfo.getPhoto_link();
                        Picasso.with(getApplicationContext())
                                .load(userInfo.getPhoto_link())
                                .into(circleImageView, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        progressBar.setVisibility(View.GONE);
                                        linearLayout.setVisibility(View.VISIBLE);
                                    }
                                    @Override
                                    public void onError() { }
                                });

                        circleImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getApplicationContext(),FullScreenImage.class);
                                intent.putExtra("link",photopath);
                                startActivity(intent);
                            }
                        });
                    }
                    else
                    {
                        progressBar.setVisibility(View.GONE);
                        linearLayout.setVisibility(View.VISIBLE);
                    }
                }
            }
            else
            {

                String fullName = "Please set full name!";
                fullnameTV.setText(fullName);
                fullnameTV.setTextColor(Color.RED);

                String email = emailTV.getText()+user.getEmail();
                emailTV.setText(email);

                String userName = usernameTV.getText()+trimmer(user.getEmail());
                usernameTV.setText(userName);

                String phone = "Please set phone number!";
                phoneTV.setText(phone);
                phoneTV.setTextColor(Color.RED);

                String address = "Please set address!";
                addressTV.setText(address);
                addressTV.setTextColor(Color.RED);

                progressBar.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            linearLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(),"Failed to load profile",Toast.LENGTH_SHORT).show();
        }
    };

    String trimmer(String str)
    {
        String temp="";
        for(int i =0;i<str.length();i++)
        {
            if(str.charAt(i)!='@')
            {
                temp = temp+str.charAt(i);
            }
            else
            {
                break;
            }
        }
        return temp.toUpperCase();
    }
}
