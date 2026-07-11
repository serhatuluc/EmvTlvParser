package dev.serhatuluc.emvtlv.tlv;

import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

public class TlvNode {
    private String tag = "";
    private TlvClass tagClass;
    private boolean constructed;
    private byte[] value = new byte[0];
    private int level;
    private final List<TlvNode> children = new ArrayList<>();

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public TlvClass getTagClass() {
        return tagClass;
    }

    public void setTagClass(TlvClass tagClass) {
        this.tagClass = tagClass;
    }

    public boolean isConstructed() {
        return constructed;
    }

    public void setConstructed(boolean constructed) {
        this.constructed = constructed;
    }

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<TlvNode> getChildren() {
        return children;
    }

    public String getValueHex() {
        return HexFormat.of().withUpperCase().formatHex(value);
    }

    public String getValueAscii() {
        StringBuilder sb = new StringBuilder();
        for (byte b : value) {
            int c = b & 0xFF;
            sb.append(c >= 0x20 && c <= 0x7E ? (char) c : '.');
        }
        return sb.toString();
    }
}
