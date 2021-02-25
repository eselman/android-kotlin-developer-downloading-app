package com.eselman.android.downloadingapp.data

import android.os.Parcel
import android.os.Parcelable

data class FileDownloaded(val uri: String?, val fileName: String?, val status: String?):
        Parcelable {
    constructor(parcel: Parcel) : this (
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uri)
        parcel.writeString(fileName)
        parcel.writeString(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FileDownloaded> {
        override fun createFromParcel(parcel: Parcel): FileDownloaded {
            return FileDownloaded(parcel)
        }

        override fun newArray(size: Int): Array<FileDownloaded?> {
            return arrayOfNulls(size)
        }
    }
}