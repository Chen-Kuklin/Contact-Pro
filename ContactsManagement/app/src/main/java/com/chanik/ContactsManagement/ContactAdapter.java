package com.chanik.ContactsManagement;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;



public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder>{

    private List<Contact> contacts;
//Builder of ContactAdapter
    public ContactAdapter(List<Contact> contacts) {
        this.contacts = contacts;
    }
    //When clicking on a contact from the list
    interface ContactsListener {
        void onContactClikced(int position, View view);
    }

    ContactsListener listener;
// set the listener
    public void setListener(ContactsListener listener) {
        this.listener = listener;
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {

        ImageView contactIv;
        TextView nameTv;
        TextView numberTv;

        private ContactViewHolder(View itemView) {
            super(itemView);

            contactIv = itemView.findViewById(R.id.contact_image);
            nameTv = itemView.findViewById(R.id.contact_name);
            numberTv = itemView.findViewById(R.id.contact_phone);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(listener!=null)
                        listener.onContactClikced(getAdapterPosition(),view);
                }
            });
        }
    }

    @Override
    // item view
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        ContactViewHolder holder  = new ContactViewHolder(view);
        return holder;
    }

    @Override
    //Each item on the list will have a phone name and photo
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.contactIv.setImageBitmap(contact.getPhoto());
        holder.nameTv.setText(contact.getName());
        holder.numberTv.setText(contact.getPhone());
    }

    @Override
    //Returns the number of items in the list
    public int getItemCount() {
        return contacts.size();
    }
}
