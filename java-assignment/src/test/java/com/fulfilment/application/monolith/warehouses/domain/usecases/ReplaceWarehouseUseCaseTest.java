package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static com.fulfilment.application.monolith.warehouses.domain.usecases.CreateWarehouseUseCaseTest.*;
import static org.junit.jupiter.api.Assertions.*;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class ReplaceWarehouseUseCaseTest {
  @Test void archivesOldAndCreatesNewWarehouseWithSameStock() {
    FakeStore store = new FakeStore(); Warehouse old = warehouse("same", 30, 12); store.items.add(old);
    Warehouse replacement = warehouse("same", 25, 12);
    new ReplaceWarehouseUseCase(store, id -> new Location(id, 1, 40)).replace(replacement);
    assertNotNull(old.archivedAt);
    assertEquals(2, store.items.size());
    assertNull(store.findByBusinessUnitCode("same").archivedAt);
  }
  @Test void rejectsMissingOldWarehouseAndStockMismatch() {
    FakeStore store = new FakeStore();
    assertStatus(404, () -> new ReplaceWarehouseUseCase(store, id -> new Location(id, 1, 100)).replace(warehouse("missing", 20, 1)));
    store.items.add(warehouse("same", 20, 4));
    assertStatus(400, () -> new ReplaceWarehouseUseCase(store, id -> new Location(id, 1, 100)).replace(warehouse("same", 20, 3)));
  }
  @Test void rejectsCapacityOrLocationErrors() {
    FakeStore store = new FakeStore(); store.items.add(warehouse("same", 20, 10));
    assertStatus(400, () -> new ReplaceWarehouseUseCase(store, id -> new Location(id, 1, 100)).replace(warehouse("same", 9, 10)));
    assertStatus(400, () -> new ReplaceWarehouseUseCase(store, id -> null).replace(warehouse("same", 20, 10)));
    assertStatus(400, () -> new ReplaceWarehouseUseCase(store, id -> new Location(id, 1, 15)).replace(warehouse("same", 20, 10)));
  }
}
