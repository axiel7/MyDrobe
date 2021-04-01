package com.axiel7.mydrobe.utils

import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.view.*
import com.google.android.material.bottomsheet.BottomSheetBehavior

object InsetsHelper {

    fun View.addSystemWindowInsetToPadding(
        left: Boolean = false,
        top: Boolean = false,
        right: Boolean = false,
        bottom: Boolean = false
    ) {
        val (initialLeft, initialTop, initialRight, initialBottom) =
            listOf(paddingLeft, paddingTop, paddingRight, paddingBottom)

        ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
            view.updatePadding(
                left = initialLeft + (if (left) insets.getInsets(WindowInsetsCompat.Type.systemBars()).left else 0),
                top = initialTop + (if (top) insets.getInsets(WindowInsetsCompat.Type.systemBars()).top else 0),
                right = initialRight + (if (right) insets.getInsets(WindowInsetsCompat.Type.systemBars()).right else 0),
                bottom = initialBottom + (if (bottom) insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom else 0)
            )
            insets
        }
        requestApplyInsetsWhenAttached()
    }

    fun View.addSystemWindowInsetToMargin(
        left: Boolean = false,
        top: Boolean = false,
        right: Boolean = false,
        bottom: Boolean = false
    ) {
        val (initialLeft, initialTop, initialRight, initialBottom) =
            listOf(marginLeft, marginTop, marginRight, marginBottom)

        ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
            view.updateLayoutParams {
                (this as? ViewGroup.MarginLayoutParams)?.let {
                    updateMargins(
                        left = initialLeft + (if (left) insets.getInsets(WindowInsetsCompat.Type.systemBars()).left else 0),
                        top = initialTop + (if (top) insets.getInsets(WindowInsetsCompat.Type.systemBars()).top else 0),
                        right = initialRight + (if (right) insets.getInsets(WindowInsetsCompat.Type.systemBars()).right else 0),
                        bottom = initialBottom + (if (bottom) insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom else 0)
                    )
                }
            }
            insets
        }
        requestApplyInsetsWhenAttached()
    }

    private fun View.requestApplyInsetsWhenAttached() {
        if (isAttachedToWindow) {
            // We're already attached, just request as normal
            requestApplyInsets()
        } else {
            // We're not attached to the hierarchy, add a listener to
            // request when we are
            addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(v: View) {
                    v.removeOnAttachStateChangeListener(this)
                    v.requestApplyInsets()
                }

                override fun onViewDetachedFromWindow(v: View) = Unit
            })
        }
    }

    fun <T : ViewGroup> getViewBottomHeight(
        layout: ViewGroup,
        targetViewId: Int,
        behavior: BottomSheetBehavior<T>
    ) {
        layout.apply {
            viewTreeObserver.addOnGlobalLayoutListener(
                object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        viewTreeObserver.removeOnGlobalLayoutListener(this)
                        behavior.peekHeight = findViewById<View>(targetViewId).bottom
                    }
                })
        }
    }
}