package dev.worldloom.simplelocks.lock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class SimpleLocksMessagesTest {
    @Test
    void enUsTranslationsContainAllMessageKeys() {
        InputStream stream = getClass().getResourceAsStream("/assets/simplelocks/lang/en_us.json");
        assertNotNull(stream);

        JsonObject translations = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();

        assertEquals("Container locked.", translations.get(SimpleLocksMessages.LOCKED).getAsString());
        assertEquals("Container unlocked.", translations.get(SimpleLocksMessages.UNLOCKED).getAsString());
        assertEquals("This container is locked.", translations.get(SimpleLocksMessages.NOT_OWNER).getAsString());
        assertEquals("This container is already locked.", translations.get(SimpleLocksMessages.ALREADY_LOCKED).getAsString());
        assertEquals("This container has invalid lock data.", translations.get(SimpleLocksMessages.INVALID_LOCK).getAsString());
        assertEquals("This item must be named Storage Key.", translations.get(SimpleLocksMessages.INVALID_KEY).getAsString());
    }
}
