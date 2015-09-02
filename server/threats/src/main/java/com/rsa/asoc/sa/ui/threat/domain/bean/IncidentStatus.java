package com.rsa.asoc.sa.ui.threat.domain.bean;

/**
 * Represents the workflow stage the Incident response process.
 *
 * @author Jay Garala
 * @since 10.6.0
 */
public enum IncidentStatus {

    /** new incident, no work started yet */
    NEW,

    /** assigned to a user */
    ASSIGNED,

    /** work has started on the incident response */
    IN_PROGRESS,

    /** some remediation tasks have been requested */
    REMEDIATION_REQUESTED,

    /** all remediation tasks are complete */
    REMEDIATION_COMPLETE,

    /** incident response workflow is complete */
    CLOSED,

    /** incident response workflow is complete - false positive */
    CLOSED_FALSE_POSITIVE

}
