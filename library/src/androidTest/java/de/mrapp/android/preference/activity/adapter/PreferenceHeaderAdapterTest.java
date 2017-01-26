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
package de.mrapp.android.preference.activity.adapter;

import android.support.annotation.NonNull;
import android.test.AndroidTestCase;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import junit.framework.Assert;

import java.util.Collection;
import java.util.LinkedList;

import de.mrapp.android.preference.activity.DataSetObserver;
import de.mrapp.android.preference.activity.PreferenceHeader;
import de.mrapp.android.preference.activity.PreferenceHeaderDecorator;
import de.mrapp.android.preference.activity.R;
import de.mrapp.android.preference.activity.adapter.PreferenceHeaderAdapter.ViewHolder;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Tests the functionality of the class {@link PreferenceHeaderAdapter}.
 *
 * @author Michael Rapp
 */
public class PreferenceHeaderAdapterTest extends AndroidTestCase {

    /**
     * An implementation of the interface {@link PreferenceHeaderDecorator}, which is needed for
     * test purposes.
     */
    private class PreferenceHeaderDecoratorImplementation implements PreferenceHeaderDecorator {

        /**
         * The position, which has been passed, when the decorator was applied the last time.
         */
        private int position;

        /**
         * The preference header, which has been passed, when the decorator was applied the last
         * time.
         */
        private PreferenceHeader preferenceHeader;

        /**
         * The view, which has been passed, when the decorator was applied the last time.
         */
        private View view;

        /**
         * The view holder, which has been passed, when the decorator was applied the last time.
         */
        private ViewHolder viewHolder;

        @Override
        public void onApplyDecorator(final int position, @NonNull final PreferenceHeader preferenceHeader,
                                     @NonNull final View view, @NonNull final ViewHolder viewHolder) {
            this.position = position;
            this.preferenceHeader = preferenceHeader;
            this.view = view;
            this.viewHolder = viewHolder;
        }

    }

    /**
     * Tests, if all properties are set correctly by the constructor.
     */
    public final void testConstructor() {
        PreferenceHeaderAdapter preferenceHeaderAdapter = new PreferenceHeaderAdapter(getContext());
        assertTrue(preferenceHeaderAdapter.isEmpty());
        assertEquals(0, preferenceHeaderAdapter.getCount());
        assertTrue(preferenceHeaderAdapter.getAllItems().isEmpty());
        assertEquals(R.drawable.selector_light, preferenceHeaderAdapter.getSelectorId());
        assertEquals(R.layout.preference_header_item, preferenceHeaderAdapter.getViewId());
        assertTrue(preferenceHeaderAdapter.isEnabled(0));
    }

    /**
     * Tests the functionality of the method, which allows to set the view id.
     */
    public final void testSetViewId() {
        int viewId = R.layout.preference;
        PreferenceHeaderAdapter preferenceHeaderAdapter = new PreferenceHeaderAdapter(getContext());
        DataSetObserver dataSetObserver = new DataSetObserver();
        preferenceHeaderAdapter.registerDataSetObserver(dataSetObserver);
        preferenceHeaderAdapter.setViewId(viewId);
        assertEquals(viewId, preferenceHeaderAdapter.getViewId());
        assertTrue(dataSetObserver.hasOnChangedBeenCalled());
    }

    /**
     * Tests the functionality of the method, which allows to add a listener , which should be
     * notified, when the underlying data of the adapter has been changed.
     */
    public final void testAddListener() {
        AdapterListener adapterListener = mock(AdapterListener.class);
        PreferenceHeader preferenceHeader = new PreferenceHeader("foo");
        PreferenceHeaderAdapter preferenceHeaderAdapter = new PreferenceHeaderAdapter(getContext());
        preferenceHeaderAdapter.addListener(adapterListener);
        preferenceHeaderAdapter.addListener(adapterListener);
        preferenceHeaderAdapter.addItem(preferenceHeader);
        verify(adapterListener, times(1))
                .onPreferenceHeaderAdded(preferenceHeaderAdapter, preferenceHeader, 0);
    }

    /**
     * Ensures, that a {@link NullPointerException} is thrown by the method, which allows to add a
     * listener , which should be notified, when the underlying data of the adapter has been
     * changed, if the listener is null.
     */
    public final void testAddListenerThrowsException() {
        try {
            PreferenceHeaderAdapter preferenceHeaderAdapter =
                    new PreferenceHeaderAdapter(getContext());
            preferenceHeaderAdapter.addListener(null);
            Assert.fail();
        } catch (NullPointerException e) {
            return;
        }
    }

