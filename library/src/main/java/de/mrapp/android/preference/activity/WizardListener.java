/*
 * AndroidPreferenceActivity Copyright 2014 - 2015 Michael Rapp
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
package de.mrapp.android.preference.activity;

import android.app.Fragment;
import android.os.Bundle;

/**
 * Defines the interface a class, which should be notified when the user
 * navigates within a {@link PreferenceActivity}, which is used as wizard, by
 * using its next-, back- and finish-button. The return values of the
 * interface's methods allow to take influence on the navigation, e.g. if the
 * currently shown preferences should be validated.
 * 
 * @author Michael Rapp
 *
 * @since 1.0.0
 */
public interface WizardListener {

	/**
	 * The method, which is invoked, when the user wants to navigate to the next
	 * step of the wizard.
	 * 
	 * @param position
	 *            The position of the currently selected preference header as an
	 *            {@link Integer} value
	 * @param preferenceHeader
	 *            The currently selected preference header as an instance of the
	 *            class {@link PreferenceHeader}
	 * @param fragment
	 *            The currently shown fragment as an instance of the class
	 *            {@link Fragment}
	 * @param bundle
	 *            A bundle, which contains the parameters, which have been
	 *            passed to the currently shown fragment or null, if no
	 *            parameters have been passed to the fragment
	 * @return The bundle, which should be passed to the next fragment, as an
	 *         instance of the class {@link Bundle} or null, if navigating to
	 *         the next step of the wizard should not be allowed
	 */
	Bundle onNextStep(int position, PreferenceHeader preferenceHeader,
			Fragment fragment, final Bundle bundle);

	/**
	 * The method, which is invoked, when the user wants to navigate to the
	 * previous step of the wizard.
	 * 
	 * @param position
	 *            The position of the currently selected preference header as an
	 *            {@link Integer} value
	 * @param preferenceHeader
	 *            The currently selected preference header as an instance of the
	 *            class {@link PreferenceHeader}
	 * @param fragment
	 *            The currently shown fragment as an instance of the class
	 *            {@link Fragment}
	 * @param bundle
	 *            A bundle, which contains the parameters, which have been
	 *            passed to the currently shown fragment or null, if no
	 *            parameters have been passed to the fragment
	 * @return The bundle, which should be passed to the next fragment, as an
	 *         instance of the class {@link Bundle} or null, if navigating to
	 *         the previous step of the wizard should not be allowed
	 */
	Bundle onPreviousStep(int position, PreferenceHeader preferenceHeader,
			Fragment fragment, final Bundle bundle);

	/**
	 * The method, which is invoked, when the user wants to finish the last step
	 * of the wizard.
	 * 
	 * @param position
	 *            The position of the currently selected preference header as an
	 *            {@link Integer} value
	 * @param preferenceHeader
	 *            The currently selected preference header as an instance of the
	 *            class {@link PreferenceHeader}
	 * @param fragment
	 *            The currently shown fragment as an instance of the class
	 *            {@link Fragment}
	 * @param bundle
	 *            A bundle, which contains the parameters, which have been
	 *            passed to the currently shown fragment or null, if no
	 *            parameters have been passed to the fragment
	 * @return True, if finishing the wizard should be allowed, false otherwise
	 */
	boolean onFinish(int position, PreferenceHeader preferenceHeader,
			Fragment fragment, final Bundle bundle);

	/**
	 * The method, which is invoked, when the user wants to skip the wizard.
	 * 
	 * @param position
	 *            The position of the currently selected preference header as an
	 *            {@link Integer} value
	 * @param preferenceHeader
	 *            The currently selected preference header as an instance of the
	 *            class {@link PreferenceHeader}
	 * @param fragment
	 *            The currently shown fragment as an instance of the class
	 *            {@link Fragment}
	 * @param bundle
	 *            A bundle, which contains the parameters, which have been
	 *            passed to the currently shown fragment or null, if no
	 *            parameters have been passed to the fragment
	 * @return True, if skipping the wizard should be allowed, false otherwise
	 */
	boolean onSkip(int position, PreferenceHeader preferenceHeader,
			Fragment fragment, final Bundle bundle);

}