import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.arkaasignment.model.Data
import com.app.arkaasignment.R
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ImageAdapter(
    var context: Context,
    private var images: ArrayList<Data>,
    private val listLayout: Boolean
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>(),
    Filterable {

    var filterList = ArrayList<Data>()

    init {
        filterList = images
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val layout = if (listLayout) {
            R.layout.item_image_list
        } else {
            R.layout.item_image_grid
        }
        val view = LayoutInflater.from(context).inflate(layout, parent, false)
        return ImageViewHolder(view)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        print("size of list $images")
        val image = filterList[position]
        "Title: ${image.title}".also { holder.titleTextView.text = it }

        // unix epoch time to human readable date time
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy hh.mm aa", Locale.ENGLISH)
        fun getDateString(time: Long) : String = simpleDateFormat.format(time * 1000L)
        val dateString = getDateString(image.datetime.toLong())
        ("Date: $dateString").also { holder.dateTextView.text = it }

        // load network image using glide library
        if (image.images != null) {
            "Count: ${image.images.size}".also { holder.imageCountTextView.text = it }
            Glide.with(
                holder.itemView
            ).load(image.images[0].link).centerCrop().placeholder(R.drawable.loading_image)
                .into(holder.imageView)
        } else {
            holder.imageCountTextView.text = context.getString(R.string.no_image_txt)
            Glide.with(
                holder.itemView
            ).load(R.drawable.loading_image).centerCrop().placeholder(R.drawable.loading_image)
                .into(holder.imageView)
        }
    }

    override fun getItemCount() = filterList.size
    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.title)
        val dateTextView: TextView = itemView.findViewById(R.id.date)
        val imageCountTextView: TextView = itemView.findViewById(R.id.count)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                filterList = if (charSearch.isEmpty()) {
                    images
                } else {
                    val resultList = ArrayList<Data>()
                    for (row in images) {
                        if (row.title.lowercase(Locale.ROOT)
                                .contains(charSearch.lowercase(Locale.ROOT))
                        ) {
                            resultList.add(row)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = filterList
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged")
            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filterList = results?.values as ArrayList<Data>
                notifyDataSetChanged()
            }

        }
    }

}


