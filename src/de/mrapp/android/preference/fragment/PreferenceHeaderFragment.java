/*
 * AndroidPreferenceActivity Copyright 2014 Michael Rapp
 *
 * This program is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU Lesser General Public License as published 
 * by the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU 
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/>. 
 */
package de.mrapp.android.preference.fragment;

import android.app.ListFragment;
import android.os.Bundle;
import android.widget.ListView;
import de.mrapp.android.preference.adapter.PreferenceHeaderAdapter;

/**
 * A list fragment, which shows multiple preference headers and provides
 * navigation to each header's fragment.
 * 
 * @author Michael Rapp
 * 
 * @since 1.0.0
 */
public class PreferenceHeaderFragment extends ListFragment {

	/**
	 * The adapter, which provides the preference headers for visualization
	 * using the fragment's list view.
	 */
	private PreferenceHeaderAdapter preferenceHeaderAdapter;

	@Override
	public final void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		preferenceHeaderAdapter = new PreferenceHeaderAdapter(getActivity());
		setListAdapter(preferenceHeaderAdapter);
	}

	@Override
	public final PreferenceHeaderAdapter getListAdapter() {
		return preferenceHeaderAdapter;
	}

}