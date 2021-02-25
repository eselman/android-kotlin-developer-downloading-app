package com.eselman.android.downloadingapp


sealed class ButtonState {
    object Canceled : ButtonState()
    object Loading : ButtonState()
    object Completed : ButtonState()
}