package com.restbusters.webdriver.enums;

import lombok.Getter;

/**
 * Enumeration of supported WebDriver browser types.
 * @author amatsaylo on 9/26/25
 * @project qreasp
 */

@Getter
public enum DriverType {
    CHROME("Chrome Browser"),
    FIREFOX("Firefox Browser"),
    EDGE("Microsoft Edge"),
    SAFARI("Safari Browser"),
    OPERA("Opera Browser");

    private final String displayName;

    DriverType(String displayName) {
        this.displayName = displayName;
    }
}
