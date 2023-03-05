package nl.itz_kiwisap_.devroom.banplugin.utils;

import nl.itz_kiwisap_.devroom.banplugin.handler.Ban;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.UUID;

public final class MojangFetcher {

    private static final int OK_STATUS = 200;

    private static final HashMap<String, UUID> UUID_CACHE = new HashMap<>();
    private static final HashMap<UUID, String> NAME_CACHE = new HashMap<>();

    private static final String UUID_URL = "https://api.mojang.com/users/profiles/minecraft/";
    private static final String NAME_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";

    public static UUID getUUID(String playerName) {
        if (UUID_CACHE.containsKey(playerName)) {
            return UUID_CACHE.get(playerName);
        }

        UUID uuid;

        try {
            String output = getContentFromSite(UUID_URL + playerName);
            if (output != null) {
                StringBuilder result = new StringBuilder();

                readData(output, result);

                String u = result.toString();
                StringBuilder uuidBuilder = new StringBuilder();

                for (int i = 0; i <= 31; i++) {
                    uuidBuilder.append(u.charAt(i));

                    if (i == 7 || i == 11 || i == 15 || i == 19) {
                        uuidBuilder.append('-');
                    }
                }

                uuid = UUID.fromString(uuidBuilder.toString());
            } else {
                uuid = null;
            }
        } catch (Exception exception) {
            uuid = null;
        }

        UUID_CACHE.put(playerName, uuid);
        return uuid;
    }

    public static String getName(UUID uuid) {
        if (uuid.equals(Ban.CONSOLE_UUID)) return "Console";

        if (NAME_CACHE.containsKey(uuid)) {
            return NAME_CACHE.get(uuid);
        }

        String name = getName(uuid.toString());
        NAME_CACHE.put(uuid, name);
        UUID_CACHE.put(name, uuid);
        return name;
    }

    public static String getName(String uuid) {
        try {
            uuid = uuid.replace("-", "");

            String output = getContentFromSite(NAME_URL + uuid);
            if (output == null) return null;

            StringBuilder result = new StringBuilder();

            for (int i = 0; i < 20000; i++) {
                if (output.charAt(i) == 'n'
                        && output.charAt(i + 1) == 'a'
                        && output.charAt(i + 2) == 'm'
                        && output.charAt(i + 3) == 'e') {

                    for (int k = i + 9; k < 20000; k++) {
                        char current = output.charAt(k);

                        if (current != '"') {
                            result.append(current);
                            continue;
                        }

                        break;
                    }

                    break;
                }
            }

            return result.toString();
        } catch (Exception exception) {
            return null;
        }
    }

    private static String getContentFromSite(String uri) throws IOException {
        URL url = new URL(uri);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        if (connection == null || connection.getInputStream() == null) return null;

        connection.setReadTimeout(60 * 1000);

        int responseCode = connection.getResponseCode();
        if (responseCode != OK_STATUS) return null;

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();

            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null) {
                response.append(inputLine);
            }

            return response.toString();
        }
    }

    private static void readData(String toRead, StringBuilder result) {
        for (int i = toRead.length() - 3; i >= 0; i--) {
            if (toRead.charAt(i) != '"') {
                result.insert(0, toRead.charAt(i));
                continue;
            }

            break;
        }
    }
}