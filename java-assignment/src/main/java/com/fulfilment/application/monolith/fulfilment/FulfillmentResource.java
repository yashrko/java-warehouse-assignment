package com.fulfilment.application.monolith.fulfilment;

import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import java.util.List;

@Path("fulfillment")
@ApplicationScoped
@Transactional
@Consumes("application/json")
@Produces("application/json")
public class FulfillmentResource implements PanacheRepository<Fulfillment> {
  @Inject EntityManager entityManager;

  public record AssignmentRequest(Long storeId, Long productId, String businessUnitCode) {}
  public record AssignmentResponse(Long storeId, Long productId, String businessUnitCode) {}

  @GET
  public List<AssignmentResponse> list() {
    return listAll().stream().map(FulfillmentResource::toResponse).toList();
  }

  @POST
  @Transactional
  public AssignmentResponse assign(AssignmentRequest request) {
    if (request == null || request.storeId() == null || request.productId() == null
        || request.businessUnitCode() == null || request.businessUnitCode().isBlank()) {
      throw new WebApplicationException("storeId, productId and businessUnitCode are required.", 400);
    }
    Store store = entityManager.find(Store.class, request.storeId(), LockModeType.PESSIMISTIC_WRITE);
    Product product = entityManager.find(Product.class, request.productId());
    DbWarehouse warehouse = entityManager.createQuery("from DbWarehouse w where w.businessUnitCode = :code and w.archivedAt is null", DbWarehouse.class)
        .setParameter("code", request.businessUnitCode()).getResultStream().findFirst().orElse(null);
    if (store == null || product == null || warehouse == null)
      throw new WebApplicationException("Store, product and active warehouse must exist.", 404);
    entityManager.lock(warehouse, LockModeType.PESSIMISTIC_WRITE);
    if (count("store.id = ?1 and product.id = ?2 and warehouse.id = ?3", store.id, product.id, warehouse.id) > 0)
      throw new WebApplicationException("This fulfillment assignment already exists.", 409);

    long productWarehouseCount = count("store.id = ?1 and product.id = ?2", store.id, product.id);
    if (productWarehouseCount >= 2)
      throw new WebApplicationException("A product can use at most two warehouses per store.", 409);
    long storeWarehouseCount = entityManager.createQuery(
        "select count(distinct f.warehouse.id) from Fulfillment f where f.store.id = :store", Long.class)
        .setParameter("store", store.id).getSingleResult();
    boolean alreadyAtStore = count("store.id = ?1 and warehouse.id = ?2", store.id, warehouse.id) > 0;
    if (!alreadyAtStore && storeWarehouseCount >= 3)
      throw new WebApplicationException("A store can use at most three warehouses.", 409);
    long warehouseProductCount = entityManager.createQuery(
        "select count(distinct f.product.id) from Fulfillment f where f.warehouse.id = :warehouse", Long.class)
        .setParameter("warehouse", warehouse.id).getSingleResult();
    boolean productAlreadyAtWarehouse = count("warehouse.id = ?1 and product.id = ?2", warehouse.id, product.id) > 0;
    if (!productAlreadyAtWarehouse && warehouseProductCount >= 5)
      throw new WebApplicationException("A warehouse can store at most five product types.", 409);

    Fulfillment fulfillment = new Fulfillment();
    fulfillment.store = store;
    fulfillment.product = product;
    fulfillment.warehouse = warehouse;
    persist(fulfillment);
    return toResponse(fulfillment);
  }

  private static AssignmentResponse toResponse(Fulfillment item) {
    return new AssignmentResponse(item.store.id, item.product.id, item.warehouse.businessUnitCode);
  }
}
