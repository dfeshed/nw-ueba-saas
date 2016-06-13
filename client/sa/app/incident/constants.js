/**
 * @file Incident constants.
 * Any constant values related to the incident model object
 * @public
 */

export const incPriority = {
  LOW: 0,
  MEDIUM: 1,
  HIGH: 2,
  CRITICAL: 3
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

export const incidentPriorityIds =
  Object.keys(incPriority).map((k) => incPriority[k]);

export const incidentStatusIds =
  Object.keys(incStatus).map((k) => incStatus[k]);
