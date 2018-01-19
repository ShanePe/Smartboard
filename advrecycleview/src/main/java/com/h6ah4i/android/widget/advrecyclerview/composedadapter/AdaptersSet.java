/*
 *    Copyright (C) 2016 Haruki Hasegawa
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.h6ah4i.android.widget.advrecyclerview.composedadapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.h6ah4i.android.widget.advrecyclerview.adapter.BridgeAdapterDataObserver;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"CanBeFinal", "UnusedReturnValue"})
class AdaptersSet {
    static long NO_SEGMENTED_POSITION = -1L;

    private BridgeAdapterDataObserver.Subscriber mSubscriber;
    private List<ComposedChildAdapterTag> mAdapterTags;
    private List<RecyclerView.Adapter> mAdapters;

    private List<RecyclerView.Adapter> mUniqueAdapters;
    private List<ComposedChildAdapterDataObserver> mObservers;

    AdaptersSet(BridgeAdapterDataObserver.Subscriber bridgeSubscriber) {
        mSubscriber = bridgeSubscriber;
        mAdapterTags = new ArrayList<>();
        mAdapters = new ArrayList<>();
        mUniqueAdapters = new ArrayList<>();
        mObservers = new ArrayList<>();
    }

    ComposedChildAdapterTag addAdapter(@NonNull RecyclerView.Adapter adapter, int position) {
        final ComposedChildAdapterTag tag = new ComposedChildAdapterTag();

        mAdapterTags.add(position, tag);
        mAdapters.add(position, adapter);

        ComposedChildAdapterDataObserver observer;

        final int uniqueAdapterIndex = mUniqueAdapters.indexOf(adapter);

        if (uniqueAdapterIndex >= 0) {
            observer = mObservers.get(uniqueAdapterIndex);
        } else {
            observer = new ComposedChildAdapterDataObserver(mSubscriber, adapter);
            mObservers.add(observer);
            mUniqueAdapters.add(adapter);

            adapter.registerAdapterDataObserver(observer);
        }

        observer.registerChildAdapterTag(tag);

        return tag;
    }

    RecyclerView.Adapter removeAdapter(@NonNull ComposedChildAdapterTag tag) {
        final int segment = getAdapterSegment(tag);

        if (segment < 0) {
            return null;
        }

        final RecyclerView.Adapter adapter = mAdapters.remove(segment);
        mAdapterTags.remove(segment);


        final int uniqueAdapterIndex = mUniqueAdapters.indexOf(adapter);

        if (uniqueAdapterIndex < 0) {
            throw new IllegalStateException("Something wrong. Inconsistency detected.");
        }

        final ComposedChildAdapterDataObserver observer = mObservers.get(uniqueAdapterIndex);

        observer.unregisterChildAdapterTag(tag);

        if (!observer.hasChildAdapters()) {
            adapter.unregisterAdapterDataObserver(observer);
        }

        return adapter;
    }

    int getAdapterSegment(ComposedChildAdapterTag tag) {
        return mAdapterTags.indexOf(tag);
    }

    int getSegmentCount() {
        return mAdapters.size();
    }

    RecyclerView.Adapter getAdapter(int segment) {
        return mAdapters.get(segment);
    }

    ComposedChildAdapterTag getTag(int segment) {
        return mAdapterTags.get(segment);
    }

    static int extractSegment(long segmentedPosition) {
        return (int) (segmentedPosition >>> 32);
    }

    static int extractSegmentOffset(long segmentedPosition) {
        return (int) (segmentedPosition & 0xFFFFFFFFL);
    }

    static long composeSegmentedPosition(int segment, int offset) {
        return (((long) segment) << 32) | (offset & 0xFFFFFFFFL);
    }

    public void release() {
        mAdapterTags.clear();
        mAdapters.clear();

        int numUniqueAdapters = mUniqueAdapters.size();
        for (int i = 0; i < numUniqueAdapters; i++) {
            ComposedChildAdapterDataObserver observer = mObservers.get(i);
            RecyclerView.Adapter adapter = mUniqueAdapters.get(i);
            adapter.unregisterAdapterDataObserver(observer);
            observer.release();
        }
        mUniqueAdapters.clear();
        mObservers.clear();
    }

    List<RecyclerView.Adapter> getUniqueAdaptersList() {
        return mUniqueAdapters;
    }
}
