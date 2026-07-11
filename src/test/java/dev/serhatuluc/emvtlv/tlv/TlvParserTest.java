package dev.serhatuluc.emvtlv.tlv;

import org.junit.jupiter.api.Test;

import java.util.HexFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TlvParserTest {

    @Test
    void parsesSimplePrimitiveTag() {
        // Tag 9F02 (Amount, Authorised), length 06, value 000000001000
        byte[] data = HexFormat.of().parseHex("9F0206000000001000");
        List<TlvNode> nodes = TlvParser.parse(data);

        assertEquals(1, nodes.size());
        TlvNode node = nodes.get(0);
        assertEquals("9F02", node.getTag());
        assertEquals(TlvClass.CONTEXT_SPECIFIC, node.getTagClass());
        assertEquals(false, node.isConstructed());
        assertEquals(6, node.getValue().length);
        assertEquals("000000001000", node.getValueHex());
    }

    @Test
    void parsesConstructedTagWithNestedChildren() {
        // Tag 70 (READ RECORD Response Template, constructed), containing 5A (PAN) and 5F24 (Expiry)
        byte[] data = HexFormat.of().parseHex("70105A0841111111111111155F2403251231");
        List<TlvNode> nodes = TlvParser.parse(data);

        assertEquals(1, nodes.size());
        TlvNode root = nodes.get(0);
        assertEquals("70", root.getTag());
        assertTrue(root.isConstructed());
        assertEquals(2, root.getChildren().size());
        assertEquals("5A", root.getChildren().get(0).getTag());
        assertEquals("5F24", root.getChildren().get(1).getTag());

        List<TlvNode> flat = TlvParser.flatten(nodes);
        assertEquals(3, flat.size()); // root + 2 children
    }

    @Test
    void parsesLongFormLength() {
        // Tag DF01 (proprietary), length encoded as 0x81 0x81 (long form: 1 length byte, value=0x81=129 bytes)
        byte[] value = new byte[129];
        byte[] header = HexFormat.of().parseHex("DF018181");
        byte[] data = new byte[header.length + value.length];
        System.arraycopy(header, 0, data, 0, header.length);
        System.arraycopy(value, 0, data, header.length, value.length);

        List<TlvNode> nodes = TlvParser.parse(data);
        assertEquals(1, nodes.size());
        assertEquals(129, nodes.get(0).getValue().length);
    }

    @Test
    void throwsOnTruncatedData() {
        // Tag 9F02, length 06, but only 3 bytes of value present
        byte[] data = HexFormat.of().parseHex("9F0206000000");
        assertThrows(TlvParseException.class, () -> TlvParser.parse(data));
    }

    @Test
    void throwsOnDataEndingMidTag() {
        byte[] data = HexFormat.of().parseHex("9F");
        assertThrows(TlvParseException.class, () -> TlvParser.parse(data));
    }
}
