package khan.sajad.example.happyplaces.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import khan.sajad.example.happyplaces.R
import khan.sajad.example.happyplaces.database.HappyPlaceEntity
import khan.sajad.example.happyplaces.databinding.HappyPlacesListBinding


class ItemAdapter(private val dataset: ArrayList<HappyPlaceEntity>): RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {
    // Long clickListener variable
    private var onLongClickListener: OnLongClickListener? = null

    class ItemViewHolder(binding: HappyPlacesListBinding): RecyclerView.ViewHolder(binding.root){
        val ivPhoto = binding.ivRecyclerview
        val tvTitle = binding.tvTitle
        val tvDate = binding.tvDate
        val tvDescription = binding.tvDescription
        val tvLocation = binding.tvPlaceLocation

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(HappyPlacesListBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val oneHappyPlace = dataset[position]
        try {
            // Using Glide to load image from internal storage into imageview
            Glide.with(holder.itemView.context).load(oneHappyPlace.imageLocation).into(holder.ivPhoto)
        }catch (e: java.lang.Exception){
            e.printStackTrace()
            holder.ivPhoto.setImageResource(R.drawable.ic_image_not_found)
        }
        holder.tvDate.text = oneHappyPlace.date
        holder.tvTitle.text = oneHappyPlace.title
        holder.tvDescription.text = oneHappyPlace.description
        holder.tvLocation.text = oneHappyPlace.location

        holder.itemView.setOnLongClickListener {
            if(onLongClickListener != null){
                onLongClickListener!!.onLongClick(position, oneHappyPlace)
            }
            return@setOnLongClickListener true
        }
    }
    override fun getItemCount(): Int {
        return dataset.size
    }

    //Setting OnLongClickListener
    fun setOnLongClickListener(onLongClickListener: OnLongClickListener){
        this.onLongClickListener = onLongClickListener
    }

    // On Long ClickListener interface
    interface OnLongClickListener{
        fun onLongClick(position: Int, model: HappyPlaceEntity): Boolean
    }
}