    /**
     * Tests the functionality of the method, which allows to remove a listener , which should not
     * be notified, when the underlying data of the adapter has been changed, anymore.
     */
    public final void testRemoveListener() {
        AdapterListener adapterListener = mock(AdapterListener.class);
        PreferenceHeader preferenceHeader = new PreferenceHeader("foo");
        PreferenceHeaderAdapter preferenceHeaderAdapter = new PreferenceHeaderAdapter(getContext());
        preferenceHeaderAdapter.addListener(adapterListener);
        preferenceHeaderAdapter.removeListener(adapterListener);
        preferenceHeaderAdapter.removeListener(adapterListener);
        preferenceHeaderAdapter.addItem(preferenceHeader);
        verify(adapterListener, times(0))
                .onPreferenceHeaderAdded(preferenceHeaderAdapter, preferenceHeader, 0);
    }

    /**
     * Ensures, that a {@link NullPointerException} is thrown by the method, which allows to remove
     * a listener , which should not be notified, when the underlying data of the adapter has been
     * changed anymore, if the listener is null.
     */
    public final void testRemoveListenerThrowsException() {
        try {
            PreferenceHeaderAdapter preferenceHeaderAdapter =
                    new PreferenceHeaderAdapter(getContext());
            preferenceHeaderAdapter.removeListener(null);
            Assert.fail();
        } catch (NullPointerException e) {
            return;
        }
    }

    /**
     * Tests the functionality of the method, which allows to add a decorator, which should be
     * applied when an item of the adapter is visualized.
     */
    public final void testAddDecorator() {
        PreferenceHeader preferenceHeader = new PreferenceHeader("foo");
        PreferenceHeaderDecorator decorator = mock(PreferenceHeaderDecorator.class);
        PreferenceHeaderAdapter preferenceHeaderAdapter = new PreferenceHeaderAdapter(getContext());
        preferenceHeaderAdapter.addDecorator(decorator);
        preferenceHeaderAdapter.addDecorator(decorator);
        preferenceHeaderAdapter.addItem(preferenceHeader);
        View view = preferenceHeaderAdapter.getView(0, null, null);
        verify(decorator, times(1))
                .onApplyDecorator(eq(0), eq(preferenceHeader), eq(view), any(ViewHolder.class));
    }

    /**
     * Ensures, that a {@link NullPointerException} is thrown by the method, which allows to add a
     * decorator, which should be applied when an item of the adapter is visualized.
     */
    public final void testAddDecoratorThrowsException() {
        try {
            PreferenceHeaderAdapter preferenceHeaderAdapter =
                    new PreferenceHeaderAdapter(getContext());
            preferenceHeaderAdapter.addDecorator(null);
            Assert.fail();
        } catch (NullPointerException e) {
            return;
        }
    }

    /**
     * Tests the functionality of the method, which allows to remove a decorator, which should not
     * be applied when an item of the adapter is visualized, anymore.
     */
    public final void testRemoveDecorator() {
        PreferenceHeader preferenceHeader = new PreferenceHeader("foo");
        PreferenceHeaderDecorator decorator = mock(PreferenceHeaderDecorator.class);
        PreferenceHeaderAdapter preferenceHeaderAdapter = new PreferenceHeaderAdapter(getContext());
        preferenceHeaderAdapter.addDecorator(decorator);
        preferenceHeaderAdapter.removeDecorator(decorator);
        preferenceHeaderAdapter.removeDecorator(decorator);
        preferenceHeaderAdapter.addItem(preferenceHeader);
        View view = preferenceHeaderAdapter.getView(0, null, null);
        verify(decorator, times(0))
                .onApplyDecorator(eq(0), eq(preferenceHeader), eq(view), any(ViewHolder.class));
    }

    /**
     * Ensures, that a {@link NullPointerException} is thrown by the method, which allows to remove
     * a decorator, which should not be applied anymore, when an item of the adapter is visualized.
     */
    public final void testRemoveDecoratorThrowsException() {
        try {
            PreferenceHeaderAdapter preferenceHeaderAdapter =
                    new PreferenceHeaderAdapter(getContext());
            preferenceHeaderAdapter.removeDecorator(null);
            Assert.fail();
        } catch (NullPointerException e) {
            return;
        }
    }

