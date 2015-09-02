package com.rsa.asoc.sa.ui.threat.domain.bean;

/**
 * Kill Chain and Remediation milestones for a journal entry
 *
 * @author Jay Garala
 * @since 10.6.0
 */
public enum InvestigationMilestone {
    /**
     * Identification, selection and research on targets
     */
    RECONNAISSANCE,

    /**
     * Transmission of the threat to the target
     */
    DELIVERY,

    /**
     * Methods used to establish a foothold into the target's environment
     */
    EXPLOITATION,

    /**
     * Control one or more hosts within the target's organization from an outside their network
     */
    INSTALLATION,

    /**
     * Interact with one or more compromised resources in the organization
     */
    COMMAND_AND_CONTROL,

    /**
     * Attacker has accomplished their purpose of the attack
     */
    ACTION_ON_OBJECTIVE,

    /**
     * Target organization determines the depth of the compromise and isolates the affected the systems
     */
    CONTAINMENT,

    /**
     * Organization's plan of execution to remove the threat from the compromised systems
     */
    ERADICATION,

    /**
     * Organization reviews and updates procedures and policies to prevent another attack
     */
    CLOSURE
}
