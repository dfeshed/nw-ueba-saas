package presidio.data.domain;

import java.util.Arrays;

public class IocEntity {

    public enum IOC_LEVEL {
        LOW("LOW"),
        MEDIUM("MEDIUM"),
        HIGH("HIGH"),
        CRITICAL("CRITICAL");

        public final String value;

        IOC_LEVEL(String value) {
            this.value = value;
        }

        public static String[] getNames(Class<? extends Enum<?>> e) {
            return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
        }
    }


    public enum IOC_TACTIC {
        MALWARE("MALWARE"),
        PHISHING("PHISHING"),
        SQL_INJECTION("SQL_INJECTION"),
        TACTIC_A("TACTIC_A"),
        TACTIC_B("TACTIC_B");

        public final String value;

        IOC_TACTIC(String value) {
            this.value = value;
        }

        public static String[] getNames(Class<? extends Enum<?>> e) {
            return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
        }
    }

    String name;
    String tactic;
    String level;

    public IocEntity(String name, String tactic, String level) {
        this.name = name;
        this.tactic = tactic;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTactic() {
        return tactic;
    }

    public void setTactic(String tactic) {
        this.tactic = tactic;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "IocEntity{" +
                "name='" + name + '\'' +
                ", tactic='" + tactic + '\'' +
                ", level='" + level + '\'' +
                '}';
    }
}
