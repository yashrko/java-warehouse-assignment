package com.fulfilment.application.monolith.stores;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.jboss.logging.Logger;

@ApplicationScoped
public class LegacyStoreManagerGateway {
  private static final Logger LOGGER = Logger.getLogger(LegacyStoreManagerGateway.class);

  public void synchronizeAfterCommit(
      @Observes(during = TransactionPhase.AFTER_SUCCESS) StoreLegacySyncEvent event) {
    if (event.operation() == StoreLegacySyncEvent.Operation.CREATE) {
      createStoreOnLegacySystem(event.store());
    } else {
      updateStoreOnLegacySystem(event.store());
    }
  }

  public void createStoreOnLegacySystem(Store store) {
    // just to emulate as this would send this to a legacy system, let's write a temp file with the
    writeToFile(store);
  }

  public void updateStoreOnLegacySystem(Store store) {
    // just to emulate as this would send this to a legacy system, let's write a temp file with the
    writeToFile(store);
  }

  private void writeToFile(Store store) {
    Path tempFile = null;
    try {
      String content =
          "Store created. [ name ="
              + store.name
              + " ] [ items on stock ="
              + store.quantityProductsInStock
              + "]";
      tempFile = Files.createTempFile("store-sync-", ".txt");
      Files.writeString(tempFile, content, StandardCharsets.UTF_8);
      LOGGER.debugf("Legacy store synchronization completed for store %s", store.name);
    } catch (IOException e) {
      LOGGER.error("Legacy store synchronization failed after the database commit", e);
    } finally {
      if (tempFile != null) {
        try {
          Files.deleteIfExists(tempFile);
        } catch (IOException e) {
          LOGGER.warn("Could not remove temporary legacy synchronization file", e);
        }
      }
    }
  }
}
