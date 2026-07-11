package dev.serhatuluc.emvtlv;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Combines the built-in EMVCo standard tag names ({@link EmvTagDictionary}) with user-supplied
 * additions/overrides loaded from {@code custom-tags.json} (see {@link CustomTagLoader}). Custom
 * entries win on conflict, so a proprietary or Turkish-translated meaning can override the
 * standard English one for a given tag.
 */
public class TagRegistry {

    private final Map<String, String> merged;
    private final int customCount;

    public TagRegistry(Map<String, String> customTags) {
        Map<String, String> combined = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        combined.putAll(EmvTagDictionary.NAMES);
        combined.putAll(customTags);
        this.merged = combined;
        this.customCount = customTags.size();
    }

    public static TagRegistry loadDefault() {
        CustomTagLoader.LoadResult result = CustomTagLoader.load();
        return new TagRegistry(result.tags());
    }

    public boolean isKnown(String tag) {
        return merged.containsKey(tag);
    }

    public String name(String tag) {
        return merged.get(tag);
    }

    public int customTagCount() {
        return customCount;
    }
}
