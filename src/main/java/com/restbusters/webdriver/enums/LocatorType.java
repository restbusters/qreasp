package com.restbusters.webdriver.enums;

import lombok.Getter;
/**
 * Enumeration of all supported Selenium locator strategies.
 *  * @author amatsaylo on 9/26/25
 *  * @project qreasp
 */
@Getter
public enum LocatorType {
    ID("id"),
    NAME("name"),
    CLASS_NAME("className"),
    TAG_NAME("tagName"),
    LINK_TEXT("linkText"),
    PARTIAL_LINK_TEXT("partialLinkText"),
    CSS_SELECTOR("cssSelector"),
    XPATH("xpath");

    private final String seleniumMethod;

    LocatorType(String seleniumMethod) {
        this.seleniumMethod = seleniumMethod;
    }
}
