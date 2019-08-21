package com.example.webdatabaseacess.view;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.webdatabaseacess.R;
import com.example.webdatabaseacess.dao.ClinicDataBase;
import com.example.webdatabaseacess.model.Clinic;
import com.example.webdatabaseacess.util.NetworkUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ItemDetailListClinic extends AppCompatActivity {

    Clinic clinicItem;
    ImageView img_detail_logo_clinic;
    ToggleButton btn_favorite;
    Context context;
    String pathPhoto;
    final String TAG = "Details";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail_list_clinic);
        context = this.getApplicationContext();
        //pathPhoto = NetworkUtil.buildUrlImg(clinicItem.getUniq_id(), clinicItem.getFoto()).toString();

        Intent intent = getIntent();
        clinicItem = (Clinic) intent.getSerializableExtra("clinic");

        img_detail_logo_clinic = findViewById(R.id.img_detail_clinic_logo);
        TextView tv_detail_clinic_name = findViewById(R.id.tv_detail_clinic_name);
        btn_favorite = findViewById(R.id.btn_detail_favorite_clinic);


        Picasso.with(this)
                .load(NetworkUtil.buildUrlImg(clinicItem.getUniq_id(), clinicItem.getFoto()).toString())
                .into(img_detail_logo_clinic, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        img_detail_logo_clinic.setImageResource(R.drawable.ic_broken_image_gray_200dp);
                    }
                });

        tv_detail_clinic_name.setText(clinicItem.getNome_fantasia());


        //Favourite checked set img button
        if(clinicItem.getFavorite() == true) {
            btn_favorite.setChecked(true);
            btn_favorite.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_red_60dp));
        }

        btn_favorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    btn_favorite.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_red_60dp));
                    new SetFavorite(context).execute();
                }
                else {
                    btn_favorite.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_border_red_60dp));
                    new DeleteFavorite(context).execute();
                }
            }
        });
        //-----------------

    }

    class SetFavorite extends AsyncTask<Void, Void, Void> {

        SetFavorite(Context context){
            context = context;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if(clinicItem.getFavorite() == false) {
                //insert status favorite in db
                clinicItem.setFavorite(true);
                //insert item no db
                ClinicDataBase.getInstance(context).getDao().insert(clinicItem);
            }

            Log.d(TAG, "inseriu (teoricamente)");


            //List<Clinic> clinics = ClinicDataBase.getInstance(context).getDao().getAllClinics();

            /*Log.d(TAG, "Mostrar Lista");
            for(Clinic c : clinics){
                Log.d(TAG, "-->" + c.toString());
            }*/
            return null;
        }
    }

    class DeleteFavorite extends AsyncTask<Void, Void, Void> {

        DeleteFavorite(Context context){
            context = context;
        }

        @Override
        protected Void doInBackground(Void... voids) {


            if(clinicItem.getFavorite() == true) {
                //insert item no db
                ClinicDataBase.getInstance(context).getDao().delete(clinicItem);
            }

            //Log.d(TAG, "deletou (teoricamente)");


            //List<Clinic> clinics = ClinicDataBase.getInstance(context).getDao().getAllClinics();

            /*Log.d(TAG, "Mostrar Lista");
            for(Clinic c : clinics){
                Log.d(TAG, "-->" + c.toString());
            }*/
            return null;
        }

    }
}
