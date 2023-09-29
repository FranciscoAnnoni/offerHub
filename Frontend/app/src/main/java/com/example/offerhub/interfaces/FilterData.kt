package com.example.offerhub.interfaces
import android.os.Parcel
import android.os.Parcelable

data class FilterData(
    var desde: String,
    var hasta: String,
    var tiposPromocion: MutableList<String>,
    var diasPromocion: MutableList<String>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: mutableListOf<String>() as MutableList<String>,
        parcel.createStringArrayList() ?: mutableListOf<String>() as MutableList<String>
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(desde)
        parcel.writeString(hasta)
        parcel.writeStringList(tiposPromocion)
        parcel.writeStringList(diasPromocion)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FilterData> {
        override fun createFromParcel(parcel: Parcel): FilterData {
            return FilterData(parcel)
        }

        override fun newArray(size: Int): Array<FilterData?> {
            return arrayOfNulls(size)
        }
    }
}
