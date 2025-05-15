package com.mediko.mediko_server.global.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;

public class SymptomChecklistDeserializer extends JsonDeserializer<List<Map<String, Object>>> {
    @Override
    public List<Map<String, Object>> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        List<Map<String, Object>> result = new ArrayList<>();
        JsonNode node = p.getCodec().readTree(p);
        ObjectMapper mapper = new ObjectMapper();

        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                Map<String, Object> item = new HashMap<>();

                // Add the condition name as a key
                item.put("condition_name", entry.getKey());

                // Process condition_translation
                JsonNode translationNode = entry.getValue().get("condition_translation");
                if (translationNode != null) {
                    Map<String, String> translation = new HashMap<>();
                    translationNode.fields().forEachRemaining(trans ->
                            translation.put(trans.getKey(), trans.getValue().asText())
                    );
                    item.put("condition_translation", translation);
                }

                // Process symptoms array
                JsonNode symptomsNode = entry.getValue().get("symptoms");
                if (symptomsNode != null && symptomsNode.isArray()) {
                    List<Map<String, String>> symptoms = new ArrayList<>();
                    symptomsNode.forEach(symptom -> {
                        Map<String, String> symptomMap = new HashMap<>();
                        symptom.fields().forEachRemaining(s ->
                                symptomMap.put(s.getKey(), s.getValue().asText())
                        );
                        symptoms.add(symptomMap);
                    });
                    item.put("symptoms", symptoms);
                }

                result.add(item);
            }
        }

        return result;
    }
}