    /**
     * Tests the functionality of the method, which allows to set the selector id.
     */
    public final void testSetSelectorId() {
        int selectorId = R.drawable.selector_dark;
        PreferenceHeaderAdapter preferenceHeaderAdapter = new PreferenceHeaderAdapter(getContext());
        DataSetObserver dataSetObserver = new DataSetObserver();
        preferenceHeaderAdapter.registerDataSetObserver(dataSetObserver);
        preferenceHeaderAdapter.setSelectorId(selectorId);
        assertEquals(selectorId, preferenceHeaderAdapter.getSelectorId());
        assertTrue(dataSetObserver.hasOnChangedBeenCalled());
    }

    /**
     * Tests the functionality of the method, which allows to add an item.
     */
    public final void testAddItem() {
        PreferenceHeader preferenceHeader = new PreferenceHeader("foo");
        DataSetObserver dataSetObserver = new DataSetObserver();
        AdapterListener adapterListener = mock(AdapterListener.class);
        PreferenceHeaderAdapter preferenceHeaderAdapter = new PreferenceHeaderAdapter(getContext());
        preferenceHeaderAdapter.addListener(adapterListener);
        preferenceHeaderAdapter.registerDataSetObserver(dataSetObserver);
        preferenceHeaderAdapter.addItem(preferenceHeader);
        assertTrue(dataSetObserver.hasOnChangedBeenCalled());
        verify(adapterListener, times(1))
                .onPreferenceHeaderAdded(preferenceHeaderAdapter, preferenceHeader, 0);
        assertFalse(preferenceHeaderAdapter.isEmpty());
        assertEquals(1, preferenceHeaderAdapter.getCount());
        assertEquals(1, preferenceHeaderAdapter.getAllItems().size());
        assertEquals(preferenceHeader, preferenceHeaderAdapter.getItem(0));
    }

    /**
     * Ensures, that a {@link NullPointerException} is thrown by the method, which allows to add an
     * item, if the item is null.
     */
    public final void testAddItemThrowsException() {
        try {
            PreferenceHeaderAdapter preferenceHeaderAdapter =
                    new PreferenceHeaderAdapter(getContext());
            preferenceHeaderAdapter.addItem(null);
            Assert.fail();
        } catch (NullPointerException e) {
            return;
        }
    }

    /**
     * Tests the functionality of the method, which allows to add all items, which are contained by
     * a specific collection.
     */
    public final void testAddAllItems() {
        PreferenceHeader preferenceHeader1 = new PreferenceHeader("foo");
        PreferenceHeader preferenceHeader2 = new PreferenceHeader("foo");
        Collection<PreferenceHeader> collection = new LinkedList<PreferenceHeader>();
        collection.add(preferenceHeader1);
        collection.add(preferenceHeader2);
        DataSetObserver dataSetObserver = new DataSetObserver();
        AdapterListener adapterListener = mock(AdapterListener.class);
        PreferenceHeaderAdapter preferenceHeaderAdapter = new PreferenceHeaderAdapter(getContext());
        preferenceHeaderAdapter.addListener(adapterListener);
        preferenceHeaderAdapter.registerDataSetObserver(dataSetObserver);
        preferenceHeaderAdapter.addAllItems(collection);
        assertTrue(dataSetObserver.hasOnChangedBeenCalled());
        verify(adapterListener, times(1))
                .onPreferenceHeaderAdded(preferenceHeaderAdapter, preferenceHeader1, 0);
        verify(adapterListener, times(1))
                .onPreferenceHeaderAdded(preferenceHeaderAdapter, preferenceHeader2, 1);
        assertFalse(preferenceHeaderAdapter.isEmpty());
        assertEquals(2, preferenceHeaderAdapter.getCount());
        assertEquals(2, preferenceHeaderAdapter.getAllItems().size());
        assertEquals(preferenceHeader1, preferenceHeaderAdapter.getItem(0));
        assertEquals(preferenceHeader2, preferenceHeaderAdapter.getItem(1));
    }

    /**
     * Ensures, that a {@link NullPointerException} is thrown by the method, which allows to add all
     * items, which are contained by a specific collection, if the collection is null.
     */
    public final void testAddAllItemsThrowsException() {
        try {
            PreferenceHeaderAdapter preferenceHeaderAdapter =
                    new PreferenceHeaderAdapter(getContext());
            preferenceHeaderAdapter.addAllItems(null);
            Assert.fail();
        } catch (NullPointerException e) {
            return;
        }
    }

