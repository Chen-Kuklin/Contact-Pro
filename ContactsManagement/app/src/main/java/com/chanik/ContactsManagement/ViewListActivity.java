package com.chanik.ContactsManagement;

import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



public class ViewListActivity extends Activity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_list_acivity);
        //Making contacts a neat list
        RecyclerView recyclerView = findViewById(R.id.contacts_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ManagerContacts manager = ManagerContacts.getInstance(this);
        ContactAdapter adapter = new ContactAdapter(manager.getContacts());
        recyclerView.setAdapter(adapter);
        adapter.setListener(new ContactAdapter.ContactsListener() {
            @Override
            public void onContactClikced(int position, View view) {
                Intent intent = new Intent(ViewListActivity.this, ViewDetailsActivity.class);
                //Send the location of the clicked item in the list
                intent.putExtra("contact",position);
                startActivity(intent);
            }
        });
    }
}

