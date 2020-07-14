package sst.com.anouncements

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create arguments for the starting fragment
        val fragmentArgs = Bundle()
        fragmentArgs.putString("feedUrl",  getString(R.string.blog_rss_url))
        // Apparently findNavController can't be used. See https://stackoverflow.com/a/59275182/4428725
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navHostFragment.navController
            .setGraph(R.navigation.nav_graph, fragmentArgs)
    }
}
