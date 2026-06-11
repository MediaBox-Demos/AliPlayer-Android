package com.alivc.player.playerkits.shortvideolist.controller.preload;

import com.alivc.player.playerkits.shortvideolist.utils.SLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A generic class that manages sliding window loading and cancellation for a collection of items.
 *
 * @param <T> Type of items to be managed in the sliding window.
 * @author baorunchen
 * @date 2024/8/15
 */
public class AliSlidingWindow<T> {
    private static final boolean ENABLE_LOG_FLAG = true;

    private final int[] windowItems;

    // List to hold the items
    private final List<T> itemList;

    // Set to keep track of executed items
    private final Set<T> executedItemSets;

    // Callbacks for Executing and canceling
    private final Callback<T> callback;

    private final String extra;

    // Current position in the item list
    private final AtomicInteger currentPosition;

    /**
     * Constructor for SlidingWindowManager.
     *
     * @param leftWindowSize  Size of the left sliding window.
     * @param rightWindowSize Size of the right sliding window.
     * @param callback        Callback for processing items.
     * @param extra           log extra
     */
    public AliSlidingWindow(int leftWindowSize, int rightWindowSize, Callback<T> callback, String extra) {
        this.windowItems = getWindowRange(leftWindowSize, rightWindowSize);

        this.itemList = new LinkedList<>();

        this.executedItemSets = new HashSet<>();

        this.callback = callback;

        this.extra = extra;

        this.currentPosition = new AtomicInteger(-1);

        printLog("CONSTRUCT", leftWindowSize + ", " + rightWindowSize);
    }

    /**
     * Constructor for SlidingWindowManager.
     *
     * @param items    Items of sliding window.
     * @param callback Callback for processing items.
     * @param extra    log extra
     */
    public AliSlidingWindow(int[] items, Callback<T> callback, String extra) {
        this.windowItems = items;

        this.itemList = new LinkedList<>();

        this.executedItemSets = new HashSet<>();

        this.callback = callback;

        this.extra = extra;

        this.currentPosition = new AtomicInteger(-1);

        printLog("CONSTRUCT", Arrays.toString(items));
    }

    /**
     * Set the items to be managed.
     * Cancels any ongoing loads and replaces the item list.
     *
     * @param items List of items to be set.
     */
    public void setItems(List<T> items) {
        cancelAll();
        synchronized (itemList) {
            itemList.clear();
            // Add new items to the item list and remove invalid items
            for (int i = 0; i < items.size(); i++) {
                T item = items.get(i);
                if (callback.isValid(item)) {
                    itemList.add(item);
                }
            }
        }
        printLog("API-SET", items);
    }

    /**
     * Add new items to the existing item list.
     *
     * @param items List of items to be added.
     */
    public void addItems(List<T> items) {
        synchronized (itemList) {
            // Add new items to the item list and remove invalid items
            for (int i = 0; i < items.size(); i++) {
                T item = items.get(i);
                if (callback.isValid(item)) {
                    itemList.add(item);
                }
            }
        }
        printLog("API-ADD", items);
    }

    /**
     * Move to a specified position in the item list and execute new items.
     *
     * @param position The new position to move to.
     */
    public void moveTo(int position) {
        printLog("API-MOVE", currentPosition.get() + "->" + position);

        // Invalid position
        if (position < 0 || position >= itemList.size()) {
            return;
        }

        // No change in position
        if (currentPosition.get() == position) {
            return;
        }

        List<T> currentWindow = getWindowIndices(position);
        List<T> previousWindow = getWindowIndices(currentPosition.get());

        // Determine new items to load and cancel
        List<T> toLoad = new LinkedList<>(currentWindow);
        if (currentPosition.get() >= 0) {
            toLoad.removeAll(previousWindow);
        }
        List<T> toCancel = new LinkedList<>(previousWindow);
        toCancel.removeAll(currentWindow);

        cancelItems(toCancel);
        executeItems(toLoad);

        currentPosition.set(position);
    }

    /**
     * Release resources, cancel all ongoing operations.
     */
    public void release() {
        printLog("API-RELEASE");
        // Cancel any ongoing operations
        cancelAll();
        currentPosition.set(-1);
    }

    /**
     * Retrieve the current position in the item list.
     *
     * @return The current position.
     */
    public int getCurrentPosition() {
        return currentPosition.get();
    }

    /**
     * Get items within the sliding window based on the current position.
     *
     * @param position The current position.
     * @return List of items in the current sliding window.
     */
    private List<T> getWindowIndices(int position) {
        List<T> windowIndices = new LinkedList<>();
        synchronized (itemList) {
            // Loop through the defined range and collect window items
            for (int i = 0; i < windowItems.length; i++) {
                int index = position + windowItems[i];
                if (index >= 0 && index < itemList.size()) {
                    windowIndices.add(itemList.get(index));
                }
            }
        }
        return windowIndices;
    }

    /**
     * Get the window range based on provided left and right window sizes.
     *
     * @return int[] representing the window range.
     */
    private int[] getWindowRange(int leftWindowSize, int rightWindowSize) {
        int totalSize = leftWindowSize + rightWindowSize + 1;
        int[] windowRange = new int[totalSize];

        for (int i = 0; i < totalSize; i++) {
            windowRange[i] = i - leftWindowSize;
        }

        return windowRange;
    }

    /**
     * Cancel specific items.
     *
     * @param items List of items to cancel.
     */
    private void cancelItems(List<T> items) {
        for (T item : items) {
            cancelItem(item);
        }
    }

    /**
     * cancel item
     *
     * @param item item to cancel
     */
    private void cancelItem(T item) {
        if (item != null && executedItemSets.contains(item)) {
            executedItemSets.remove(item);
            printLog("CANCEL", item);
            callback.cancel(item);
        }
    }

    /**
     * Execute items and start loading the first item.
     *
     * @param items List of items to execute.
     */
    private void executeItems(List<T> items) {
        for (T item : items) {
            executeItem(item);
        }
    }

    /**
     * Execute item
     *
     * @param item item to execute
     */
    private void executeItem(T item) {
        if (item != null && !executedItemSets.contains(item)) {
            executedItemSets.add(item);
            printLog("EXECUTE", item);
            callback.execute(item);
        }
    }

    /**
     * Cancel all ongoing operations and clear the item list.
     */
    private void cancelAll() {
        synchronized (itemList) {
            itemList.clear();
        }
        cancelItems(new ArrayList<>(executedItemSets));
        assert executedItemSets.isEmpty();
    }

    public void refresh() {
        // get all items in the current window
        List<T> currentWindow = getWindowIndices(currentPosition.get());

        // if the window is empty, these is no need to refresh
        if (!currentWindow.isEmpty()) {
            // cancel all items in the current window
            cancelItems(currentWindow);
            // re execute all items in the current window
            executeItems(currentWindow);
        }
    }

    /**
     * print log message
     *
     * @param method   method
     * @param messages log messages
     */
    private void printLog(String method, Object... messages) {
        if (ENABLE_LOG_FLAG) {
            SLog.i(this, String.format("%s-%s", extra, method), messages);
        }
    }

    public abstract static class Callback<T> {
        /**
         * execute item task
         *
         * @param item item to execute
         */
        public abstract void execute(T item);

        /**
         * cancel item task
         *
         * @param item item to cancel
         */
        public void cancel(T item) {
        }

        /**
         * check item is valid
         *
         * @param item item
         * @return is valid
         */
        public boolean isValid(T item) {
            return true;
        }
    }
}
