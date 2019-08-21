package com.example.webdatabaseacess.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.webdatabaseacess.R;
import com.example.webdatabaseacess.model.Clinic;
import com.example.webdatabaseacess.util.NetworkUtil;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Callback;

import java.util.List;

public class ClinicAdapter extends BaseAdapter {
    Context context;
    List<Clinic> listClinics;
    LayoutInflater inflter;
    ImageView img_clinic;

    public ClinicAdapter(Context applicationContext, List<Clinic> listClinics) {
        this.listClinics = listClinics;
        this.context = applicationContext;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return listClinics.size();
    }

    @Override
    public Clinic getItem(int position) {
        return listClinics.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = inflter.inflate(R.layout.show_list_clinica_evo, null);
        Clinic clinic = getItem(position);

        img_clinic = view.findViewById(R.id.img_icon_clinic);
        Picasso.with(view.getContext())
                .load(NetworkUtil.buildUrlImg(clinic.getUniq_id(), clinic.getFoto()).toString())
                .into(img_clinic, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        img_clinic.setImageResource(R.drawable.ic_broken_image_gray_200dp);
                    }
                });

        TextView tv_nameclinic = view.findViewById(R.id.tv_name_clinic);
        tv_nameclinic.setText(clinic.getNome_fantasia());

        return view;
    }
}
