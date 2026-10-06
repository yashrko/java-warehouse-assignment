package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class WarehouseRepository implements WarehouseStore, PanacheRepository<DbWarehouse> {

  @Override
  public List<Warehouse> getAll() {
    return this.list("archivedAt is null").stream().map(DbWarehouse::toWarehouse).toList();
  }

  @Override
  public void create(Warehouse warehouse) {
    DbWarehouse entity = new DbWarehouse();
    copy(warehouse, entity);
    persist(entity);
    warehouse.id = entity.id;
  }

  @Override
  public void update(Warehouse warehouse) {
    DbWarehouse entity = find("businessUnitCode = ?1 and archivedAt is null", warehouse.businessUnitCode).firstResult();
    if (entity == null) throw new IllegalArgumentException("Active warehouse not found: " + warehouse.businessUnitCode);
    copy(warehouse, entity);
  }

  @Override
  public void remove(Warehouse warehouse) {
    DbWarehouse entity = find("businessUnitCode = ?1 and archivedAt is null", warehouse.businessUnitCode).firstResult();
    if (entity != null) delete(entity);
  }

  @Override
  public Warehouse findByBusinessUnitCode(String buCode) {
    DbWarehouse entity = find("businessUnitCode = ?1 and archivedAt is null", buCode).firstResult();
    return entity == null ? null : entity.toWarehouse();
  }

  /** Resolves the public path identifier, accepting either a business code or legacy numeric row id. */
  public Warehouse findActiveByIdentifier(String identifier) {
    Warehouse byCode = findByBusinessUnitCode(identifier);
    if (byCode != null || identifier == null) return byCode;
    try {
      DbWarehouse entity = findById(Long.valueOf(identifier));
      return entity == null || entity.archivedAt != null ? null : entity.toWarehouse();
    } catch (NumberFormatException ignored) {
      return null;
    }
  }

  public Warehouse findActiveByNumericId(String identifier) {
    if (identifier == null) return null;
    try {
      DbWarehouse entity = findById(Long.valueOf(identifier));
      return entity == null || entity.archivedAt != null ? null : entity.toWarehouse();
    } catch (NumberFormatException ignored) {
      return null;
    }
  }

  private void copy(Warehouse source, DbWarehouse target) {
    target.businessUnitCode = source.businessUnitCode;
    target.location = source.location;
    target.capacity = source.capacity;
    target.stock = source.stock;
    target.createdAt = source.createdAt;
    target.archivedAt = source.archivedAt;
  }
}
