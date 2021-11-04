package com.mcgars.solarsystem.data.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize

@Parcelize
class Planet(
    @StringRes
    val text: Int,
    @StringRes
    val description: Int,
    @DrawableRes
    val icon: Int,

): Parcelable