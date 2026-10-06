package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.usecases.ArchiveWarehouseUseCase;
import com.fulfilment.application.monolith.warehouses.domain.usecases.CreateWarehouseUseCase;
import com.fulfilment.application.monolith.warehouses.domain.usecases.ReplaceWarehouseUseCase;
import com.warehouse.api.WarehouseResource;
import com.warehouse.api.beans.Warehouse;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.WebApplicationException;
import jakarta.transaction.Transactional;
import java.util.List;

@RequestScoped
@Transactional
public class WarehouseResourceImpl implements WarehouseResource {

  @Inject private WarehouseRepository warehouseRepository;
  @Inject private CreateWarehouseUseCase createWarehouse;
  @Inject private ReplaceWarehouseUseCase replaceWarehouse;
  @Inject private ArchiveWarehouseUseCase archiveWarehouse;

  @Override
  public List<com.warehouse.api.beans.Warehouse> listAllWarehousesUnits() {
    return warehouseRepository.getAll().stream().map(this::toWarehouseResponse).toList();
  }

  @Override
  public Warehouse createANewWarehouseUnit(@NotNull Warehouse data) {
    com.fulfilment.application.monolith.warehouses.domain.models.Warehouse model = toDomain(data);
    createWarehouse.create(model);
    return toWarehouseResponse(model);
  }

  @Override
  public Warehouse getAWarehouseUnitByID(String id) {
    com.fulfilment.application.monolith.warehouses.domain.models.Warehouse model = warehouseRepository.findActiveByIdentifier(id);
    if (model == null) throw new WebApplicationException("Warehouse not found.", 404);
    return toWarehouseResponse(model);
  }

  @Override
  public void archiveAWarehouseUnitByID(String id) {
    com.fulfilment.application.monolith.warehouses.domain.models.Warehouse model = warehouseRepository.findActiveByNumericId(id);
    if (model == null) model = warehouseRepository.findByBusinessUnitCode(id);
    if (model == null) throw new WebApplicationException("Warehouse not found.", 404);
    archiveWarehouse.archive(model);
  }

  @Override
  public Warehouse replaceTheCurrentActiveWarehouse(
      String businessUnitCode, @NotNull com.warehouse.api.beans.Warehouse data) {
    com.fulfilment.application.monolith.warehouses.domain.models.Warehouse model = toDomain(data);
    model.businessUnitCode = businessUnitCode;
    replaceWarehouse.replace(model);
    return toWarehouseResponse(model);
  }

  private com.fulfilment.application.monolith.warehouses.domain.models.Warehouse toDomain(com.warehouse.api.beans.Warehouse data) {
    if (data == null) throw new WebApplicationException("Warehouse body is required.", 400);
    com.fulfilment.application.monolith.warehouses.domain.models.Warehouse model = new com.fulfilment.application.monolith.warehouses.domain.models.Warehouse();
    model.businessUnitCode = data.getBusinessUnitCode();
    model.location = data.getLocation();
    model.capacity = data.getCapacity();
    model.stock = data.getStock();
    model.createdAt = java.time.LocalDateTime.now();
    return model;
  }

  private com.warehouse.api.beans.Warehouse toWarehouseResponse(
      com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse) {
    var response = new Warehouse();
    response.setId(warehouse.id == null ? null : warehouse.id.toString());
    response.setBusinessUnitCode(warehouse.businessUnitCode);
    response.setLocation(warehouse.location);
    response.setCapacity(warehouse.capacity);
    response.setStock(warehouse.stock);

    return response;
  }
}
