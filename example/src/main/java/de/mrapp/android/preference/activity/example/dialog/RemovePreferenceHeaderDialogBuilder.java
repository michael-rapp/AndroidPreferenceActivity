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
package de.mrapp.android.preference.activity.example.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.mrapp.android.preference.activity.PreferenceActivity;
import de.mrapp.android.preference.activity.PreferenceHeader;
import de.mrapp.android.preference.activity.example.R;

/**
 * A builder, which allows to create a dialog, which allows to remove a specific preference header
 * from a {@link PreferenceActivity}.
 *
 * @author Michael Rapp
 */
public class RemovePreferenceHeaderDialogBuilder extends AlertDialog.Builder {

    /**
     * The listener, which should be notified, when the user closes the dialog confirmatively.
     */
    private RemovePreferenceHeaderDialogListener listener;

    /**
     * The spinner, which allows to choose the preference header, which should be removed.
     */
    private Spinner spinner;

    /**
     * Inflates the layout of the dialog.
     *
     * @param activity
     *         The activity, the dialog should belong to, as an instance of the class {@link
     *         Activity}
     * @return The view, which has been inflated, as an instance of the class {@link View}
     */
    @SuppressLint("InflateParams")
    private View inflateLayout(final Activity activity) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.remove_preference_header_dialog, null);
        setView(view);
        return view;
    }

    /**
     * Initializes the spinner, which allows to choose the preference header, which should be
     * removed.
     *
     * @param parentView
     *         The parent view of the spinner as an instance of the class {@link View}
     * @param preferenceHeaders
     *         A collection, which contains the preference headers, the spinner should allow to
     *         choose from, as an instance of the type {@link Collection}
     */
    private void initializeSpinner(final View parentView,
                                   final Collection<PreferenceHeader> preferenceHeaders) {
        spinner = (Spinner) parentView.findViewById(R.id.remove_preference_header_spinner);
        List<CharSequence> items = getPreferenceHeaderTitles(preferenceHeaders);
        ArrayAdapter<CharSequence> adapter =
                new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    /**
     * Returns a list, which contains the titles of all preference headers, which are contained by a
     * specific collection.
     *
     * @param preferenceHeaders
     *         A collection, which contains the preference headers, whose title should be returned,
     *         as an instance of the type {@link Collection}
     * @return A list, which contains the title of the given preference headers, as an instance of
     * the type {@link List}
     */
    private List<CharSequence> getPreferenceHeaderTitles(
            final Collection<PreferenceHeader> preferenceHeaders) {
        List<CharSequence> titles = new LinkedList<>();

        for (PreferenceHeader preferenceHeader : preferenceHeaders) {
            titles.add(preferenceHeader.getTitle());
        }

        return titles;
    }

    /**
     * Initializes the dialog's title and message.
     */
    private void initializeTitleAndMessage() {
        setTitle(R.string.remove_preference_header_dialog_title);
        setMessage(R.string.remove_preference_header_dialog_message);
    }

    /**
     * Initializes the dialog's buttons.
     */
    private void initializeButtons() {
        setPositiveButton(android.R.string.ok, createRemovePreferenceHeaderClickListener());
        setNegativeButton(android.R.string.cancel, null);
    }

    /**
     * Creates and returns a listener, which allows to notify the registered listener, when the user
     * closes the dialog confirmatively.
     *
     * @return The listener, which has been created, as an instance of the type {@link
     * OnClickListener}
     */
    private OnClickListener createRemovePreferenceHeaderClickListener() {
        return new OnClickListener() {

            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                int position = spinner.getSelectedItemPosition();
                listener.onRemovePreferenceHeader(position);
            }

        };
    }

    /**
     * Creates a new builder, which allows to create a dialog, which allows to remove a specific
     * preference header from a {@link PreferenceActivity}.
     *
     * @param activity
     *         The activity, the dialog should belong to, as an instance of the class {@link
     *         Activity}
     * @param preferenceHeaders
     *         A collection, which contains the preference headers, the dialog should allow to
     *         choose from, as an instance of the type {@link Collection}
     * @param listener
     *         The listener, which should be notified when the user closes the dialog
     *         confirmatively, as an instance of the type {@link RemovePreferenceHeaderDialogListener}
     */
    public RemovePreferenceHeaderDialogBuilder(final Activity activity,
                                               final Collection<PreferenceHeader> preferenceHeaders,
                                               final RemovePreferenceHeaderDialogListener listener) {
        super(activity);
        this.listener = listener;
        View view = inflateLayout(activity);
        initializeSpinner(view, preferenceHeaders);
        initializeTitleAndMessage();
        initializeButtons();
    }

}