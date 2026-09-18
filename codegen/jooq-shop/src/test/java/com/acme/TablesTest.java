package com.acme;

import static com.acme.jooq.Tables.CUSTOMERS;
import static com.acme.jooq.Tables.ORDERS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Test;

class TablesTest {

    @Test
    void migrationsBecomeTypedTables() {
        // DDLDatabase applies the scripts to H2, which folds an unquoted identifier to upper case.
        assertEquals("CUSTOMERS", CUSTOMERS.getName());
        assertEquals("ORDERS", ORDERS.getName());
        assertEquals(Long.class, ORDERS.CUSTOMER_ID.getType());
        assertEquals(BigDecimal.class, ORDERS.TOTAL.getType());
        assertEquals(1, ORDERS.getReferences().size(), "orders.customer_id references customers");
    }

    @Test
    void theJoinRendersFromTheGeneratedMetadata() {
        String sql = DSL.select(CUSTOMERS.EMAIL, ORDERS.TOTAL)
                .from(ORDERS)
                .join(CUSTOMERS)
                .on(ORDERS.CUSTOMER_ID.eq(CUSTOMERS.ID))
                .getSQL();
        assertTrue(sql.contains("join"), sql);
        assertTrue(sql.contains("\"ORDERS\".\"CUSTOMER_ID\" = \"CUSTOMERS\".\"ID\""), sql);
    }
}
