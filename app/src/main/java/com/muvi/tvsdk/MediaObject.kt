package com.muvi.tvsdk

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class MediaObject(

    @field:SerializedName("drm_license_uri")
    val drmLicenseUri: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("uri")
    val uri: String? = null,

    @field:SerializedName("isLive")
    val isLive: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(drmLicenseUri)
        parcel.writeString(name)
        parcel.writeString(uri)
        parcel.writeByte(if (isLive) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MediaObject> {
        override fun createFromParcel(parcel: Parcel): MediaObject {
            return MediaObject(parcel)
        }

        override fun newArray(size: Int): Array<MediaObject?> {
            return arrayOfNulls(size)
        }
    }
}
