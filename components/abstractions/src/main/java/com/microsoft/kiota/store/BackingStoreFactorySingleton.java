package com.microsoft.kiota.store;

import javax.annotation.Nonnull;

/** This class is used to register the backing store factory. */
public class BackingStoreFactorySingleton {
    /** Default constructor */
    public BackingStoreFactorySingleton() {
        // Default constructor
    }
    /** The backing store factory singleton instance. */
    @Nonnull
    public static BackingStoreFactory instance = new InMemoryBackingStoreFactory();
}