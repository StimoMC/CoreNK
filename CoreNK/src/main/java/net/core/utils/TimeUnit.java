package net.core.utils;

public enum TimeUnit {
    SECOND(new String[]{"s", "sec", "secs", "second", "seconds"}, 1),
    MINUTE(new String[]{"m", "min", "mins", "minute", "minutes"}, 60),
    HOUR(new String[]{"h", "hs", "hour", "hours"}, 60 * 60),
    DAY(new String[]{"d", "ds", "day", "days"}, 60 * 60 * 24),
    MONTH(new String[]{"month"}, 60 * 60 * 24 * 31),
    YEAR(new String[]{"y", "year"}, 60 * 60 * 24 * 365);

    private String[] names;
    private long seconds;

    private TimeUnit(String[] names, long seconds) {
        this.names = names;
        this.seconds = seconds;
    }

    public long getSeconds() {
        return seconds;
    }

    public String[] getNames() {
        return names;
    }

    public static TimeUnit getByString(String str) {
        for (TimeUnit timeUnit : TimeUnit.values()) {
            for (String name : timeUnit.getNames()) {
                if (name.equalsIgnoreCase(str)) {
                    return timeUnit;
                }
            }
        }
        return null;
    }
}