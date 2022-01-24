package com.gmcoreui.controllers.ui;

import com.gmcoreui.controllers.ui.WidgetObserver;

/**
 * Created by ganeshkanna.subraman on 16-02-2018.
 */

public interface GMWidget {
    String getJsonKey();

    Object getJsonObject();

    void registerObserver(WidgetObserver widgetObserver);

    void removeObserver(WidgetObserver widgetObserver);

    void notifyObservers();

}