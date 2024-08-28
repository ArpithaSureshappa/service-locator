package com.igot.service_locator.security;

public enum ContentSource {
    CORNELL;

    public static ContentSource fromProviderName(String providerName) {
        switch (providerName) {
            case "eCornell":
                return CORNELL;
            default:
                throw new RuntimeException("Unknown provider name: " + providerName);
        }
    }
}
