package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import java.time.LocalDateTime;
import java.util.Objects;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final LocationResolver locationResolver;

  @Inject
  public ReplaceWarehouseUseCase(WarehouseStore warehouseStore, LocationResolver locationResolver) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
  }

  @Override
  @Transactional
  public void replace(Warehouse newWarehouse) {
    CreateWarehouseUseCase.validateFields(newWarehouse);
    Warehouse previous = warehouseStore.findByBusinessUnitCode(newWarehouse.businessUnitCode);
    if (previous == null) throw new WebApplicationException("Active warehouse not found.", 404);
    if (!Objects.equals(previous.stock, newWarehouse.stock))
      throw new WebApplicationException("Replacement stock must match the current warehouse stock.", 400);
    if (newWarehouse.capacity < previous.stock)
      throw new WebApplicationException("Replacement capacity cannot accommodate the existing stock.", 400);
    Location location = locationResolver.resolveByIdentifier(newWarehouse.location);
    if (location == null) throw new WebApplicationException("Unknown warehouse location.", 400);
    newWarehouse.location = location.identification;
    var inLocation = warehouseStore.getAll().stream().filter(w -> w.location.equals(location.identification)).toList();
    boolean sameLocation = Objects.equals(previous.location, location.identification);
    if (!sameLocation && inLocation.size() >= location.maxNumberOfWarehouses)
      throw new WebApplicationException("The location has reached its warehouse limit.", 409);
    int occupiedCapacity = inLocation.stream().mapToInt(w -> w.capacity).sum()
        - (sameLocation ? previous.capacity : 0);
    if (occupiedCapacity + newWarehouse.capacity > location.maxCapacity)
      throw new WebApplicationException("The location has insufficient remaining capacity.", 400);

    previous.archivedAt = LocalDateTime.now();
    warehouseStore.update(previous);
    newWarehouse.createdAt = LocalDateTime.now();
    newWarehouse.archivedAt = null;
    warehouseStore.create(newWarehouse);
  }
}
