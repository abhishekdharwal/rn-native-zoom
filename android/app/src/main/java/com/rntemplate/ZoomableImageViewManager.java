package com.rntemplate;


import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ZoomableImageViewManager extends SimpleViewManager<SubsamplingScaleImageView> {

    public static final String REACT_CLASS = "ZoomableImageView";
    static final String REACT_ON_SCALE_CHANGED_EVENT = "onScaleChanged";
    static final String REACT_ON_CENTER_CHANGED_EVENT = "onSubsamplingScaleImageCenterChanged";


    @NonNull
    @Override
    public String getName() {
        return REACT_CLASS;
    }
    @Nullable
    private RequestBuilder<Bitmap> requestManager = null;


    @androidx.annotation.Nullable
    @Override
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.<String, Object>builder()
                .put(REACT_ON_SCALE_CHANGED_EVENT, MapBuilder.of("registrationName", REACT_ON_SCALE_CHANGED_EVENT))
                .put(REACT_ON_CENTER_CHANGED_EVENT, MapBuilder.of("registrationName", REACT_ON_CENTER_CHANGED_EVENT))
                .build();
    }

    @NonNull
    @Override
    protected SubsamplingScaleImageView createViewInstance(@NonNull ThemedReactContext reactContext) {
        return new SubsamplingScaleImageView(reactContext);
    }

    @ReactProp(name = "src")
    public void setSrc(SubsamplingScaleImageView view, String src) {
        Glide.with(view.getContext())
                .downloadOnly()
                .load(src)
                .into(new CustomTarget<File>() {
                    @Override
                    public void onResourceReady(@NonNull File resource, Transition<? super File> transition) {
                        // Load the downloaded file into SubsamplingScaleImageView
                        view.setImage(ImageSource.uri(resource.getAbsolutePath()));
                        view.setOnStateChangedListener(new SubsamplingScaleImageView.OnStateChangedListener() {
                            @Override
                            public void onScaleChanged(float newScale, int origin) {
                                WritableMap event = Arguments.createMap();
                                event.putDouble("newScale", newScale);
                                event.putDouble("origin", origin);
                                ReactContext reactContext = (ReactContext) view.getContext();
                                int viewId = view.getId();
                                reactContext.getJSModule(RCTEventEmitter.class)
                                        .receiveEvent(viewId, REACT_ON_SCALE_CHANGED_EVENT, event);
                            }

                            @Override
                            public void onCenterChanged(PointF newCenter, int origin) {
                                WritableMap event = Arguments.createMap();
                                WritableMap center = Arguments.createMap();
                                center.putDouble("x", newCenter.x);
                                center.putDouble("y", newCenter.y);
                                event.putMap("newCenter", center);
                                event.putDouble("origin", origin);
                                ReactContext reactContext = (ReactContext) view.getContext();
                                int viewId = view.getId();
                                reactContext.getJSModule(RCTEventEmitter.class)
                                        .receiveEvent(viewId, REACT_ON_CENTER_CHANGED_EVENT, event);
                            }
                        });
                    }

                    @Override
                    public void onLoadCleared(Drawable placeholder) {
                        // Handle if needed (optional)
                    }

                    @Override
                    public void onLoadFailed(Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        // Handle load failure if needed (e.g., show a placeholder or log an error)
                    }
                });

        if (!src.isEmpty()) {

        }
    }


    @ReactProp(name = "maxScale", defaultFloat = 2.0f)
    public void setMaxScale(SubsamplingScaleImageView view, float maxScale) {
        view.setMaxScale(maxScale);
    }
    @ReactProp(name = "zoomEnabled")
    public void setZoomEnabled(SubsamplingScaleImageView view, boolean zoomEnabled) {
        view.setZoomEnabled(zoomEnabled);
    }
    @Override
    public void onDropViewInstance(@NonNull SubsamplingScaleImageView view) {
        super.onDropViewInstance(view);
    }
}
