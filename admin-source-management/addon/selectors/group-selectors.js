import reselect from 'reselect';
import { isBlank } from '@ember/utils';

const { createSelector } = reselect;

const groupState = (state) => state.usm.group;

/**
 * the group object to be created/updated/saved
 * @public
 */
export const group = createSelector(
  groupState,
  (groupState) => groupState.group
);

/**
 * all available osType objects
 * @public
 */
export const osTypes = createSelector(
  groupState,
  (groupState) => groupState.osTypes
);

/**
 * we need selected osType objects, but group.osTypes only has ID's,
 * so we'll find osType objects by the ID's for selected osTypes
 * @public
 */
export const selectedOsTypes = createSelector(
  group, osTypes,
  (group, osTypes) => {
    const selected = [];
    for (let t = 0; t < osTypes.length; t++) {
      const osType = osTypes[t];
      for (let i = 0; i < group.osTypes.length; i++) {
        const groupOsTypeId = group.osTypes[i];
        if (osType.id === groupOsTypeId) {
          selected.push(osType);
          break;
        }
      }
    }
    return selected;
  }
);

/**
 * available osDescriptions for the selected osTypes
 * @public
 */
export const osDescriptions = createSelector(
  selectedOsTypes,
  (selectedOsTypes) => {
    let available = [];
    for (let i = 0; i < selectedOsTypes.length; i++) {
      const selectedOsType = selectedOsTypes[i];
      available = available.concat(selectedOsType.osDescriptions);
    }
    return available;
  }
);

/**
 * we need selected osDescription objects, but group.osDescriptions only has ID's,
 * so we'll find osDescription objects by the ID's for selected osDescriptions
 * @public
 */
export const selectedOsDescriptions = createSelector(
  group, osDescriptions,
  (group, osDescriptions) => {
    const selected = [];
    for (let d = 0; d < osDescriptions.length; d++) {
      const osDescription = osDescriptions[d];
      for (let i = 0; i < group.osDescriptions.length; i++) {
        const groupOsDescriptionId = group.osDescriptions[i];
        if (osDescription.id === groupOsDescriptionId) {
          selected.push(osDescription);
          break;
        }
      }
    }
    return selected;
  }
);

/**
 * the policies objects to fill the policy select/dropdown
 * @public
 */
export const policies = createSelector(
  groupState,
  (groupState) => groupState.policies
);

/**
 * we need the selected policy object, but group.policy has a map of { 'type': 'policyID' } ( ex. { 'edrPolicy': 'id_abc123' } ),
 * so we'll find the policy object by the type:ID map for the selected policy
 * @public
 */
export const selectedPolicy = createSelector(
  group, policies,
  (group, policies) => {
    let selected = null;
    if (!group.policy) {
      return selected;
    }
    for (let p = 0; p < policies.length; p++) {
      const policy = policies[p];
      const groupPolicyId = group.policy[policy.type];
      if (policy.id === groupPolicyId) {
        selected = policy;
        break;
      }
    }
    return selected;
  }
);

export const isGroupLoading = createSelector(
  groupState,
  (groupState) => {
    return groupState.groupSaveStatus === 'wait' ||
      groupState.initGroupFetchPoliciesStatus === 'wait';
  }
);

export const hasMissingRequiredData = createSelector(
  group,
  (group) => {
    return isBlank(group.name);
  }
);
