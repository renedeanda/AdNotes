package com.dcrtns.adnotebook.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class AdNote(var id: Int = -1,
             var headline1: String? = null,
             var headline2: String? = null,
             var headline3: String? = null,
             var description: String? = null,
             var description2: String? = null,
             var mainUrl: String? = null,
             var urlPath1: String? = null,
             var urlPath2: String? = null,
             var adCreateDate: String? = null,
             var adUpdateDate: String? = null,
             var campaignName: String? = null,
             var adGroupName: String? = null,
             var extraNotes: String? = null) : Parcelable