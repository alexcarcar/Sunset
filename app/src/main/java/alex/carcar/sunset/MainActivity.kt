package alex.carcar.sunset

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


private const val DURATION: Long = 3000

class MainActivity : AppCompatActivity() {

    private lateinit var sceneView: View
    private lateinit var sunView: View
    private lateinit var skyView: View
    private var goingUp = false

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
        animateSunPulse()

        sceneView.setOnClickListener {
            sunAnimation(goingUp)
            goingUp = !goingUp
        }
    }

    private fun animateSunPulse() {
        val pulseAnimator = ObjectAnimator
            .ofFloat(sunView, "alpha", 0.5f, 1f)
            .setDuration(DURATION/20)
        pulseAnimator.interpolator = AccelerateInterpolator()
        pulseAnimator.repeatCount = ObjectAnimator.INFINITE
        pulseAnimator.repeatMode = ObjectAnimator.REVERSE

        val heatAnimator = ObjectAnimator.ofPropertyValuesHolder(
            sunView,
            PropertyValuesHolder.ofFloat("scaleX", 1.3f)
            , PropertyValuesHolder.ofFloat("scaleY", 1.3f)
        ).setDuration(600)
        heatAnimator.repeatCount = ObjectAnimator.INFINITE
        heatAnimator.repeatMode = ObjectAnimator.REVERSE
        heatAnimator.interpolator = AccelerateDecelerateInterpolator()

        val animatorSet = AnimatorSet()
        animatorSet.play(pulseAnimator)
            .with(heatAnimator)
        animatorSet.start()
    }

    /***
     * @param goingUp: determines if the sun is going up (sunrise) or going down (sunset)
     */
    private fun sunAnimation(goingUp: Boolean) {
        val y0 = sunView.top.toFloat()                  // Sun's beginning position
        val ys = sunView.y                              // Sun's current position
        val y1 = skyView.height.toFloat()               // Sun's final position
        val ds = y1 - y0                                // Total distance the Sun moves
        val dr = if (goingUp) ys - y0 else y1 - ys      // Distance remaining on the sunrise/sunset
        val duration = (DURATION * dr / ds).toLong()    // Duration of time left based on
        // ======================================================================================
        Toast.makeText(applicationContext, "duration = $duration", Toast.LENGTH_LONG).show()
        val heightAnimator = ObjectAnimator
            .ofFloat(sunView, "y", ys, if (goingUp) y0 else y1)
            .setDuration(duration)
        heightAnimator.interpolator = AccelerateInterpolator()

        val skyStartColor = if (goingUp) sunsetSkyColor else blueSkyColor
        val skyEndColor = if (goingUp) blueSkyColor else sunsetSkyColor
        val sunsetSkyAnimator = ObjectAnimator
            .ofInt(skyView, "backgroundColor", skyStartColor, skyEndColor)
            .setDuration(duration)
        sunsetSkyAnimator.setEvaluator(ArgbEvaluator())

        val nightStartColor = if (goingUp) nightSkyColor else sunsetSkyColor
        val nightEndColor = if (goingUp) sunsetSkyColor else nightSkyColor
        val nightSkyAnimator = ObjectAnimator
            .ofInt(nightSkyColor, "backgroundColor", nightStartColor, nightEndColor)
            .setDuration(duration / 2)
        nightSkyAnimator.setEvaluator(ArgbEvaluator())

        val animatorSet = AnimatorSet()
        animatorSet.play(heightAnimator)
            .with(sunsetSkyAnimator)
            .before(nightSkyAnimator)
        animatorSet.start()
    }
}