package com.acme;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.acme.order.ObjectFactory;
import com.acme.order.Order;
import jakarta.xml.bind.JAXBContext;
import java.io.StringReader;
import java.io.StringWriter;
import org.junit.jupiter.api.Test;

class OrderTest {

    @Test
    void generatedOrderRoundTripsThroughXml() throws Exception {
        Order order = new ObjectFactory().createOrder();
        order.setId("A-1");
        order.setQuantity(3);

        JAXBContext context = JAXBContext.newInstance(Order.class);
        StringWriter xml = new StringWriter();
        context.createMarshaller().marshal(order, xml);
        assertTrue(xml.toString().contains("xmlns=\"http://acme.com/order\""), xml.toString());

        Order back = (Order) context.createUnmarshaller().unmarshal(new StringReader(xml.toString()));
        assertEquals("A-1", back.getId());
        assertEquals(3, back.getQuantity());
    }
}
