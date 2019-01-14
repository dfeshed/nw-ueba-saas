import reselect from 'reselect';
import { isPresent } from '@ember/utils';

const { createSelector } = reselect;

const _policiesState = (state) => state.usm.policies;
export const policies = (state) => state.usm.policies.items;
export const selectedPolicies = (state) => state.usm.policies.itemsSelected;
export const focusedPolicy = (state) => state.usm.policies.focusedItem;

export const listOfEndpoints = (state) => state.usm.policyWizard.listOfEndpointServers || [];
export const listOfLogServers = (state) => state.usm.policyWizard.listOfLogServers || [];

export const isPoliciesLoading = createSelector(
  _policiesState,
  (_policiesState) => _policiesState.itemsStatus === 'wait'
);

export const selectedEditItem = createSelector(
  selectedPolicies, policies,
  (items, all) => {
    // edit disabled for default windows log policies for 11.3
    if (isPresent(items) && items.length == 1 && all.findBy('id', items[0]).id !== '__default_windows_log_policy') {
      const [item] = items;
      return item;
    } else {
      return 'none';
    }
  }
);

export const hasSelectedEditItem = createSelector(
  selectedEditItem,
  (item) => {
    return (isPresent(item) && (item !== 'none'));
  }
);

export const selectedDeleteItems = createSelector(
  selectedPolicies, policies,
  (items, all) => {
    if (items && all) {
      return items.filter((selected) => !all.findBy('id', selected).defaultPolicy);
    }
  }
);

export const hasSelectedDeleteItems = createSelector(
  selectedDeleteItems,
  (items) => {
    if (items) {
      return (items.length > 0);
    }
  }
);

export const selectedPublishItems = createSelector(
  selectedPolicies, policies,
  (items, all) => {
    if (items && all) {
      return items.filter((selected) => all.findBy('id', selected).dirty);
    }
  }
);

export const hasSelectedPublishItems = createSelector(
  selectedPublishItems,
  (items) => {
    if (items) {
      return (items.length > 0);
    }
  }
);

// ==================================
//   filters
// ==================================

// the summary list of policies objects to build the source type filter
const availablePolicySourceTypes = createSelector(
  _policiesState,
  (_policiesState) => {
    const policyList = _policiesState.items;
    const list = [];
    for (let index = 0; index < policyList.length; index++) {
      const sourceType = policyList[index].policyType;
      if (!list.includes(sourceType)) {
        list.push(sourceType);
      }
    }
    return list;
  }
);

const sourceTypeFilterConfig = createSelector(
  availablePolicySourceTypes,
  (sourceTypes) => {
    const config = {
      name: 'sourceType',
      label: 'adminUsm.policies.filter.sourceType',
      listOptions: [],
      type: 'list'
    };
    for (let i = 0; i < sourceTypes.length; i++) {
      const sourceType = sourceTypes[i];
      config.listOptions.push({
        name: sourceType,
        label: `adminUsm.policyTypes.${sourceType}`
      });
    }
    return config;
  }
);

const publishStatusFilterConfig = {
  'name': 'publishStatus',
  'label': 'adminUsm.policies.list.publishStatus',
  'listOptions': [
    // policy.lastPublishedOn > 0 ???
    { name: 'published', label: 'adminUsm.publishStatus.published' },
    // policy.lastPublishedOn === 0
    { name: 'unpublished', label: 'adminUsm.publishStatus.unpublished' },
    // policy.dirty === true
    { name: 'unpublished_edits', label: 'adminUsm.publishStatus.unpublishedEdits' }
  ],
  type: 'list'
};

let sourceTypeConfigCache = null;
export const filterTypesConfig = createSelector(
  sourceTypeFilterConfig,
  (sourceTypeConfig) => {
    // only set the sourceTypeConfigCache if unset, or the first time we have list option values...
    // this avoids re-building & re-rendering every time the manage policies screen is refreshed,
    // which we don't need to do since there will always be at least one of each policy type (a.k.a the default policies)
    if (!sourceTypeConfigCache || sourceTypeConfigCache.listOptions.length === 0) {
      sourceTypeConfigCache = sourceTypeConfig;
    }
    const configs = [sourceTypeConfigCache, publishStatusFilterConfig];
    return configs;
  }
);