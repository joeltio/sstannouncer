package sst.com.anouncements

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import sst.com.anouncements.feed.FeedFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainFragment = FeedFragment()
        val fragmentArgs = Bundle()
        fragmentArgs.putString("feedUrl",  getString(R.string.blog_rss_url))
        mainFragment.arguments = fragmentArgs

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment, mainFragment, mainFragment.tag)
            .commit()

        setContentView(R.layout.activity_main)
    }
}