    /**
     * Tests the functionality of the method, which allows to remove a specific item.
     */
    public final void testRemoveItem() {
        PreferenceHeader preferenceHeader = new PreferenceHeader("foo");
        DataSetObserver dataSetObserver = new DataSetObserver();
        AdapterListener adapterListener = mock(AdapterListener.class);
        PreferenceHeaderAdapter preferenceHeaderAdapter = new PreferenceHeaderAdapter(getContext());
        preferenceHeaderAdapter.addItem(preferenceHeader);
        preferenceHeaderAdapter.addListener(adapterListener);
        preferenceHeaderAdapter.registerDataSetObserver(dataSetObserver);
        boolean removed = preferenceHeaderAdapter.removeItem(preferenceHeader);
        assertTrue(removed);
        assertTrue(dataSetObserver.hasOnChangedBeenCalled());
        verify(adapterListener, times(1))
                .onPreferenceHeaderRemoved(preferenceHeaderAdapter, preferenceHeader, 0);
        assertTrue(preferenceHeaderAdapter.isEmpty());
        assertEquals(0, preferenceHeaderAdapter.getCount());
        assertEquals(0, preferenceHeaderAdapter.getAllItems().size());
    }

    /**
     * Tests the functionality of the method, which allows to remove a specific item, if the adapter
     * does not contain the item.
     */
    public final void testRemoveItemWhenAdapterDoesNotContainItem() {
        PreferenceHeader preferenceHeader = new PreferenceHeader("foo");
        DataSetObserver dataSetObserver = new DataSetObserver();
        AdapterListener adapterListener = mock(AdapterListener.class);
        PreferenceHeaderAdapter preferenceHeaderAdapter = new PreferenceHeaderAdapter(getContext());
        preferenceHeaderAdapter.addListener(adapterListener);
        preferenceHeaderAdapter.registerDataSetObserver(dataSetObserver);
        boolean removed = preferenceHeaderAdapter.removeItem(preferenceHeader);
        assertFalse(removed);
        assertFalse(dataSetObserver.hasOnChangedBeenCalled());
        verify(adapterListener, times(0))
                .onPreferenceHeaderRemoved(preferenceHeaderAdapter, preferenceHeader, 0);
        assertTrue(preferenceHeaderAdapter.isEmpty());
        assertEquals(0, preferenceHeaderAdapter.getCount());
        assertEquals(0, preferenceHeaderAdapter.getAllItems().size());
    }

    /**
     * Ensures, that a {@link NullPointerException} is thrown by the method, which allows to remove
     * an item, if the item is null.
     */
    public final void testRemoveItemThrowsException() {
        try {
            PreferenceHeaderAdapter preferenceHeaderAdapter =
                    new PreferenceHeaderAdapter(getContext());
            preferenceHeaderAdapter.removeItem(null);
            Assert.fail();
        } catch (NullPointerException e) {
            return;
        }
    }

    /**
     * Tests the functionality of the method, which allows to remove all items.
     */
    public final void testClear() {
        PreferenceHeader preferenceHeader1 = new PreferenceHeader("foo");
        PreferenceHeader preferenceHeader2 = new PreferenceHeader("foo");
        DataSetObserver dataSetObserver = new DataSetObserver();
        AdapterListener adapterListener = mock(AdapterListener.class);
        PreferenceHeaderAdapter preferenceHeaderAdapter = new PreferenceHeaderAdapter(getContext());
        preferenceHeaderAdapter.addItem(preferenceHeader1);
        preferenceHeaderAdapter.addItem(preferenceHeader2);
        preferenceHeaderAdapter.addListener(adapterListener);
        preferenceHeaderAdapter.registerDataSetObserver(dataSetObserver);
        preferenceHeaderAdapter.clear();
        assertTrue(dataSetObserver.hasOnChangedBeenCalled());
        verify(adapterListener, times(1))
                .onPreferenceHeaderRemoved(preferenceHeaderAdapter, preferenceHeader1, 0);
        verify(adapterListener, times(1))
                .onPreferenceHeaderRemoved(preferenceHeaderAdapter, preferenceHeader2, 1);
        assertTrue(preferenceHeaderAdapter.isEmpty());
        assertEquals(0, preferenceHeaderAdapter.getCount());
        assertEquals(0, preferenceHeaderAdapter.getAllItems().size());
    }

