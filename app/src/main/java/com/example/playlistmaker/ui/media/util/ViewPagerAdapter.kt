package com.example.playlistmaker.ui.media.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmaker.ui.media.activity.FavoriteTracksFragment
import com.example.playlistmaker.ui.media.activity.PlaylistsFragment

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) FavoriteTracksFragment.newInstance() else PlaylistsFragment.newInstance()
    }
}