package com.bluej.chitchat.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bluej.chitchat.R;
import com.bluej.chitchat.adapters.RecentConversationsAdapter;
import com.bluej.chitchat.adapters.UserAdapter;
import com.bluej.chitchat.databinding.ActivityUserBinding;
import com.bluej.chitchat.listeners.UserListener;
import com.bluej.chitchat.models.ChatMessage;
import com.bluej.chitchat.models.User;
import com.bluej.chitchat.utilities.Constants;
import com.bluej.chitchat.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class UserActivity extends BaseActivity implements UserListener {
private ActivityUserBinding binding;
private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager=new PreferenceManager(getApplicationContext());
        getUsers();
        setListeners();
    }
    private void setListeners(){
        binding.imageBack.setOnClickListener(view -> onBackPressed());
    }
    private void getUsers(){
        loading(true);
        FirebaseFirestore fdb=FirebaseFirestore.getInstance();
        fdb.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task ->{
                        loading(false);
        String currentUserId=preferenceManager.getString(Constants.KEY_USER_ID);
        if(task.isSuccessful()&&task.getResult()!=null){
            List<User> users=new ArrayList<>();
            for(QueryDocumentSnapshot queryDocumentSnapshot:task.getResult()){
               if(currentUserId.equals(queryDocumentSnapshot.getId())){
                  continue;
    }
    User user=new User();
    user.name=queryDocumentSnapshot.getString(Constants.KEY_NAME);
    user.email=queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
    user.image=queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
    user.token=queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
    user.id=queryDocumentSnapshot.getId();
    users.add(user);
            }
            if(users.size()>0){
                UserAdapter userAdapter=new UserAdapter(users,this::onUserClicked);
                binding.userRecyclerView.setAdapter(userAdapter);
                binding.userRecyclerView.setVisibility(View.VISIBLE);
            }else{
                showErrorMessage();
            }
        }else{
            showErrorMessage();
        }
    });
    }
    private void showErrorMessage(){
        binding.textErrorMessage.setText(String.format("%s","No user Available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }
    private void loading(Boolean isLoading){
        if(isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    public void onUserClicked(User user){
        Intent intent=new Intent(getApplicationContext(),ChatActivity.class);
        intent.putExtra(Constants.KEY_USER,user);
        startActivity(intent);
        finish();
    }
}