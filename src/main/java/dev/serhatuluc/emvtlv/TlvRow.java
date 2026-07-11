package dev.serhatuluc.emvtlv;

import dev.serhatuluc.emvtlv.tlv.TlvNode;

/** Flat, display-ready view of a single {@link TlvNode} for the results table. */
public class TlvRow {
    private final String tag;
    private final String name;
    private final String tlvClass;
    private final String type;
    private final int length;
    private final String valueHex;
    private final String valueAscii;
    private final boolean known;
    private final boolean constructed;
    private final int level;

    public TlvRow(TlvNode node, TagRegistry registry) {
        this.tag = "  ".repeat(node.getLevel()) + node.getTag();
        this.known = registry.isKnown(node.getTag());
        this.name = known ? registry.name(node.getTag()) : "(unknown / proprietary tag)";
        this.tlvClass = switch (node.getTagClass()) {
            case UNIVERSAL -> "Universal";
            case APPLICATION -> "Application";
            case CONTEXT_SPECIFIC -> "Context-Specific";
            case PRIVATE -> "Private";
        };
        this.type = node.isConstructed() ? "Constructed" : "Primitive";
        this.length = node.getValue().length;
        this.valueHex = node.isConstructed() ? "" : node.getValueHex();
        this.valueAscii = node.isConstructed() ? "" : node.getValueAscii();
        this.constructed = node.isConstructed();
        this.level = node.getLevel();
    }

    public String getTag() {
        return tag;
    }

    public String getName() {
        return name;
    }

    public String getTlvClass() {
        return tlvClass;
    }

    public String getType() {
        return type;
    }

    public int getLength() {
        return length;
    }

    public String getValueHex() {
        return valueHex;
    }

    public String getValueAscii() {
        return valueAscii;
    }

    public boolean isKnown() {
        return known;
    }

    public boolean isConstructed() {
        return constructed;
    }

    public int getLevel() {
        return level;
    }
}