    /**
     * Tests the functionality of the method, which allows to retrieve the index, a specific item
     * belongs to.
     */
    public final void testIndexOf() {
        PreferenceHeader preferenceHeader1 = new PreferenceHeader("foo");
        PreferenceHeader preferenceHeader2 = new PreferenceHeader("foo");
        PreferenceHeaderAdapter preferenceHeaderAdapter = new PreferenceHeaderAdapter(getContext());
        preferenceHeaderAdapter.addItem(preferenceHeader1);
        assertEquals(0, preferenceHeaderAdapter.indexOf(preferenceHeader1));
        assertEquals(-1, preferenceHeaderAdapter.indexOf(preferenceHeader2));
    }

    /**
     * Ensures, that a {@link NullPointerException} is thrown by the method, which allows to
     * retrieve the index, a specific item belongs to, if the item is null.
     */
    public final void testIndexOfThrowsException() {
        try {
            PreferenceHeaderAdapter preferenceHeaderAdapter =
                    new PreferenceHeaderAdapter(getContext());
            preferenceHeaderAdapter.indexOf(null);
            Assert.fail();
        } catch (NullPointerException e) {
            return;
        }
    }

    /**
     * Tests the functionality of the method, which allows to set, whether the adapter should be
     * enabled, or not.
     */
    public final void testSetEnabled() {
        PreferenceHeaderAdapter preferenceHeaderAdapter = new PreferenceHeaderAdapter(getContext());
        preferenceHeaderAdapter.setEnabled(false);
        assertFalse(preferenceHeaderAdapter.isEnabled(0));
    }

    /**
     * Tests the functionality of the getItemId-method.
     */
    public final void testGetItemId() {
        PreferenceHeaderAdapter preferenceHeaderAdapter = new PreferenceHeaderAdapter(getContext());
        assertEquals(0, preferenceHeaderAdapter.getItemId(0));
    }

    /**
     * Tests the functionality of the getView-method.
     */
    public final void testGetView() {
        CharSequence title = "title";
        CharSequence summary = "summary";
        int iconId = android.R.drawable.ic_delete;
        PreferenceHeader preferenceHeader = new PreferenceHeader(title);
        preferenceHeader.setSummary(summary);
        preferenceHeader.setIcon(getContext(), iconId);
        PreferenceHeaderDecoratorImplementation decorator =
                new PreferenceHeaderDecoratorImplementation();
        PreferenceHeaderAdapter preferenceHeaderAdapter = new PreferenceHeaderAdapter(getContext());
        preferenceHeaderAdapter.addDecorator(decorator);
        preferenceHeaderAdapter.addItem(preferenceHeader);
        View view = preferenceHeaderAdapter.getView(0, null, null);
        assertNotNull(view);
        assertEquals(0, decorator.position);
        assertEquals(preferenceHeader, decorator.preferenceHeader);
        assertEquals(view, decorator.view);
        TextView titleTextView = decorator.viewHolder.titleTextView;
        TextView summaryTextView = decorator.viewHolder.summaryTextView;
        ImageView iconImageView = decorator.viewHolder.iconImageView;
        assertEquals(title, titleTextView.getText());
        assertEquals(summary, summaryTextView.getText());
        assertEquals(View.VISIBLE, summaryTextView.getVisibility());
        assertNotNull(iconImageView.getDrawable());
        assertNotNull(view.getBackground());
    }

    /**
     * Tests the functionality of the getView-method, if the preference header's icon is null.
     */
    public final void testGetViewWhenIconIsNull() {
        CharSequence title = "title";
        CharSequence summary = "summary";
        PreferenceHeader preferenceHeader = new PreferenceHeader(title);
        preferenceHeader.setSummary(summary);
        PreferenceHeaderDecoratorImplementation decorator =
                new PreferenceHeaderDecoratorImplementation();
        PreferenceHeaderAdapter preferenceHeaderAdapter = new PreferenceHeaderAdapter(getContext());
        preferenceHeaderAdapter.addDecorator(decorator);
        preferenceHeaderAdapter.addItem(preferenceHeader);
        View view = preferenceHeaderAdapter.getView(0, null, null);
        assertNotNull(view);
        assertEquals(0, decorator.position);
        assertEquals(preferenceHeader, decorator.preferenceHeader);
        assertEquals(view, decorator.view);
        TextView titleTextView = decorator.viewHolder.titleTextView;
        TextView summaryTextView = decorator.viewHolder.summaryTextView;
        ImageView iconImageView = decorator.viewHolder.iconImageView;
        assertEquals(title, titleTextView.getText());
        assertEquals(summary, summaryTextView.getText());
        assertEquals(View.VISIBLE, summaryTextView.getVisibility());
        assertNull(iconImageView.getDrawable());
        assertNotNull(view.getBackground());
    }

