package fortscale.domain.core.ioc;

public enum Tactic {
    INITIAL_ACCESS,
    PERSISTENCE,
    PRIVILEGE_ESCALATION,
    DEFENSE_EVASION,
    CREDENTIAL_ACCESS,
    DISCOVERY,
    LATERAL_MOVEMENT,
    EXECUTION,
    COLLECTION,
    EXFILTRATION,
    COMMAND_AND_CONTROL;



    public static Tactic getTactic(String tactic) {
        return Tactic.valueOf(tactic.toUpperCase());
    }
}
