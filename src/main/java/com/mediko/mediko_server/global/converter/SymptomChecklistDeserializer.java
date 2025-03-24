package com.mediko.mediko_server.global.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.*;

public class SymptomChecklistDeserializer extends JsonDeserializer<List<Map<String, Object>>> {
    @Override
    public List<Map<String, Object>> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        List<Map<String, Object>> result = new ArrayList<>();
        JsonNode node = p.getCodec().readTree(p);

        if (node.isObject()) {  // 객체인 경우도 처리
            Map<String, Object> item = new HashMap<>();
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                item.put(entry.getKey(), entry.getValue().asText());
            }
            result.add(item);
        } else if (node.isArray()) {  // 배열인 경우
            for (JsonNode item : node) {
                Map<String, Object> mapItem = new HashMap<>();
                Iterator<Map.Entry<String, JsonNode>> fields = item.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> entry = fields.next();
                    if (entry.getValue().isObject()) {
                        Map<String, Object> nestedMap = new HashMap<>();
                        Iterator<Map.Entry<String, JsonNode>> nestedFields = entry.getValue().fields();
                        while (nestedFields.hasNext()) {
                            Map.Entry<String, JsonNode> nestedEntry = nestedFields.next();
                            nestedMap.put(nestedEntry.getKey(), nestedEntry.getValue().asText());
                        }
                        mapItem.put(entry.getKey(), nestedMap);
                    } else if (entry.getValue().isArray()) {
                        List<Map<String, String>> nestedList = new ArrayList<>();
                        for (JsonNode arrayItem : entry.getValue()) {
                            Map<String, String> nestedMap = new HashMap<>();
                            Iterator<Map.Entry<String, JsonNode>> arrayFields = arrayItem.fields();
                            while (arrayFields.hasNext()) {
                                Map.Entry<String, JsonNode> arrayEntry = arrayFields.next();
                                nestedMap.put(arrayEntry.getKey(), arrayEntry.getValue().asText());
                            }
                            nestedList.add(nestedMap);
                        }
                        mapItem.put(entry.getKey(), nestedList);
                    } else {
                        mapItem.put(entry.getKey(), entry.getValue().asText());
                    }
                }
                result.add(mapItem);
            }
        }
        return result;
    }
}
