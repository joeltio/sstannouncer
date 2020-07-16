package sst.com.anouncements.feed.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_post.*
import sst.com.anouncements.R
import sst.com.anouncements.feed.model.Entry

class PostFragment : Fragment() {
    private lateinit var entry: Entry
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        entry = requireArguments().getParcelable("post")!!
        return layoutInflater.inflate(R.layout.fragment_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Add content to the fragment
        title_text_view.text = entry.title
        author_text_view.text = entry.authorName
        date_text_view.text = entry.relativePublishedDate

        // Add custom CSS and JS to make images fit within the window better
        val customHTML = """
            <style>
                img {
                    display: inline;
                    height: auto;
                    max-width: 100%;
                }
            </style>
            <script>
                document.addEventListener('DOMContentLoaded', function(event) {
                    var imgs = document.getElementsByTagName('img');
                    for (var i=0; i<imgs.length; i++) {
                        console.log(imgs[i].parentElement.style.marginLeft);
                        imgs[i].parentElement.style.marginLeft = '0px';
                    }
                });
            </script>
        """.trimIndent()
        // loadData cannot be used here as it is breaks when special characters are not escaped in
        // the content. See https://stackoverflow.com/a/11926466/4428725
        content_web_view.loadDataWithBaseURL(null, customHTML + entry.content, "text/html; charset=utf-8", "UTF-8", null)

        // It is acceptable to enable JavaScript here as the source is deemed trustworthy
        // If not, anyone viewing the blog will also get XSSed
        @SuppressLint("SetJavaScriptEnabled")
        content_web_view.settings.javaScriptEnabled = true
        content_web_view.settings.loadsImagesAutomatically = true
    }
}