package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static com.fulfilment.application.monolith.warehouses.domain.usecases.CreateWarehouseUseCaseTest.*;
import static org.junit.jupiter.api.Assertions.*;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import org.junit.jupiter.api.Test;

class ArchiveWarehouseUseCaseTest {
  @Test void marksWarehouseArchived() {
    FakeStore store = new FakeStore(); Warehouse warehouse = warehouse("code", 20, 2); store.items.add(warehouse);
    new ArchiveWarehouseUseCase(store).archive(warehouse);
    assertNotNull(warehouse.archivedAt);
    assertNull(store.findByBusinessUnitCode("code"));
  }
  @Test void rejectsMissingWarehouse() {
    Warehouse warehouse = warehouse("missing", 1, 0);
    assertStatus(404, () -> new ArchiveWarehouseUseCase(new FakeStore()).archive(warehouse));
  }
}
