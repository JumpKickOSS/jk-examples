package com.acme;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.acme.events.Role;
import com.acme.events.UserCreated;
import org.junit.jupiter.api.Test;

class UserCreatedTest {

    @Test
    void generatedRecordRoundTripsThroughAvroBinary() throws Exception {
        UserCreated event = UserCreated.newBuilder()
                .setId(7L)
                .setEmail("ada@acme.com")
                .setRole(Role.ADMIN)
                .build();

        UserCreated back = UserCreated.fromByteBuffer(event.toByteBuffer());

        assertEquals(event, back);
        assertEquals(String.class, back.getEmail().getClass());
    }

    @Test
    void schemaCarriesTheDeclaredNamespaceAndFields() {
        assertEquals("com.acme.events", UserCreated.getClassSchema().getNamespace());
        assertEquals(3, UserCreated.getClassSchema().getFields().size());
        assertEquals(2, Role.getClassSchema().getEnumSymbols().size());
    }
}
