package dev.serhatuluc.emvtlv;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TagRegistryTest {

    @Test
    void fallsBackToBuiltInTagsWhenNoCustomOnesGiven() {
        TagRegistry registry = new TagRegistry(Map.of());
        assertTrue(registry.isKnown("5A"));
        assertEquals(EmvTagDictionary.name("5A"), registry.name("5A"));
    }

    @Test
    void addsBrandNewCustomTag() {
        TagRegistry registry = new TagRegistry(Map.of("FFFF", "Custom tag"));
        assertTrue(registry.isKnown("FFFF"));
        assertEquals("Custom tag", registry.name("FFFF"));
        assertEquals(1, registry.customTagCount());
    }

    @Test
    void customTagOverridesBuiltInName() {
        TagRegistry registry = new TagRegistry(Map.of("5A", "Custom: Card Number"));
        assertEquals("Custom: Card Number", registry.name("5A"));
    }

    @Test
    void unknownTagStaysUnknown() {
        TagRegistry registry = new TagRegistry(Map.of());
        assertFalse(registry.isKnown("ZZZZ"));
    }
}
