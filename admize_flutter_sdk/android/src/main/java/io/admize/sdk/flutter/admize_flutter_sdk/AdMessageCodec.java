package io.admize.sdk.flutter.admize_flutter_sdk;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.List;

import io.flutter.plugin.common.StandardMessageCodec;

/**
 * Encodes and decodes values by reading from a ByteBuffer and writing to a ByteArrayOutputStream.
 */
class AdMessageCodec extends StandardMessageCodec {
    // The type values below must be consistent for each platform.
    private static final byte VALUE_AD_REQUEST = (byte) 129;

    @NonNull
    Context context;

    AdMessageCodec(@NonNull Context context) {
        this.context = context;
    }

    void setContext(@NonNull Context context) {
        this.context = context;
    }

    @Override
    protected void writeValue(ByteArrayOutputStream stream, Object value) {
        if (value instanceof FlutterAdRequest) {
            stream.write(VALUE_AD_REQUEST);
            final FlutterAdRequest request = (FlutterAdRequest) value;
            writeValue(stream, request.getAdmizeAdType());
            writeValue(stream, request.getPublisherUid());
            writeValue(stream, request.getPlacementUid());
            writeValue(stream, request.getMediaUid());
            writeValue(stream, request.getAdmizeMultiBidsList());
            writeValue(stream, request.getCoppaEnabled());
            writeValue(stream, request.getSetTest());
        } else {
            super.writeValue(stream, value);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Object readValueOfType(byte type, ByteBuffer buffer) {
        switch (type) {
            case VALUE_AD_REQUEST:
                return new FlutterAdRequest.Builder((String) readValueOfType(buffer.get(), buffer),
                        (String) readValueOfType(buffer.get(), buffer),
                        (String) readValueOfType(buffer.get(), buffer),
                        (String) readValueOfType(buffer.get(), buffer))
                        .setAdmizeMultiBidsList((List<String>) readValueOfType(buffer.get(), buffer))
                        .setCoppaEnabled(booleanValueOf(readValueOfType(buffer.get(), buffer)))
                        .setSetTest(booleanValueOf(readValueOfType(buffer.get(), buffer)))
                        .build();
            default:
                return super.readValueOfType(type, buffer);
        }
    }

    @Nullable
    private static Boolean booleanValueOf(@Nullable Object object) {
        if (object == null) {
            return null;
        }
        return (Boolean) object;
    }
}
