package dev.serhatuluc.emvtlv.tlv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;

/**
 * Full BER-TLV parser: supports multi-byte tags (continuation bits) and long-form lengths
 * (0x80+ prefix), unlike a naive single-byte-length reader.
 */
public final class TlvParser {

    private TlvParser() {
    }

    public static List<TlvNode> parse(byte[] data) {
        return parseLevel(data, 0);
    }

    private static List<TlvNode> parseLevel(byte[] data, int level) {
        List<TlvNode> nodes = new ArrayList<>();
        int pos = 0;
        while (pos < data.length) {
            int tagStart = pos;
            byte firstByte = data[pos];
            List<Byte> tagBytes = new ArrayList<>();
            tagBytes.add(firstByte);
            pos++;

            boolean constructed = (firstByte & 0x20) != 0;
            TlvClass tagClass = switch (firstByte & 0xC0) {
                case 0x00 -> TlvClass.UNIVERSAL;
                case 0x40 -> TlvClass.APPLICATION;
                case 0x80 -> TlvClass.CONTEXT_SPECIFIC;
                default -> TlvClass.PRIVATE;
            };

            if ((firstByte & 0x1F) == 0x1F) {
                // multi-byte tag number: continues while the high bit of each following byte is set
                boolean cont = true;
                while (cont && pos < data.length) {
                    byte b = data[pos];
                    tagBytes.add(b);
                    pos++;
                    cont = (b & 0x80) != 0;
                }
            }

            if (pos >= data.length) {
                throw new TlvParseException(
                        "Unexpected end of data: no bytes left while reading tag (offset " + tagStart + ").");
            }

            byte lengthByte = data[pos];
            pos++;
            int length;
            if ((lengthByte & 0x80) == 0) {
                length = lengthByte;
            } else {
                int numLenBytes = lengthByte & 0x7F;
                if (numLenBytes == 0) {
                    throw new TlvParseException("Unsupported indefinite length form (offset " + (pos - 1) + ").");
                }
                if (pos + numLenBytes > data.length) {
                    throw new TlvParseException("Length bytes exceed end of data (offset " + (pos - 1) + ").");
                }
                length = 0;
                for (int i = 0; i < numLenBytes; i++) {
                    length = (length << 8) | (data[pos] & 0xFF);
                    pos++;
                }
            }

            if (pos + length > data.length) {
                byte[] tagArray = toByteArray(tagBytes);
                throw new TlvParseException("Tag " + HexFormat.of().withUpperCase().formatHex(tagArray)
                        + " value length (" + length + ") exceeds remaining data (" + (data.length - pos)
                        + ") (offset " + pos + ").");
            }

            byte[] value = Arrays.copyOfRange(data, pos, pos + length);
            pos += length;

            TlvNode node = new TlvNode();
            node.setTag(HexFormat.of().withUpperCase().formatHex(toByteArray(tagBytes)));
            node.setTagClass(tagClass);
            node.setConstructed(constructed);
            node.setValue(value);
            node.setLevel(level);

            if (constructed && value.length > 0) {
                try {
                    node.getChildren().addAll(parseLevel(value, level + 1));
                } catch (TlvParseException ex) {
                    // value doesn't actually contain nested TLV despite the constructed bit; keep as raw
                }
            }

            nodes.add(node);
        }

        return nodes;
    }

    public static List<TlvNode> flatten(List<TlvNode> nodes) {
        List<TlvNode> result = new ArrayList<>();
        flattenInto(nodes, result);
        return result;
    }

    private static void flattenInto(List<TlvNode> nodes, List<TlvNode> result) {
        for (TlvNode n : nodes) {
            result.add(n);
            flattenInto(n.getChildren(), result);
        }
    }

    private static byte[] toByteArray(List<Byte> bytes) {
        byte[] result = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            result[i] = bytes.get(i);
        }
        return result;
    }
}
