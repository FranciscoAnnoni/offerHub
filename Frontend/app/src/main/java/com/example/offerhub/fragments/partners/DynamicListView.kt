package com.example.offerhub.fragments.partners

import android.content.Context
import android.util.AttributeSet
import android.widget.ListView

class DynamicListView(context: Context, attrs: AttributeSet) : ListView(context, attrs) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val expandSpec = MeasureSpec.makeMeasureSpec(
            Int.MAX_VALUE shr 2, MeasureSpec.AT_MOST
        )
        super.onMeasure(widthMeasureSpec, expandSpec)
    }
}