    /**
     * Tests the functionality of the getView-method, if the preference header's summary is null.
     */
    public final void testGetViewWhenSummaryIsNull() {
        CharSequence title = "title";
        int iconId = android.R.drawable.ic_delete;
        PreferenceHeader preferenceHeader = new PreferenceHeader(title);
        preferenceHeader.setIcon(getContext(), iconId);
        PreferenceHeaderDecoratorImplementation decorator =
                new PreferenceHeaderDecoratorImplementation();
        PreferenceHeaderAdapter preferenceHeaderAdapter = new PreferenceHeaderAdapter(getContext());
        preferenceHeaderAdapter.addDecorator(decorator);
        preferenceHeaderAdapter.addItem(preferenceHeader);
        View view = preferenceHeaderAdapter.getView(0, null, null);
        assertNotNull(view);
        assertEquals(0, decorator.position);
        assertEquals(preferenceHeader, decorator.preferenceHeader);
        assertEquals(view, decorator.view);
        TextView titleTextView = decorator.viewHolder.titleTextView;
        TextView summaryTextView = decorator.viewHolder.summaryTextView;
        ImageView iconImageView = decorator.viewHolder.iconImageView;
        assertEquals(title, titleTextView.getText());
        assertTrue(TextUtils.isEmpty(summaryTextView.getText()));
        assertEquals(View.GONE, summaryTextView.getVisibility());
        assertNotNull(iconImageView.getDrawable());
        assertNotNull(view.getBackground());
    }

    /**
     * Tests the functionality of the getView-method, if the preference header's summary is empty.
     */
    public final void testGetViewWhenSummaryIsEmpty() {
        CharSequence title = "title";
        int iconId = android.R.drawable.ic_delete;
        PreferenceHeader preferenceHeader = new PreferenceHeader(title);
        preferenceHeader.setSummary("");
        preferenceHeader.setIcon(getContext(), iconId);
        PreferenceHeaderDecoratorImplementation decorator =
                new PreferenceHeaderDecoratorImplementation();
        PreferenceHeaderAdapter preferenceHeaderAdapter = new PreferenceHeaderAdapter(getContext());
        preferenceHeaderAdapter.addDecorator(decorator);
        preferenceHeaderAdapter.addItem(preferenceHeader);
        View view = preferenceHeaderAdapter.getView(0, null, null);
        assertNotNull(view);
        assertEquals(0, decorator.position);
        assertEquals(preferenceHeader, decorator.preferenceHeader);
        assertEquals(view, decorator.view);
        TextView titleTextView = decorator.viewHolder.titleTextView;
        TextView summaryTextView = decorator.viewHolder.summaryTextView;
        ImageView iconImageView = decorator.viewHolder.iconImageView;
        assertEquals(title, titleTextView.getText());
        assertTrue(TextUtils.isEmpty(summaryTextView.getText()));
        assertEquals(View.GONE, summaryTextView.getVisibility());
        assertNotNull(iconImageView.getDrawable());
        assertNotNull(view.getBackground());
    }

    /**
     * Tests the functionality of the getView-method, if the inflated view does not contain the
     * necessary child views.
     */
    public final void testGetViewWhenViewDoesNotContainChildViews() {
        PreferenceHeader preferenceHeader = new PreferenceHeader("foo");
        PreferenceHeaderDecoratorImplementation decorator =
                new PreferenceHeaderDecoratorImplementation();
        PreferenceHeaderAdapter preferenceHeaderAdapter = new PreferenceHeaderAdapter(getContext());
        preferenceHeaderAdapter.setViewId(android.R.layout.list_content);
        preferenceHeaderAdapter.addDecorator(decorator);
        preferenceHeaderAdapter.addItem(preferenceHeader);
        View view = preferenceHeaderAdapter.getView(0, null, null);
        assertNotNull(view);
        assertEquals(0, decorator.position);
        assertEquals(preferenceHeader, decorator.preferenceHeader);
        assertEquals(view, decorator.view);
        assertNull(decorator.viewHolder.titleTextView);
        assertNull(decorator.viewHolder.summaryTextView);
        assertNull(decorator.viewHolder.iconImageView);
        assertNotNull(view.getBackground());
    }

}