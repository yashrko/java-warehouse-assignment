package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.*;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import java.util.ArrayList;
import java.util.List;
import jakarta.ws.rs.WebApplicationException;
import org.junit.jupiter.api.Test;

class CreateWarehouseUseCaseTest {
  @Test void createsWhenLocationHasRoom() {
    FakeStore store = new FakeStore();
    Warehouse item = warehouse("new", 20, 5);
    new CreateWarehouseUseCase(store, id -> new Location(id, 2, 40)).create(item);
    assertEquals(1, store.items.size());
    assertEquals(item, store.items.get(0));
  }

  @Test void rejectsDuplicateBusinessUnitCode() {
    FakeStore store = new FakeStore(); store.items.add(warehouse("same", 10, 2));
    assertStatus(409, () -> new CreateWarehouseUseCase(store, id -> new Location(id, 3, 100)).create(warehouse("same", 10, 2)));
  }

  @Test void rejectsUnknownLocationAndLocationLimits() {
    FakeStore empty = new FakeStore();
    assertStatus(400, () -> new CreateWarehouseUseCase(empty, id -> null).create(warehouse("new", 1, 0)));
    FakeStore full = new FakeStore(); full.items.add(warehouse("existing", 10, 1));
    assertStatus(409, () -> new CreateWarehouseUseCase(full, id -> new Location(id, 1, 100)).create(warehouse("new", 10, 1)));
    assertStatus(400, () -> new CreateWarehouseUseCase(empty, id -> new Location(id, 4, 10)).create(warehouse("new", 11, 1)));
  }

  @Test void rejectsInvalidStockAndCapacity() {
    FakeStore store = new FakeStore();
    assertStatus(400, () -> new CreateWarehouseUseCase(store, id -> new Location(id, 4, 100)).create(warehouse("bad", 2, 3)));
  }

  static Warehouse warehouse(String code, int capacity, int stock) {
    Warehouse warehouse = new Warehouse(); warehouse.businessUnitCode = code; warehouse.location = "LOC";
    warehouse.capacity = capacity; warehouse.stock = stock; return warehouse;
  }
  static void assertStatus(int status, Runnable action) {
    WebApplicationException error = assertThrows(WebApplicationException.class, action::run);
    assertEquals(status, error.getResponse().getStatus());
  }
  static class FakeStore implements WarehouseStore {
    final List<Warehouse> items = new ArrayList<>();
    public List<Warehouse> getAll() { return items.stream().filter(w -> w.archivedAt == null).toList(); }
    public void create(Warehouse w) { items.add(w); }
    public void update(Warehouse w) { }
    public void remove(Warehouse w) { items.remove(w); }
    public Warehouse findByBusinessUnitCode(String code) { return getAll().stream().filter(w -> w.businessUnitCode.equals(code)).findFirst().orElse(null); }
  }
}
