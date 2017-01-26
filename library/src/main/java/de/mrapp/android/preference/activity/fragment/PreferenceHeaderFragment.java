/*
 * Copyright 2014 - 2017 Michael Rapp
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package de.mrapp.android.preference.activity.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.LinkedHashSet;
import java.util.Set;

import de.mrapp.android.preference.activity.R;
import de.mrapp.android.preference.activity.adapter.PreferenceHeaderAdapter;

import static de.mrapp.android.util.Condition.ensureNotNull;

/**
 * A fragment, which shows multiple preference headers and provides navigation to each header's
 * fragment.
 *
 * @author Michael Rapp
 * @since 1.0.0
 */
public class PreferenceHeaderFragment extends Fragment {

    /**
     * The list view, which is used to show the preference headers.
     */
    private ListView listView;

    /**
     * The adapter, which provides the preference headers for visualization using the fragment's
     * list view.
     */
    private PreferenceHeaderAdapter adapter;

    /**
     * A set, which contains the listeners, which should be notified on events concerning the
     * fragment.
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
     * Adds a new listener, which should be notified on events concerning the fragment.
     *
     * @param listener
     *         The listener, which should be added, as an instance of the type {@link
     *         FragmentListener}. The listener may not be null
     */
    public final void addFragmentListener(@NonNull final FragmentListener listener) {
        ensureNotNull(listener, "The listener may not be null");
        listeners.add(listener);
    }

    /**
     * Removes a specific listener, which should not be notified on events concerning the fragment,
     * anymore.
     *
     * @param listener
     *         The listener, which should be removed, as an instance of the type {@link
     *         FragmentListener}. The listener may not be null
     */
    public final void removeFragmentListener(@NonNull final FragmentListener listener) {
        ensureNotNull(listener, "The listener may not be null");
        listeners.remove(listener);
    }

    /**
     * Returns the list view, which is used to show the preference headers.
     *
     * @return The list view, which is used to show the preference headers, as an instance of the
     * class {@link ListView}
     */
    public final ListView getListView() {
        return listView;
    }

    /**
     * Returns the adapter, which provides the preference headers for visualization using the
     * fragment's list view.
     *
     * @return The adapter, which provides the preference headers for visualization using the
     * fragment's list view, as an instance of the class {@link PreferenceHeaderAdapter}
     */
    public final PreferenceHeaderAdapter getListAdapter() {
        return adapter;
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.preference_header_fragment, container, false);
        listView = (ListView) view.findViewById(android.R.id.list);
        return view;
    }

    @Override
    public final void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new PreferenceHeaderAdapter(getActivity());
        getListView().setAdapter(adapter);
        notifyFragmentCreated();
    }

}