package disabler.opennbt.conversion.builtin;

import disabler.opennbt.conversion.ConverterRegistry;
import disabler.opennbt.conversion.TagConverter;
import disabler.opennbt.tag.builtin.CompoundTag;
import disabler.opennbt.tag.builtin.Tag;

import java.util.HashMap;
import java.util.Map;

/**
 * A converter that converts between CompoundTag and Map.
 */
public class CompoundTagConverter implements TagConverter<CompoundTag, Map> {
    @Override
    public Map convert(CompoundTag tag) {
        Map<String, Object> ret = new HashMap<String, Object>();
        Map<String, Tag> tags = tag.getValue();
        for (String name : tags.keySet()) {
            Tag t = tags.get(name);
            ret.put(t.getName(), ConverterRegistry.convertToValue(t));
        }

        return ret;
    }

    @Override
    public CompoundTag convert(String name, Map value) {
        Map<String, Tag> tags = new HashMap<String, Tag>();
        for (Object na : value.keySet()) {
            String n = (String) na;
            tags.put(n, ConverterRegistry.convertToTag(n, value.get(n)));
        }

        return new CompoundTag(name, tags);
    }
}
