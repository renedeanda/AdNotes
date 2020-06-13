package com.dcrtns.adnotebook.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.dcrtns.adnotebook.R
import com.dcrtns.adnotebook.database.AdNoteDatasource
import com.dcrtns.adnotebook.models.AdNote
import com.dcrtns.adnotebook.ui.activities.AdNoteActivity
import com.dcrtns.adnotebook.util.DialogUtil
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.adnote_list_item.*
import java.text.SimpleDateFormat
import java.util.*

class AdNoteAdapter(private val context: Context, private val mAdNotes: ArrayList<AdNote>) : RecyclerView.Adapter<AdNoteAdapter.AdNoteViewHolder>() {

    inner class AdNoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener, LayoutContainer {

        override val containerView: View?
            get() = itemView

        init {
            shareButton.setOnClickListener(this)
            editButton!!.setOnClickListener(this)
            moreButton!!.setOnClickListener(this)
        }

        override fun onClick(v: View) {

            val adNote = mAdNotes[adapterPosition]

            if (v == shareButton) {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.check_out_this_ad))
                shareIntent.putExtra(Intent.EXTRA_TEXT,
                        "Headline 1: " + adNote.headline1
                                + "\nHeadline 2: " + adNote.headline2
                                + "\nHeadline 3: " + adNote.headline3
                                + "\nDescription 1: " + adNote.description
                                + "\nDescription 2: " + adNote.description2
                                + "\nMainURL: " + adNote.mainUrl
                                + "\nPath 1: " + adNote.urlPath1
                                + "\nPath 2: " + adNote.urlPath2
                                + "\nCampaign: " + adNote.campaignName
                                + "\nAd Group: " + adNote.adGroupName
                                + "\nExtra notes: " + adNote.extraNotes)
                shareIntent.type = "text/plain"
                context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_via)))
            }

            if (v == editButton) {
                context.startActivity(AdNoteActivity.createIntent(context, adNote))
            }

            if (v == moreButton) {
                showPopupMenu(moreButton, adNote.id, adNote)
            }
        }


        override fun onLongClick(v: View): Boolean {
            return false
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdNoteViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.adnote_list_item, parent, false)
        return AdNoteViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: AdNoteViewHolder, position: Int) {
        val adNote = mAdNotes[position]

        val date = java.lang.Long.valueOf(adNote.adUpdateDate!!)
        val formatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        val convertedDate = formatter.format(date)
        holder.dateLabel!!.text = "Last updated: $convertedDate"

        if (adNote.headline1!!.isNotEmpty() && adNote.headline2!!.isEmpty() && adNote.headline3.isNullOrEmpty()) {
            holder.headlineText!!.text = adNote.headline1
        }
        if (adNote.headline1!!.isEmpty() && adNote.headline2!!.isNotEmpty() && adNote.headline3.isNullOrEmpty()) {
            holder.headlineText!!.text = adNote.headline2
        }
        if (adNote.headline1!!.isEmpty() && adNote.headline2!!.isEmpty() && !adNote.headline3.isNullOrEmpty()) {
            holder.headlineText!!.text = adNote.headline3
        }
        if (adNote.headline1!!.isNotEmpty() && adNote.headline2!!.isNotEmpty() && adNote.headline3.isNullOrEmpty()) {
            holder.headlineText!!.text = adNote.headline1 + " | " + adNote.headline2
        }
        if (adNote.headline1!!.isNotEmpty() && adNote.headline2!!.isEmpty() && !adNote.headline3.isNullOrEmpty()) {
            holder.headlineText!!.text = adNote.headline1 + " | " + adNote.headline3
        }
        if (adNote.headline1!!.isEmpty() && adNote.headline2!!.isNotEmpty() && !adNote.headline3.isNullOrEmpty()) {
            holder.headlineText!!.text = adNote.headline2 + " | " + adNote.headline3
        }
        if (adNote.headline1!!.isNotEmpty() && adNote.headline2!!.isNotEmpty() && !adNote.headline3.isNullOrEmpty()) {
            holder.headlineText!!.text = adNote.headline1 + " | " + adNote.headline2 + " | " + adNote.headline3
        }


        if (adNote.mainUrl!!.isNotEmpty() && adNote.urlPath1!!.isNotEmpty() && adNote.urlPath2!!.isNotEmpty()) {
            holder.urlText!!.text = adNote.mainUrl + "/" + adNote.urlPath1 + "/" + adNote.urlPath2
        }
        if (adNote.mainUrl!!.isNotEmpty() && adNote.urlPath1!!.isNotEmpty() && adNote.urlPath2!!.isEmpty()) {
            holder.urlText!!.text = adNote.mainUrl + "/" + adNote.urlPath1
        }
        if (adNote.mainUrl!!.isNotEmpty() && adNote.urlPath2!!.isNotEmpty() && adNote.urlPath1!!.isEmpty()) {
            holder.urlText!!.text = adNote.mainUrl + "/" + adNote.urlPath2
        }
        if (adNote.mainUrl!!.isNotEmpty() && adNote.urlPath1!!.isEmpty() && adNote.urlPath2!!.isEmpty()) {
            holder.urlText!!.text = adNote.mainUrl
        }
        if (adNote.mainUrl!!.isEmpty() && adNote.urlPath1!!.isEmpty() && adNote.urlPath2!!.isEmpty()) {
            holder.urlText!!.text = "www.example.com"
        }
        if (adNote.mainUrl!!.isEmpty() && adNote.urlPath1!!.isNotEmpty() && adNote.urlPath2!!.isNotEmpty()) {
            holder.urlText!!.text = "www.example.com/" + adNote.urlPath1 + "/" + adNote.urlPath2
        }
        if (adNote.mainUrl!!.isEmpty() && adNote.urlPath1!!.isNotEmpty() && adNote.urlPath2!!.isEmpty()) {
            holder.urlText!!.text = "www.example.com/" + adNote.urlPath1!!
        }
        if (adNote.mainUrl!!.isEmpty() && adNote.urlPath2!!.isNotEmpty() && adNote.urlPath1!!.isEmpty()) {
            holder.urlText!!.text = "www.example.com/" + adNote.urlPath2!!
        }

        if (adNote.description!!.isNotEmpty() && adNote.description2.isNullOrEmpty()) {
            holder.descriptionText!!.text = adNote.description
        }
        if (adNote.description!!.isEmpty() && !adNote.description2.isNullOrEmpty()) {
            holder.descriptionText!!.text = adNote.description2
        }
        if (adNote.description!!.isNotEmpty() && !adNote.description2.isNullOrEmpty()) {
            holder.descriptionText!!.text = adNote.description + " " + adNote.description2
        }
    }

    private fun showPopupMenu(buttonView: View?, id: Int, adNote: AdNote) {
        val popup = PopupMenu(buttonView!!.context, buttonView)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.menu_adnote_card, popup.menu)
        popup.setOnMenuItemClickListener(MyMenuItemClickListener(id, adNote))
        popup.show()
    }

    internal inner class MyMenuItemClickListener(private val id: Int, private val adNote: AdNote) : PopupMenu.OnMenuItemClickListener {

        override fun onMenuItemClick(menuItem: MenuItem): Boolean {
            when (menuItem.itemId) {
                R.id.action_delete -> {
                    deleteEntry(id)
                    return true
                }
                R.id.action_details -> {
                    showAdDetails(adNote)
                    return true
                }
                else -> {
                }
            }
            return false
        }
    }

    //TODO refactor to be interface & called in MainActivity
    private fun deleteEntry(id: Int) {
        val datasource = AdNoteDatasource(context as Activity)

        AlertDialog.Builder(context)
                .setTitle(R.string.delete_adnote)
                .setMessage(R.string.are_you_sure_delete_ad)
                .setPositiveButton(R.string.ok) { _, _ ->
                    datasource.delete(id)
                    context.recreate()
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
    }

    private fun showAdDetails(adNote: AdNote) {

        val campaign: String? = if (adNote.campaignName!!.isNotEmpty()) {
            adNote.campaignName
        } else {
            context.getString(R.string.none)
        }
        val adGroup: String? = if (adNote.adGroupName!!.isNotEmpty()) {
            adNote.adGroupName
        } else {
            context.getString(R.string.none)
        }
        val extraNotes: String? = if (adNote.extraNotes!!.isNotEmpty()) {
            adNote.extraNotes
        } else {
            context.getString(R.string.none)
        }

        DialogUtil.showInfoDialog(context,
                context.getString(R.string.ad_details),
                "Campaign: $campaign\n\nAdGroup: $adGroup\n\nNotes: $extraNotes")
    }

    override fun getItemCount(): Int {
        return mAdNotes.size
    }
}
