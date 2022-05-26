package com.udacity.asteroidradar

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.main.AsteroidAdapter
import com.udacity.asteroidradar.main.ApiStatus


@BindingAdapter("asteroidApiStatus")
fun ImageView.bindAsteroidApiStatus(status: ApiStatus?) {
    when (status) {
        ApiStatus.ERROR -> {
            this.visibility = View.VISIBLE
        }
        else -> {
            this.visibility = View.GONE
        }
    }
}

@BindingAdapter("loadingStatus")
fun ProgressBar.bindAsteroidLoadingStatus(status: ApiStatus?) {
    when (status) {
        ApiStatus.LOADING -> {
            this.visibility = View.VISIBLE
        }
        else -> {
            this.visibility = View.GONE
        }
    }
}

@BindingAdapter("listData")
fun RecyclerView.bindListData(data: List<Asteroid>?) {
    val adapter = this.adapter as AsteroidAdapter
    adapter.submitList(data)
}

@BindingAdapter("statusIcon")
fun ImageView.bindAsteroidStatusImage(isHazardous: Boolean) {
    contentDescription = if (isHazardous) {
        setImageResource(R.drawable.ic_status_potentially_hazardous)
        "Unhappy face icon."
    } else {
        setImageResource(R.drawable.ic_status_normal)
        "Happy face icon."
    }
}

@BindingAdapter("asteroidStatusImage")
fun ImageView.bindDetailsStatusImage(isHazardous: Boolean) {
    contentDescription = if (isHazardous) {
        setImageResource(R.drawable.asteroid_hazardous)
        "Image depicting a hazardous asteroid."
    } else {
        setImageResource(R.drawable.asteroid_safe)
        "Image depicting a non-hazardous asteroid."
    }
}

@BindingAdapter("astronomicalUnitText")
fun bindTextViewToAstronomicalUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.astronomical_unit_format), number)
}

@BindingAdapter("kmUnitText")
fun bindTextViewToKmUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_unit_format), number)
}

@BindingAdapter("velocityText")
fun bindTextViewToDisplayVelocity(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_s_unit_format), number)
}

@BindingAdapter("imageDescription")
fun ImageView.bindImageDescription(title: String) {
    contentDescription = "The image of the day with the title, $title"
}

@BindingAdapter("imageUrl")
fun ImageView.bindImageOfTheDayImage(imageSrcUrl: String?) {
    Log.d("kento", "Attempting to load the image of the day using Picasso: $imageSrcUrl")
    imageSrcUrl?.let {
        val imgUri = it.toUri().buildUpon().scheme("https").build()
        Picasso.with(this.context)
            .load(imgUri)
            .placeholder(R.drawable.loading_animation)
            .error(R.drawable.outline_broken_image)
            .into(this)
    }
}
