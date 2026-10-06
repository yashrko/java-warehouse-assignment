package com.fulfilment.application.monolith.location;

import static org.junit.jupiter.api.Assertions.*;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import org.junit.jupiter.api.Test;

public class LocationGatewayTest {

  @Test
  public void testWhenResolveExistingLocationShouldReturn() {
    Location location = new LocationGateway().resolveByIdentifier("ZWOLLE-001");
    assertNotNull(location);
    assertEquals("ZWOLLE-001", location.identification);
    assertEquals(1, location.maxNumberOfWarehouses);
    assertEquals(40, location.maxCapacity);
  }

  @Test void returnsNullForUnknownOrBlankIdentifier() {
    LocationGateway gateway = new LocationGateway();
    assertNull(gateway.resolveByIdentifier("not-a-location"));
    assertNull(gateway.resolveByIdentifier("  "));
    assertEquals("AMSTERDAM-001", gateway.resolveByIdentifier(" amsterdam-001 ").identification);
  }
}
