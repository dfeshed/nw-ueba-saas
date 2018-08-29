import Immutable from 'seamless-immutable';
import _ from 'lodash';
import { initialState as policyWizInitialState } from 'admin-source-management/reducers/usm/policy-wizard-reducers';
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

  policy(config) {
    _set(this.state, 'usm.policy', config);
    return this;
  }

  fetchPolicyStatus(status) {
    _set(this.state, 'usm.policy.itemsStatus', status);
    return this;
  }

  policyWiz() {
    _set(this.state, 'usm.policyWizard', _.cloneDeep(policyWizInitialState));
    return this;
  }

  policyWizSourceType(type) {
    _set(this.state, 'usm.policyWizard.policy.type', type);
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

  getGroups() {
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

  getPolicies() {
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
