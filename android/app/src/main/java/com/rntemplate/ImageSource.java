package com.rntemplate;


import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.text.TextUtils;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;

import javax.annotation.Nullable;

public class ImageSource extends com.facebook.react.views.imagehelper.ImageSource {
    private static final String DATA_SCHEME = "data";
    private static final String LOCAL_RESOURCE_SCHEME = "res";
    private static final String ANDROID_RESOURCE_SCHEME = "android.resource";
    private static final String ANDROID_CONTENT_SCHEME = "content";
    private static final String LOCAL_FILE_SCHEME = "file";
    private Headers mHeaders;
    private Uri mUri;

    public static boolean isBase64Uri(Uri uri) {
        return DATA_SCHEME.equals(uri.getScheme());
    }

    public static boolean isLocalResourceUri(Uri uri) {
        return LOCAL_RESOURCE_SCHEME.equals(uri.getScheme());
    }

    public static boolean isResourceUri(Uri uri) {
        return ANDROID_RESOURCE_SCHEME.equals(uri.getScheme());
    }

    public static boolean isContentUri(Uri uri) {
        return ANDROID_CONTENT_SCHEME.equals(uri.getScheme());
    }

    public static boolean isLocalFileUri(Uri uri) {
        return LOCAL_FILE_SCHEME.equals(uri.getScheme());
    }

    public ImageSource(Context context, String source) {
        this(context, source, null);
    }

    public ImageSource(Context context, String source, @Nullable Headers headers) {
        this(context, source, 0.0d, 0.0d, headers);
    }

    public ImageSource(Context context, String source, double width, double height, @Nullable Headers headers) {
        super(context, source, width, height);
        mHeaders = headers == null ? Headers.DEFAULT : headers;
        mUri = super.getUri();

        if (isResource() && TextUtils.isEmpty(mUri.toString())) {
            throw new Resources.NotFoundException("Local Resource Not Found. Resource: '" + getSource() + "'.");
        }

        if (isLocalResourceUri(mUri)) {
            // Convert res:/ scheme to android.resource:// so
            // glide can understand the uri.
            mUri = Uri.parse(mUri.toString().replace("res:/", ANDROID_RESOURCE_SCHEME + "://" + context.getPackageName() + "/"));
        }
    }


    public boolean isBase64Resource() {
        return mUri != null && ImageSource.isBase64Uri(mUri);
    }

    public boolean isResource() {
        return mUri != null && ImageSource.isResourceUri(mUri);
    }

    public boolean isLocalFile() {
        return mUri != null && ImageSource.isLocalFileUri(mUri);
    }

    public boolean isContentUri() {
        return mUri != null && ImageSource.isContentUri(mUri);
    }

    public Object getSourceForLoad() {
        if (isContentUri()) {
            return getSource();
        }
        if (isBase64Resource()) {
            return getSource();
        }
        if (isResource()) {
            return getUri();
        }
        if (isLocalFile()) {
            return getUri().toString();
        }
        return getGlideUrl();
    }

    @Override
    public Uri getUri() {
        return mUri;
    }

    public Headers getHeaders() {
        return mHeaders;
    }

    public GlideUrl getGlideUrl() {
        return new GlideUrl(getUri().toString(), getHeaders());
    }
}
