package com.dcrtns.adnotebook.ui.activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.dcrtns.adnotebook.R
import com.github.paolorotolo.appintro.AppIntro2
import com.github.paolorotolo.appintro.AppIntroFragment
import com.github.paolorotolo.appintro.model.SliderPage

class IntroActivity : AppIntro2() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Instead of fragments, you can also use our default slide.
        // Just create a `SliderPage` and provide title, description, background and image.
        // AppIntro will do the rest.
        val sliderPage = SliderPage()
        sliderPage.title = getString(R.string.slider_intro_title_one)
        sliderPage.description = getString(R.string.slider_intro_desc_one)
        sliderPage.imageDrawable = R.drawable.intro_one
        sliderPage.bgColor = getColor(R.color.black)
        addSlide(AppIntroFragment.newInstance(sliderPage))

        val sliderPage2 = SliderPage()
        sliderPage2.title = getString(R.string.slider_intro_title_two)
        sliderPage2.description = getString(R.string.slider_intro_desc_two)
        sliderPage2.imageDrawable = R.drawable.intro_two
        sliderPage2.bgColor = getColor(R.color.black)
        addSlide(AppIntroFragment.newInstance(sliderPage2))

        val sliderPage3 = SliderPage()
        sliderPage3.title = getString(R.string.slider_intro_title_three)
        sliderPage3.description = getString(R.string.slider_intro_desc_three)
        sliderPage3.imageDrawable = R.drawable.intro_three
        sliderPage3.bgColor = getColor(R.color.black)
        addSlide(AppIntroFragment.newInstance(sliderPage3))

        // Hide Skip/Done button.
        showSkipButton(false)
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        // Do something when users tap on Skip button.
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        // Do something when users tap on Done button.
        finish()
    }

}
