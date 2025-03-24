package com.mediko.mediko_server.global.converter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;
import java.util.*;

@JsonDeserialize(using = PossibleConditionsDeserializer.class)
public class PossibleConditionsDeserializer extends JsonDeserializer<List<Map<String, String>>> {
    @Override
    public List<Map<String, String>> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        List<Map<String, String>> result = new ArrayList<>();
        JsonNode node = p.getCodec().readTree(p);
     if (node.isArray()) {
            for (JsonNode condition : node) {
                JsonNode conditionObj = condition.get("condition");
                if (conditionObj != null) {
                    Map<String, String> languageMap = new HashMap<>();
                    Iterator<Map.Entry<String, JsonNode>> fields = conditionObj.fields();
                    while (fields.hasNext()) {
                        Map.Entry<String, JsonNode> entry = fields.next();
                        languageMap.put(entry.getKey(), entry.getValue().asText());
                    }
                    result.add(languageMap);
                }
            }
        }
        return result;
    }
}
