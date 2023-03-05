package nl.itz_kiwisap_.devroom.banplugin.utils;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TimeUtils {

    private static final Pattern TIME_PATTERN = Pattern.compile("(?:([0-9]+)y[,\\s]*)?" + "(?:([0-9]+)mo[,\\s]*)?" + "(?:([0-9]+)w[,\\s]*)?" + "(?:([0-9]+)d[,\\s]*)?" + "(?:([0-9]+)h[,\\s]*)?" + "(?:([0-9]+)m[,\\s]*)?" + "(?:([0-9]+)s[,\\s]*)?", Pattern.CASE_INSENSITIVE);

    public static Duration stringToDuration(String input) throws Exception {
        Matcher matcher = TIME_PATTERN.matcher(input);
        int[] timePattern = new int[7];
        boolean found = false;

        while (matcher.find()) {
            String matcherGroup = matcher.group();
            if (matcherGroup == null || matcherGroup.isEmpty()) continue;

            for (int i = 0; i < matcher.groupCount(); i++) {
                String group = matcher.group(i);
                if (group == null || group.isEmpty()) continue;

                found = true;
                break;
            }

            if (found) {
                for (int i = 1; i <= timePattern.length; i++) {
                    String group = matcher.group(i);
                    if (group == null || group.isEmpty()) continue;

                    timePattern[i - 1] = Integer.parseInt(group);
                }

                break;
            }
        }

        if (!found) {
            throw new Exception("illegalDate");
        }

        return Duration.ofSeconds(
                timePattern[6]
                        + (timePattern[5] * 60L)
                        + (timePattern[4] * 3_600L)
                        + (timePattern[3] * 86_400L)
                        + (timePattern[2] * 604_800L)
                        + (timePattern[1] * 2_592_000L)
                        + (timePattern[0] * 31_536_000L)
        );
    }

    public static String formatDate(Long date) {
        Instant instance = Instant.ofEpochMilli(date);
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instance, ZoneId.systemDefault());
        String year = zonedDateTime.format(DateTimeFormatter.ofPattern("u"));
        String month = zonedDateTime.format(DateTimeFormatter.ofPattern("M"));
        String day = zonedDateTime.format(DateTimeFormatter.ofPattern("d"));
        String time = zonedDateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        return day + "-" + month + "-" + year + " " + time;
    }

    public static String formatDuration(Duration duration) {
        long millis = duration.toMillis();

        if (millis < 1000L) {
            return String.format("0%s", "s");
        } else {
            String[] units = new String[]{"d", "h", "m", "s"};
            long[] times = new long[4];
            times[0] = TimeUnit.DAYS.convert(millis, TimeUnit.MILLISECONDS);
            millis -= TimeUnit.MILLISECONDS.convert(times[0], TimeUnit.DAYS);
            times[1] = TimeUnit.HOURS.convert(millis, TimeUnit.MILLISECONDS);
            millis -= TimeUnit.MILLISECONDS.convert(times[1], TimeUnit.HOURS);
            times[2] = TimeUnit.MINUTES.convert(millis, TimeUnit.MILLISECONDS);
            millis -= TimeUnit.MILLISECONDS.convert(times[2], TimeUnit.MINUTES);
            times[3] = TimeUnit.SECONDS.convert(millis, TimeUnit.MILLISECONDS);
            StringBuilder s = new StringBuilder();

            for (int i = 0; i < 4; ++i) {
                if (times[i] > 0L) {
                    s.append(String.format("%d%s, ", times[i], units[i]));
                }
            }

            return s.substring(0, s.length() - 2);
        }
    }
}
