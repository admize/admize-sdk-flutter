package io.admize.sdk.flutter.admize_flutter_sdk;

import android.content.Context;
import androidx.annotation.NonNull;

import io.admize.sdk.android.ads.AdmizeAdView;

/** Creates AdmizeAdView for banner ads. */
public class BannerAdCreator {

  @NonNull
  private final Context context;

  public BannerAdCreator(@NonNull Context context) {
    this.context = context;
  }

  public AdmizeAdView createAdView() {
    return new AdmizeAdView(context);
  }
}
