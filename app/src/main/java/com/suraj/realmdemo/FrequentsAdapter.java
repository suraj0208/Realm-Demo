package com.suraj.realmdemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by suraj on 5/2/17.
 */
public class FrequentsAdapter extends RecyclerView.Adapter<FrequentsAdapter.MyViewHolder> {

    private final Context context;
    private final LayoutInflater inflater;
    private final List<Contact> contacts;
    private ClickListener clickListener;

    public List<Contact> getContacts() {
        return contacts;
    }

    public FrequentsAdapter(Context context, List<Contact> contacts, ClickListener clickListener) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.contacts = contacts;
        this.clickListener = clickListener;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.imageview_column, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.imgviewPicture.setImageDrawable(contacts.get(position).getPicture());
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imgviewPicture;

        public MyViewHolder(View itemView) {
            super(itemView);
            imgviewPicture = (ImageView) itemView.findViewById(R.id.imgviewcolumn);
            imgviewPicture.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.itemClicked(v,getAdapterPosition());
        }
    }

    public interface ClickListener {
        void itemClicked(View view, int position);
    }
}
