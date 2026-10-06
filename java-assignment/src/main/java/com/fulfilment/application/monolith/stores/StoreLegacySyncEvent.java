package com.fulfilment.application.monolith.stores;

/** Transactional notification that a store change is ready for legacy synchronization. */
public record StoreLegacySyncEvent(Store store, Operation operation) {
  public enum Operation { CREATE, UPDATE }
}
