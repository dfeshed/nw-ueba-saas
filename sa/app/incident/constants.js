/**
 * @file Incident constants.
 * Any constant values related to the incident model object
 * @public
 */

export const incPriority = {
  CRITICAL: 3,
  HIGH: 2,
  MEDIUM: 1,
  LOW: 0
};

export const incStatus = {
  NEW: 0,
  ASSIGNED: 1,
  IN_PROGRESS: 2,
  REMEDIATION_REQUESTED: 3,
  REMEDIATION_COMPLETE: 4,
  CLOSED: 5,
  FALSE_POSITIVE: 6
};

export const incidentRiskThreshold = {
  LOW: 30,
  MEDIUM: 50,
  HIGH: 70
};

export const journalMilestones = {
  RECONNAISSANCE: 0,
  DELIVERY: 1,
  EXPLOITATION: 2,
  INSTALLATION: 3,
  COMMAND_AND_CONTROL: 4,
  ACTION_ON_OBJECTIVE: 5,
  CONTAINMENT: 6,
  ERADICATION: 7,
  CLOSURE: 8
};

// returns : {0: 0, 1: 1, 2: 2, 3: 3}
export const incidentPriorityIds =
  Object.keys(incPriority).map((k) => incPriority[k]);

// returns : {0: 0, 1: 1, 2: 2, 3: 3, 4: 4, 5: 5, 6: 6}
export const incidentStatusIds =
  Object.keys(incStatus).map((k) => incStatus[k]);

// returns {0: "NEW", 1: "ASSIGNED", 2: "IN_PROGRESS", 3: "REMEDIATION_REQUESTED", 4: "REMEDIATION_COMPLETE", 5: "CLOSED", 6: "FALSE_POSITIVE"}
export const incidentStatusString =
  Object.keys(incStatus).reduce(function(status, key) {
    status[ incStatus[key] ] = key;
    return status;
  }, {});

// returns {0: "LOW", 1: "MEDIUM", 2: "HIGH", 3: "CRITICAL"}
export const incidentPriorityString =
  Object.keys(incPriority).reduce(function(priority, key) {
    priority[ incPriority[key] ] = key;
    return priority;
  }, {});
