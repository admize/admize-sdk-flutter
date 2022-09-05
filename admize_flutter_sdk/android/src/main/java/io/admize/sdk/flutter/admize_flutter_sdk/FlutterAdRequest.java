// Copyright 2021 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package io.admize.sdk.flutter.admize_flutter_sdk;

import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import io.admize.sdk.android.ads.ADMIZE_AD_SIZE;
import io.admize.sdk.android.ads.ADMIZE_AD_TYPE;
import io.admize.sdk.android.ads.AdmizeAdRequest;

class FlutterAdRequest {
    @Nullable
    private final String admizeAdType;
    @Nullable
    private final String publisherUid;
    @Nullable
    private final String placementUid;
    @Nullable
    private final String mediaUid;
    @Nullable
    private final List<String> admizeMultiBidsList;
    @Nullable
    private final Boolean coppaEnabled;
    @Nullable
    private final Boolean setTest;

    //TODO 추후 없어질 옵션임


    protected static class Builder {
        private final String admizeAdType;

        private final String publisherUid;

        private final String placementUid;

        private final String mediaUid;
        @Nullable
        private List<String> admizeMultiBidsList;
        @Nullable
        private Boolean coppaEnabled;
        @Nullable
        private Boolean setTest;

        //TODO 추후 없어질 옵션임


        public Builder(String admizeAdType,
                       String publisherUid,
                       String placementUid,
                       String mediaUid) {
            this.admizeAdType = admizeAdType;
            this.publisherUid = publisherUid;
            this.placementUid = placementUid;
            this.mediaUid = mediaUid;
        }

        public FlutterAdRequest.Builder setAdmizeMultiBidsList(@Nullable List<String> admizeMultiBidsList) {
            this.admizeMultiBidsList = admizeMultiBidsList;
            return this;
        }

        public FlutterAdRequest.Builder setCoppaEnabled(@Nullable Boolean coppaEnabled) {
            this.coppaEnabled = coppaEnabled;
            return this;
        }

        public FlutterAdRequest.Builder setSetTest(@Nullable Boolean setTest) {
            this.setTest = setTest;
            return this;
        }

        @Nullable
        public String getAdmizeAdType() {
            return admizeAdType;
        }

        @Nullable
        public String getPublisherUid() {
            return publisherUid;
        }

        @Nullable
        public String getPlacementUid() {
            return placementUid;
        }

        @Nullable
        public String getMediaUid() {
            return mediaUid;
        }

        @Nullable
        public List<String> getAdmizeMultiBidsList() {
            return admizeMultiBidsList;
        }

        @Nullable
        public Boolean getCoppaEnabled() {
            return coppaEnabled;
        }

        @Nullable
        public Boolean getSetTest() {
            return setTest;
        }

        FlutterAdRequest build() {
            return new FlutterAdRequest(
                    admizeAdType,
                    publisherUid,
                    placementUid,
                    mediaUid,
                    admizeMultiBidsList,
                    coppaEnabled,
                    setTest
            );
        }
    }

    protected FlutterAdRequest(
            String admizeAdType,
            String publisherUid,
            String placementUid,
            String mediaUid,
            @Nullable List<String> admizeMultiBidsList,
            @Nullable Boolean coppaEnabled,
            @Nullable Boolean setTest) {
        this.admizeAdType = admizeAdType;
        this.publisherUid = publisherUid;
        this.placementUid = placementUid;
        this.mediaUid = mediaUid;
        this.admizeMultiBidsList = admizeMultiBidsList;
        this.coppaEnabled = coppaEnabled;
        this.setTest = setTest;
    }

    protected AdmizeAdRequest.Builder updateAdRequestBuilder(AdmizeAdRequest.Builder builder) {
        builder.admizeAdType(ADMIZE_AD_TYPE.valueOf(admizeAdType.toUpperCase()));
        builder.publisherUid(publisherUid);
        builder.placementUid(placementUid);
        builder.mediaUid(mediaUid);

        if (admizeMultiBidsList != null) {
            List<ADMIZE_AD_SIZE> sizeList = admizeMultiBidsList.stream()
                    .map(sizeString -> ADMIZE_AD_SIZE.valueOf(sizeString.toUpperCase()))
                    .collect(Collectors.toList());
            builder.admizeMultiBidsList(sizeList);
        }

        if (coppaEnabled != null) {
            builder.coppaEnabled(coppaEnabled);
        }
        if (setTest != null) {
            builder.setTest(setTest);
        }
        return builder;
    }

    AdmizeAdRequest asAdRequest() {
        return updateAdRequestBuilder(new AdmizeAdRequest.Builder()).build();
    }

    @Nullable
    protected String getAdmizeAdType() {
        return admizeAdType;
    }

    @Nullable
    protected String getPublisherUid() {
        return publisherUid;
    }

    @Nullable
    protected String getPlacementUid() {
        return placementUid;
    }

    @Nullable
    protected String getMediaUid() {
        return mediaUid;
    }

    @Nullable
    protected List<String> getAdmizeMultiBidsList() {
        return admizeMultiBidsList;
    }

    @Nullable
    protected Boolean getCoppaEnabled() {
        return coppaEnabled;
    }

    @Nullable
    protected Boolean getSetTest() {
        return setTest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FlutterAdRequest)) return false;
        FlutterAdRequest that = (FlutterAdRequest) o;
        return admizeAdType.equals(that.admizeAdType)
                && publisherUid.equals(that.publisherUid)
                && placementUid.equals(that.placementUid)
                && mediaUid.equals(that.mediaUid)
                && Objects.equals(admizeMultiBidsList, that.admizeMultiBidsList)
                && Objects.equals(coppaEnabled, that.coppaEnabled)
                && Objects.equals(setTest, that.setTest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(admizeAdType,
                publisherUid,
                placementUid,
                mediaUid,
                admizeMultiBidsList,
                coppaEnabled,
                setTest
        );
    }
}
