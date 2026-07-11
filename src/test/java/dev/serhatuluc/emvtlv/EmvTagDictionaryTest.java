package dev.serhatuluc.emvtlv;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmvTagDictionaryTest {

    @Test
    void knownTagIsCaseInsensitive() {
        assertTrue(EmvTagDictionary.isKnown("5A"));
        assertTrue(EmvTagDictionary.isKnown("5a"));
        assertEquals(EmvTagDictionary.name("5A"), EmvTagDictionary.name("5a"));
    }

    @Test
    void unknownTagReturnsFalse() {
        assertFalse(EmvTagDictionary.isKnown("FF99"));
    }
}
