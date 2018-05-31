package com.nicky.framework.component;

import com.rl.commons.interf.EdwinTimeoutCallback;

/**
 * Created by Nicky on 2017/8/28.
 */

public interface TimeOutComponent {

    void startTimeoutThread( EdwinTimeoutCallback callback );
    void clearTimeoutThread( );

}
