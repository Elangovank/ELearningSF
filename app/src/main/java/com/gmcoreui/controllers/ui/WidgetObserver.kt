package com.gmcoreui.controllers.ui

import com.gmcoreui.controllers.ui.GMWidget

/**
 * Created by ganeshkanna.subraman on 19-02-2018.
 */

interface WidgetObserver {
    fun onDataChanged(widget: GMWidget, value: Any)
}
