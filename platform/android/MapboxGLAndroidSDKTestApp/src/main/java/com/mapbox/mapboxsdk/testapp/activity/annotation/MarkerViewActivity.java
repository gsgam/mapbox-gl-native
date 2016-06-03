package com.mapbox.mapboxsdk.testapp.activity.annotation;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.MarkerView;
import com.mapbox.mapboxsdk.annotations.MarkerViewManager;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.testapp.R;
import com.mapbox.mapboxsdk.testapp.model.annotations.CountryMarkerOptions;
import com.mapbox.mapboxsdk.testapp.model.annotations.CountryMarkerView;
import com.mapbox.mapboxsdk.testapp.model.annotations.CountryMarkerViewOptions;
import com.mapbox.mapboxsdk.testapp.model.annotations.TextMarkerView;
import com.mapbox.mapboxsdk.testapp.model.annotations.TextMarkerViewOptions;

public class MarkerViewActivity extends AppCompatActivity {

    private MapboxMap mMapboxMap;
    private MapView mMapView;

    private final static LatLng[] LAT_LNGS = new LatLng[]{
            new LatLng(38.897424, -77.036508),
            new LatLng(38.909698, -77.029642),
            new LatLng(38.907227, -77.036530),
            new LatLng(38.905607, -77.031916),
            new LatLng(38.889441, -77.050134)
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        final TextView viewCountView = (TextView) findViewById(R.id.countView);
        mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                mMapboxMap = mapboxMap;

                final MarkerViewManager markerViewManager = mapboxMap.getMarkerViewManager();

                Icon usFlag = IconFactory.getInstance(MarkerViewActivity.this)
                        .fromResource(R.drawable.ic_us);

                // add default ViewMarker markers
                for (int i = 0; i < LAT_LNGS.length; i++) {
                    mMapboxMap.addMarker(new MarkerViewOptions()
                            .position(LAT_LNGS[i])
                            .title(String.valueOf(i))
                            .icon(usFlag)
                            .selectAnimatorResource(R.animator.scale_up)
                            .deselectAnimatorResource(R.animator.scale_down)
                    );
                }

                // add custom ViewMarker
                CountryMarkerViewOptions options = new CountryMarkerViewOptions();
                options.flagRes(R.drawable.icon_burned);
                options.abbrevName("Mapbox");
                options.title("Hello");
                options.position(new LatLng(38.899774, -77.023237));
                options.selectAnimatorResource(R.animator.rotate_360);
                options.deselectAnimatorResource(R.animator.rotate_360);
                options.flat(true);
                mapboxMap.addMarker(options);

                mMapboxMap.addMarker(new MarkerOptions()
                        .title("United States")
                        .position(new LatLng(38.902580, -77.050102))
                );

                mMapboxMap.addMarker(new TextMarkerViewOptions()
                        .text("A")
                        .position(new LatLng(38.889876, -77.008849))
                );

                mMapboxMap.addMarker(new TextMarkerViewOptions()
                        .text("B")
                        .position(new LatLng(38.907327, -77.041293))
                );

                mMapboxMap.addMarker(new TextMarkerViewOptions()
                        .text("C")
                        .position(new LatLng(38.897642, -77.041980))
                );

                // set adapters for child classes of ViewMarker
                markerViewManager.addMarkerViewAdapter(new CountryAdapter(MarkerViewActivity.this, mapboxMap));
                markerViewManager.addMarkerViewAdapter(new TextAdapter(MarkerViewActivity.this, mapboxMap));

                // add a change listener to validate the size of amount of child views
                mMapView.addOnMapChangedListener(new MapView.OnMapChangedListener() {
                    @Override
                    public void onMapChanged(@MapView.MapChange int change) {
                        if (change == MapView.REGION_IS_CHANGING || change == MapView.REGION_DID_CHANGE) {
                            if (!markerViewManager.getMarkerViewAdapters().isEmpty() && viewCountView != null) {
                                viewCountView.setText("ViewCache size " + (mMapView.getChildCount() - 5));
                            }
                        }
                    }
                });

                // add a OnMarkerView click listener
                mMapboxMap.getMarkerViewManager().setOnMarkerViewClickListener(new MapboxMap.OnMarkerViewClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker, @NonNull View view, @NonNull MapboxMap.MarkerViewAdapter adapter) {
                        Toast.makeText(MarkerViewActivity.this, "Hello " + marker.getId(), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });
            }
        });
    }

    private static class CountryAdapter extends MapboxMap.MarkerViewAdapter<CountryMarkerView> {

        private LayoutInflater inflater;
        private MapboxMap mapboxMap;

        public CountryAdapter(@NonNull Context context, @NonNull MapboxMap mapboxMap) {
            super(context);
            this.inflater = LayoutInflater.from(context);
            this.mapboxMap = mapboxMap;
        }

        @Nullable
        @Override
        public View getView(@NonNull CountryMarkerView marker, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.view_custom_marker, parent, false);
                viewHolder.flag = (ImageView) convertView.findViewById(R.id.imageView);
                viewHolder.abbrev = (TextView) convertView.findViewById(R.id.textView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.flag.setImageResource(marker.getFlagRes());
            viewHolder.abbrev.setText(marker.getAbbrevName());
            return convertView;
        }

        @Override
        public boolean onSelect(@NonNull final CountryMarkerView marker, @NonNull final View convertView, boolean reselectionForViewReuse) {
            convertView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(convertView, View.ROTATION, 0, 360);
            rotateAnimator.setDuration(reselectionForViewReuse ? 0 : 350);
            rotateAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    convertView.setLayerType(View.LAYER_TYPE_NONE, null);
                    mapboxMap.selectMarker(marker);
                }
            });
            rotateAnimator.start();

            // false indicates that we are calling selectMarker after our animation ourselves
            // true will let the system call it for you, which will result in showing an InfoWindow instantly
            return false;
        }

        @Override
        public void onDeselect(@NonNull CountryMarkerView marker, @NonNull final View convertView) {
            convertView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(convertView, View.ROTATION, 360, 0);
            rotateAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    convertView.setLayerType(View.LAYER_TYPE_NONE, null);
                }
            });
            rotateAnimator.start();
        }

        private static class ViewHolder {
            ImageView flag;
            TextView abbrev;
        }
    }


    private static class TextAdapter extends MapboxMap.MarkerViewAdapter<TextMarkerView> {

        private LayoutInflater inflater;
        private MapboxMap mapboxMap;

        public TextAdapter(@NonNull Context context, @NonNull MapboxMap mapboxMap) {
            super(context);
            this.inflater = LayoutInflater.from(context);
            this.mapboxMap = mapboxMap;
        }

        @Nullable
        @Override
        public View getView(@NonNull TextMarkerView marker, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.view_text_marker, parent, false);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.textView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.textView.setText(marker.getText());
            return convertView;
        }

        @Override
        public boolean onSelect(@NonNull final TextMarkerView marker, @NonNull final View convertView, boolean reselectionForViewReuse) {
            animateGrow(marker, convertView, 0);

            // false indicates that we are calling selectMarker after our animation ourselves
            // true will let the system call it for you, which will result in showing an InfoWindow instantly
            return false;
        }

        @Override
        public void onDeselect(@NonNull TextMarkerView marker, @NonNull final View convertView) {
            animateShrink(convertView, 350);
        }

        @Override
        public boolean prepareViewForReuse(@NonNull MarkerView marker, @NonNull View convertView) {
            // this method is called before a view will be reused, we need to restore view state
            // as we have scaled the view in onSelect. If not correctly applied other MarkerView will
            // become large since these have been recycled

            // cancel ongoing animation
            convertView.animate().cancel();

            if (marker.isSelected()) {
                // shrink view to be able to be reused
                animateShrink(convertView, 0);
            }

            // true if you want reuse to occur automatically, false if you want to manage this yourself
            return true;
        }

        private void animateGrow(@NonNull final MarkerView marker, @NonNull final View convertView, int duration) {
            convertView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            Animator animator = AnimatorInflater.loadAnimator(convertView.getContext(), R.animator.scale_up);
            animator.setDuration(duration);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    convertView.setLayerType(View.LAYER_TYPE_NONE, null);
                    mapboxMap.selectMarker(marker);
                }
            });
            animator.setTarget(convertView);
            animator.start();
        }

        private void animateShrink(@NonNull final View convertView, int duration) {
            convertView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            Animator animator = AnimatorInflater.loadAnimator(convertView.getContext(), R.animator.scale_down);
            animator.setDuration(duration);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    convertView.setLayerType(View.LAYER_TYPE_NONE, null);
                }
            });
            animator.setTarget(convertView);
            animator.start();
        }

        private static class ViewHolder {
            TextView textView;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
