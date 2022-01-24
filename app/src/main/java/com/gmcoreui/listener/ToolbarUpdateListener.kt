package com.gmcoreui.listener

/**
 * Interface definition for the callback to be invoked to update the toolbar contents.
 */

interface ToolbarUpdateListener {

    /**
     * Method to be invoked to update the toolbar title.
     *
     * @param title title to be updated.
     */
    fun updateTitle(title: String)

}
