/*
 * AndroidPreferenceActivity Copyright 2014 - 2016 Michael Rapp
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package de.mrapp.android.preference.activity;

/**
 * An implementation of the abstract class {@link android.database.DataSetObserver}, which allows to
 * check, whether the method <code>onChanged():void</code> or <code>onInvalidated():void</code> has
 * been invoked by an observed class, or not.
 *
 * @author Michael Rapp
 */
public class DataSetObserver extends android.database.DataSetObserver {

    /**
     * True, if the method <code>onChanged():void</code> has been invoked at least once, false
     * otherwise.
     */
    private boolean onChanged;

    /**
     * True, if the method <code>onInvalidated():void</code> has been invoked at least once, false
     * otherwise.
     */
    private boolean onInvalidated;

    /**
     * Returns, whether the method <code>onChanged():void</code> has been invoked at least once, or
     * not.
     *
     * @return True, if the method <code>onChanged():void</code> has been invoked at least once,
     * false otherwise
     */
    public final boolean hasOnChangedBeenCalled() {
        return onChanged;
    }

    /**
     * Returns, whether the method <code>onInvalidated():void</code> has been invoked at least once,
     * or not.
     *
     * @return True, if the method <code>onInvalidated():void</code> has been invoked at least once,
     * false otherwise
     */
    public final boolean hasOnInvalidatedBeenCalled() {
        return onInvalidated;
    }

    /**
     * Resets the data set observer.
     */
    public final void reset() {
        onChanged = false;
        onInvalidated = false;
    }

    @Override
    public final void onChanged() {
        super.onChanged();
        onChanged = true;
    }

    @Override
    public final void onInvalidated() {
        super.onInvalidated();
        onInvalidated = true;
    }

}