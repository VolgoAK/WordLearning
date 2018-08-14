package xyz.volgoak.wordlearning.adapter

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.util.DiffUtil
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.analytics.FirebaseAnalytics
import com.squareup.picasso.Picasso
import xyz.volgoak.wordlearning.R
import xyz.volgoak.wordlearning.data.DatabaseContract
import xyz.volgoak.wordlearning.data.StorageContract
import xyz.volgoak.wordlearning.entities.DataEntity
import xyz.volgoak.wordlearning.entities.Set
import java.io.File

/**
 * Created by Volgoak on 18.08.2017.
 */

class SetsRecyclerAdapter(entityList: MutableList<Set>, recyclerView: RecyclerView)
    : RecyclerAdapter<SetsRecyclerAdapter.SetsRowController, Set>(entityList, recyclerView) {

    private val checkedColor by lazy { ContextCompat.getColor(recyclerView.context, R.color.colorAccent) }
    private val uncheckedColor by lazy { ContextCompat.getColor(recyclerView.context, R.color.semi_transparent_white) }

    var setStatusChanger: (Set) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetsRowController {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sets_cursor_adapter, parent, false)
        return SetsRowController(view)
    }

    /*public void downloadImage(String image) {
        WordsApp.getsComponent().inject(this);
        downloader.loadImage(image);
    }*/

    inner class SetsRowController(mRoot: View) : BaseHolder(mRoot) {

        private val mCardRoot: CardView = mRoot as CardView

        private val setNameTv: TextView = mRoot.findViewById(R.id.tv_name_setsadapter)
        private val setDescriptionTv: TextView = mRoot.findViewById(R.id.tv_description_setsadapter)
        private val addButton: ImageButton = mRoot.findViewById(R.id.ibt_add_sets)
        private val civ: ImageView = mRoot.findViewById(R.id.civ_sets)

        init {
            mRoot.setOnClickListener {
                onControllerClick(this, mRoot, adapterPosition)
            }
        }

        override fun bindController(dataEntity: DataEntity) {
            val set = dataEntity as Set
            setNameTv.text = set.name
            setDescriptionTv.text = set.description

            val drawableId = if (set.status == DatabaseContract.Sets.IN_DICTIONARY)
                R.drawable.ic_added_green_50dp
            else
                R.drawable.ic_add_blue_50dp
            addButton.setImageResource(drawableId)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                civ.transitionName = dataEntity.name
            }

            addButton.setOnClickListener {
                setStatusChanger(set)
            }

            val imageUrl = set.imageUrl
            val imagesDir = File(itemView.context.filesDir, StorageContract.IMAGES_FOLDER)
            val imageFile = File(imagesDir, imageUrl)
            val imageUri = Uri.fromFile(imageFile)

            if (imageFile.exists()) {
                Picasso.get().load(imageUri)
                        .error(R.drawable.button_back)
                        .into(civ)
            } else {
                // TODO: 1/31/18 load new image
                val failEvent = Bundle()
                failEvent.putString("image", set.imageUrl)
                FirebaseAnalytics.getInstance(itemView.context).logEvent("Image_fail", failEvent)

                //            ((SetsRecyclerAdapter) mAdapter).downloadImage(set.getImageUrl());

                Glide.with(itemView.context).load(R.drawable.button_back).into(civ)
            }
        }

        override fun setChecked(checked: Boolean) {
            val backgroundColor = if (checked) checkedColor else uncheckedColor
            mCardRoot.setCardBackgroundColor(backgroundColor)
        }
    }
}
