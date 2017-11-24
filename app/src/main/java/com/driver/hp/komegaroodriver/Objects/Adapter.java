package com.driver.hp.komegaroodriver.Objects;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.driver.hp.komegaroodriver.Fragment.Modules.DirectionFinder;
import com.driver.hp.komegaroodriver.Fragment.Modules.DirectionFinderListener;
import com.driver.hp.komegaroodriver.Fragment.Modules.Route;
import com.driver.hp.komegaroodriver.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP on 01/03/2017.
 */

public class Adapter extends RecyclerView.Adapter<Adapter.TravelsViewHolder>{
    List<Travels> travelses;

    public Adapter(List<Travels> travelses) {
        this.travelses = travelses;
    }
    @Override
    public TravelsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_recycler, parent, false);
        TravelsViewHolder holder = new TravelsViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(TravelsViewHolder holder, int position) {
        Travels travels = travelses.get(position);
        holder.fecha.setText(travels.getDate());
        holder.hora.setText(travels.getStartHour());
        DecimalFormatSymbols simb = new DecimalFormatSymbols();
        simb.setGroupingSeparator('.');
        DecimalFormat form = new DecimalFormat("###,###", simb);
        String precio = "CLP $"+form.format(travels.getTripPrice());
        holder.precio.setText(precio);
        if(travels.getCertificatedNumber()!=null&&!travels.getCertificatedNumber().isEmpty()){
            holder.certificado.setText("Envio Certificado");
        }

        try {

            new DirectionFinder(holder, travels.getFrom(), travels.getTo()).execute();

        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return travelses.size();
    }





    public static class TravelsViewHolder extends RecyclerView.ViewHolder implements DirectionFinderListener, View.OnClickListener, OnMapReadyCallback {

        TextView fecha, hora, precio, certificado;
        GoogleMap mMap;
        MapView mapView;
        private List<Marker> originMarkers = new ArrayList<>();
        private List<Marker> destinationMarkers = new ArrayList<>();
        private List<Polyline> polylinePaths = new ArrayList<>();
        private final Context context;
        public static final String MESSAGE_KEY = "com.driver.hp.komegaroodriver.message_key";
        private LatLngBounds.Builder builder;
        private LatLngBounds bounds;

        public TravelsViewHolder(final View itemView) {
            super(itemView);
            context = itemView.getContext();
            itemView.setOnClickListener(this);
            fecha = (TextView) itemView.findViewById(R.id.textFecha);
            hora = (TextView) itemView.findViewById(R.id.textHora);
            precio = (TextView) itemView.findViewById(R.id.textPrecioT);
            certificado = (TextView) itemView.findViewById(R.id.txtCertificado);
            mapView = (MapView)itemView.findViewById(R.id.mapHistorial);
            mapView.setClickable(false);
            mapView.getMapAsync(this);
            mapView.onCreate(null);
            mapView.onResume();
            builder = new LatLngBounds.Builder();
        }

        @Override
        public void onDirectionFinderStart() {
            if (originMarkers != null) {
                for (Marker marker : originMarkers) {
                    marker.remove();
                }
            }

            if (destinationMarkers != null) {
                for (Marker marker : destinationMarkers) {
                    marker.remove();
                }
            }

            if (polylinePaths != null) {
                for (Polyline polyline : polylinePaths) {
                    polyline.remove();
                }
            }
        }

        @Override
        public void onDirectionFinderSuccess(final List<Route> routes) {
            polylinePaths = new ArrayList<>();
            originMarkers = new ArrayList<>();
            destinationMarkers = new ArrayList<>();
                    mMap.getUiSettings().setScrollGesturesEnabled(false);
                    mMap.getUiSettings().setZoomControlsEnabled(false);
                    for (Route route : routes) {
                        mMap.clear();
                            originMarkers.add(mMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.inicio))
                                    .title(route.startAddress)
                                    .position(route.startLocation)));
                            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.finaly))
                                    .title(route.endAddress)
                                    .position(route.endLocation)));

                            builder.include(route.startLocation);
                            builder.include(route.endLocation);

                            PolylineOptions polylineOptions = new PolylineOptions().
                                    geodesic(true).
                                    color(Color.rgb(119, 21, 204)).
                                    width(8);

                            for (int i = 0; i < route.points.size(); i++) {
                                polylineOptions.add(route.points.get(i));
                                builder.include(route.points.get(i));
                            }

                            polylinePaths.add(mMap.addPolyline(polylineOptions));

                            bounds = builder.build();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 90));

                        }


        }

        @Override
        public void onClick(View v) {

            final Intent intent;
                intent =  new Intent(context, TravelsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                Activity activity = (Activity) context;
                intent.putExtra(MESSAGE_KEY,getAdapterPosition());
                Log.v("Position",String.valueOf(getAdapterPosition()));
                context.startActivity(intent);
                activity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_in_left);

        }


        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            LatLng sydney = new LatLng(-33.4724227, -70.7699159);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 9));
        }
    }



}
