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

import static de.mrapp.android.preference.util.Condition.ensureNotNull;

import java.util.LinkedHashSet;
import java.util.Set;

import android.app.ListFragment;
import android.os.Bundle;
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

	/**
	 * A set, which contains the listeners, which should be notified on events
	 * concerning the fragment.
	 */
	private Set<FragmentListener> listeners = new LinkedHashSet<>();

	/**
	 * Notifies all registered listeners, that the fragment has been created.
	 */
	private void notifyFragmentCreated() {
		for (FragmentListener listener : listeners) {
			listener.onFragmentCreated(this);
		}
	}

	/**
	 * Adds a new listener, which should be notified on events concerning the
	 * fragment.
	 * 
	 * @param listener
	 *            The listener, which should be added, as an instance of the
	 *            type {@link FragmentListener}. The listener may not be null
	 */
	public final void addFragmentListener(final FragmentListener listener) {
		ensureNotNull(listener, "The listener may not be null");
		listeners.add(listener);
	}

	/**
	 * Removes a specific listener, which should not be notified on events
	 * concerning the fragment, anymore.
	 * 
	 * @param listener
	 *            The listener, which should be removed, as an instance of the
	 *            type {@link FragmentListener}. The listener may not be null
	 */
	public final void removeFragmentListener(final FragmentListener listener) {
		ensureNotNull(listener, "The listener may not be null");
		listeners.remove(listener);
	}

	@Override
	public final void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		preferenceHeaderAdapter = new PreferenceHeaderAdapter(getActivity());
		setListAdapter(preferenceHeaderAdapter);
		notifyFragmentCreated();
	}

	@Override
	public final PreferenceHeaderAdapter getListAdapter() {
		return preferenceHeaderAdapter;
	}

}