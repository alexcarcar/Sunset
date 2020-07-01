package alex.carcar.sunset

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var sceneView: View
    private lateinit var sunView: View
    private lateinit var skyView: View
    private var sunsetReverse = false

    private val blueSkyColor: Int by lazy {
        ContextCompat.getColor(this, R.color.blue_sky)
    }
    private val sunsetSkyColor: Int by lazy {
        ContextCompat.getColor(this, R.color.sunset_sky)
    }
    private val nightSkyColor: Int by lazy {
        ContextCompat.getColor(this, R.color.night_sky)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sceneView = findViewById(R.id.scene)
        sunView = findViewById(R.id.sun)
        skyView = findViewById(R.id.sky)

        sceneView.setOnClickListener {
            startAnimation(sunsetReverse)
            sunsetReverse = !sunsetReverse
        }
    }

    private fun startAnimation(reverse: Boolean) {
        var sunYStart = sunView.top.toFloat()
        var sunYEnd = skyView.height.toFloat()
        if (reverse) sunYStart = sunYEnd.also { sunYEnd = sunYStart }
        val heightAnimator = ObjectAnimator
            .ofFloat(sunView, "y", sunYStart, sunYEnd)
            .setDuration(3000)
        heightAnimator.interpolator = AccelerateInterpolator()

        val skyStartColor = if (reverse) sunsetSkyColor else blueSkyColor
        val skyEndColor = if (reverse) blueSkyColor else sunsetSkyColor
        val sunsetSkyAnimator = ObjectAnimator
            .ofInt(skyView, "backgroundColor", skyStartColor, skyEndColor)
            .setDuration(3000)
        sunsetSkyAnimator.setEvaluator(ArgbEvaluator())

        val nightStartColor = if (reverse) nightSkyColor else sunsetSkyColor
        val nightEndColor = if (reverse) sunsetSkyColor else nightSkyColor
        val nightSkyAnimator = ObjectAnimator
            .ofInt(nightSkyColor, "backgroundColor", nightStartColor, nightEndColor)
            .setDuration(1500)
        nightSkyAnimator.setEvaluator(ArgbEvaluator())

        val animatorSet = AnimatorSet()
        animatorSet.play(heightAnimator)
            .with(sunsetSkyAnimator)
            .before(nightSkyAnimator)
        animatorSet.start()
    }
}