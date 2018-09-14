import Immutable from 'seamless-immutable';
import _ from 'lodash';
import { initialState as policyWizInitialState } from 'admin-source-management/reducers/usm/policy-wizard-reducers';
import { initialState as groupWizInitialState } from 'admin-source-management/reducers/usm/group-wizard-reducers';
import {
  groups,
  policies
} from '../data/data';

const _set = (obj, key, val) => {
  if (obj[key]) {
    obj[key] = val;
    return;
  }

  const keys = key.split('.');
  const firstKey = keys.shift();

  if (!obj[firstKey]) {
    obj[firstKey] = {};
  }

  if (keys.length === 0) {
    obj[firstKey] = val;
    return;
  } else {
    _set(obj[firstKey], keys.join('.'), val);
  }
};

export default class DataHelper {
  constructor(setState) {
    this.state = {};
    this.setState = setState;
  }

  // Trigger setState, also return the resulting state
  // in case it needs to be used/checked
  build() {
    const state = Immutable.from(this.state);
    if (this.setState) {
      this.setState(state);
    }
    return state.asMutable();
  }

  /* TODO is this deprecated? Should this be deleted when policy wizard is done ??? */
  policy(config) {
    _set(this.state, 'usm.policy', config);
    return this;
  }

  /* TODO is this deprecated? Should this be deleted when policy wizard is done ??? */
  fetchPolicyStatus(status) {
    _set(this.state, 'usm.policy.itemsStatus', status);
    return this;
  }

  policyWiz() {
    _set(this.state, 'usm.policyWizard', _.cloneDeep(policyWizInitialState));
    return this;
  }

  policyWizSourceType(type) {
    _set(this.state, 'usm.policyWizard.policy.policyType', type);
    return this;
  }

  policyWizName(name) {
    _set(this.state, 'usm.policyWizard.policy.name', name);
    return this;
  }

  policyWizDescription(description) {
    _set(this.state, 'usm.policyWizard.policy.description', description);
    return this;
  }

  policyWizVisited(visitedFieldsArray) {
    _set(this.state, 'usm.policyWizard.visited', visitedFieldsArray);
    return this;
  }

  policyWizPolicy(policy) {
    _set(this.state, 'usm.policyWizard.policy', policy);
    return this;
  }

  policyWizPolicyStatus(status) {
    _set(this.state, 'usm.policyWizard.policyStatus', status);
    return this;
  }

  policyWizPolicyList(policyList) {
    _set(this.state, 'usm.policyWizard.policyList', policyList);
    return this;
  }

  policyWizPolicyListStatus(status) {
    _set(this.state, 'usm.policyWizard.policyListStatus', status);
    return this;
  }

  policyWizScanStartDate(startDate) {
    _set(this.state, 'usm.policyWizard.policy.scheduleConfig.scheduleOptions.scanStartDate', startDate);
    return this;
  }

  /* TODO is this deprecated? Should this be deleted when group wizard is done ??? */
  group(config) {
    _set(this.state, 'usm.group', config);
    return this;
  }

  /* TODO is this deprecated? Should this be deleted when group wizard is done ??? */
  fetchGroupStatus(status) {
    _set(this.state, 'usm.group.itemsStatus', status);
    return this;
  }

  groupWiz() {
    _set(this.state, 'usm.groupWizard', _.cloneDeep(groupWizInitialState));
    return this;
  }

  groupWizName(name) {
    _set(this.state, 'usm.groupWizard.group.name', name);
    return this;
  }

  groupWizDescription(description) {
    _set(this.state, 'usm.groupWizard.group.description', description);
    return this;
  }

  groupWizVisited(visitedFieldsArray) {
    _set(this.state, 'usm.groupWizard.visited', visitedFieldsArray);
    return this;
  }

  groupWizGroup(group) {
    _set(this.state, 'usm.groupWizard.group', group);
    return this;
  }

  groupWizGroupStatus(status) {
    _set(this.state, 'usm.groupWizard.groupStatus', status);
    return this;
  }

  groupWizGroupList(groupList) {
    _set(this.state, 'usm.groupWizard.groupList', groupList);
    return this;
  }

  groupWizGroupListStatus(status) {
    _set(this.state, 'usm.groupWizard.groupListStatus', status);
    return this;
  }

  fetchGroups() {
    _set(this.state, 'usm.groups.items', groups);
    return this;
  }

  groups(config) {
    _set(this.state, 'usm.groups.items', config);
    return this;
  }

  selectedGroups(config) {
    _set(this.state, 'usm.groups.itemsSelected', config);
    return this;
  }

  fetchPolicies() {
    _set(this.state, 'usm.policies.items', policies);
    return this;
  }

  policies(config) {
    _set(this.state, 'usm.policies.items', config);
    return this;
  }

  selectedPolicies(config) {
    _set(this.state, 'usm.policies.itemsSelected', config);
    return this;
  }

}
