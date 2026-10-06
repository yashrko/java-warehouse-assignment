package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import java.time.LocalDateTime;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final LocationResolver locationResolver;

  @Inject
  public CreateWarehouseUseCase(WarehouseStore warehouseStore, LocationResolver locationResolver) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
  }

  @Override
  @Transactional
  public void create(Warehouse warehouse) {
    validateFields(warehouse);
    if (warehouseStore.findByBusinessUnitCode(warehouse.businessUnitCode) != null)
      throw new WebApplicationException("Business unit code is already in use.", 409);
    Location location = locationResolver.resolveByIdentifier(warehouse.location);
    if (location == null) throw new WebApplicationException("Unknown warehouse location.", 400);
    warehouse.location = location.identification;
    var active = warehouseStore.getAll().stream().filter(w -> w.location.equals(location.identification)).toList();
    if (active.size() >= location.maxNumberOfWarehouses)
      throw new WebApplicationException("The location has reached its warehouse limit.", 409);
    int occupiedCapacity = active.stream().mapToInt(w -> w.capacity).sum();
    if (occupiedCapacity + warehouse.capacity > location.maxCapacity)
      throw new WebApplicationException("The location has insufficient remaining capacity.", 400);
    if (warehouse.createdAt == null) warehouse.createdAt = LocalDateTime.now();
    warehouse.archivedAt = null;
    warehouseStore.create(warehouse);
  }

  static void validateFields(Warehouse warehouse) {
    if (warehouse == null || warehouse.businessUnitCode == null || warehouse.businessUnitCode.isBlank()
        || warehouse.location == null || warehouse.location.isBlank()
        || warehouse.capacity == null || warehouse.stock == null
        || warehouse.capacity < 0 || warehouse.stock < 0 || warehouse.stock > warehouse.capacity)
      throw new WebApplicationException("Warehouse requires a code, location, non-negative capacity and stock no greater than capacity.", 400);
  }
}
