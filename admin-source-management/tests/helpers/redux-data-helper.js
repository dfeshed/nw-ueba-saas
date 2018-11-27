import Immutable from 'seamless-immutable';
import _ from 'lodash';
import { buildInitialState as policyWizInitialState } from 'admin-source-management/reducers/usm/policy-wizard/policy-wizard-reducers';
import { initialState as groupWizInitialState } from 'admin-source-management/reducers/usm/group-wizard-reducers';
import {
  groups,
  policies,
  endpointServers,
  logServers
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

  policyWiz(policyType = 'edrPolicy') {
    _set(this.state, 'usm.policyWizard', _.cloneDeep(policyWizInitialState(Immutable.from({}), policyType)));
    return this;
  }

  policyWizSelectedSettings(selectedSettings) {
    _set(this.state, 'usm.policyWizard.selectedSettings', selectedSettings);
    return this;
  }

  policyWizPolicy(policy, updateOrig) {
    _set(this.state, 'usm.policyWizard.policy', policy);
    if (updateOrig) {
      _set(this.state, 'usm.policyWizard.policyOrig', policy);
    }
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

  policyWizScanType(scanType) {
    _set(this.state, 'usm.policyWizard.policy.scanType', scanType);
    return this;
  }

  policyWizScanStartDate(scanStartDate) {
    _set(this.state, 'usm.policyWizard.policy.scanStartDate', scanStartDate);
    return this;
  }

  policyWizScanStartTime(scanStartTime) {
    _set(this.state, 'usm.policyWizard.policy.scanStartTime', scanStartTime);
    return this;
  }

  policyWizRecurrenceInterval(recurrenceInterval) {
    _set(this.state, 'usm.policyWizard.policy.recurrenceInterval', recurrenceInterval);
    return this;
  }

  policyWizRecurrenceUnit(recurrenceUnit) {
    _set(this.state, 'usm.policyWizard.policy.recurrenceUnit', recurrenceUnit);
    return this;
  }

  policyWizCaptureFloatingCode(captureFloatingCode) {
    _set(this.state, 'usm.policyWizard.policy.captureFloatingCode', captureFloatingCode);
    return this;
  }

  policyWizDownloadMbr(downloadMbr) {
    _set(this.state, 'usm.policyWizard.policy.downloadMbr', downloadMbr);
    return this;
  }

  policyWizFilterSignedHooks(filterSignedHooks) {
    _set(this.state, 'usm.policyWizard.policy.filterSignedHooks', filterSignedHooks);
    return this;
  }

  policyWizRequestScanOnRegistration(requestScanOnRegistration) {
    _set(this.state, 'usm.policyWizard.policy.requestScanOnRegistration', requestScanOnRegistration);
    return this;
  }

  policyWizBlockingEnabled(blockingEnabled) {
    _set(this.state, 'usm.policyWizard.policy.blockingEnabled', blockingEnabled);
    return this;
  }

  policyWizPrimaryAddress(primaryAddress) {
    _set(this.state, 'usm.policyWizard.policy.primaryAddress', primaryAddress);
    return this;
  }

  policyWizAgentMode(agentMode) {
    _set(this.state, 'usm.policyWizard.policy.agentMode', agentMode);
    return this;
  }

  policyWizPrimaryHttpsPort(primaryHttpsPort) {
    _set(this.state, 'usm.policyWizard.policy.primaryHttpsPort', primaryHttpsPort);
    return this;
  }

  policyWizPrimaryHttpsBeaconInterval(primaryHttpsBeaconInterval) {
    _set(this.state, 'usm.policyWizard.policy.primaryHttpsBeaconInterval', primaryHttpsBeaconInterval);
    return this;
  }

  policyWizPrimaryHttpsBeaconIntervalUnit(primaryHttpsBeaconIntervalUnit) {
    _set(this.state, 'usm.policyWizard.policy.primaryHttpsBeaconIntervalUnit', primaryHttpsBeaconIntervalUnit);
    return this;
  }

  policyWizPrimaryUdpPort(primaryUdpPort) {
    _set(this.state, 'usm.policyWizard.policy.primaryUdpPort', primaryUdpPort);
    return this;
  }

  policyWizPrimaryUdpBeaconInterval(primaryUdpBeaconInterval) {
    _set(this.state, 'usm.policyWizard.policy.primaryUdpBeaconInterval', primaryUdpBeaconInterval);
    return this;
  }

  policyWizPrimaryUdpBeaconIntervalUnit(primaryUdpBeaconIntervalUnit) {
    _set(this.state, 'usm.policyWizard.policy.primaryUdpBeaconIntervalUnit', primaryUdpBeaconIntervalUnit);
    return this;
  }

  policyWizRunOnDaysOfWeek(runOnDaysOfWeekArray) {
    _set(this.state, 'usm.policyWizard.policy.runOnDaysOfWeek', runOnDaysOfWeekArray);
    return this;
  }

  policyWizCpuMax(cpuMax) {
    _set(this.state, 'usm.policyWizard.policy.cpuMax', cpuMax);
    return this;
  }

  policyWizCpuMaxVm(cpuMaxVm) {
    _set(this.state, 'usm.policyWizard.policy.cpuMaxVm', cpuMaxVm);
    return this;
  }

  policyWizCustomConfig(customConfig) {
    _set(this.state, 'usm.policyWizard.policy.customConfig', customConfig);
    return this;
  }

  policyWizVisited(visitedFieldsArray) {
    _set(this.state, 'usm.policyWizard.visited', visitedFieldsArray);
    return this;
  }

  policyWizStepShowErrors(stepId, showErrors) {
    switch (stepId) {
      case 'identifyPolicyStep':
        _set(this.state, 'usm.policyWizard.steps.0.showErrors', showErrors);
        break;
      case 'defineyPolicyStep':
        _set(this.state, 'usm.policyWizard.steps.1.showErrors', showErrors);
        break;
      default:
        break;
    }
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

  policyWizEndpointServers() {
    _set(this.state, 'usm.policyWizard.listOfEndpointServers', endpointServers);
    return this;
  }

  policyWizEndpointServersEmpty() {
    _set(this.state, 'usm.policyWizard.listOfEndpointServers', []);
    return this;
  }

  // ====================================================================
  // Windows Log Server settings
  // ====================================================================
  policyWizWinLogPrimaryDestination(primaryDestination) {
    _set(this.state, 'usm.policyWizard.policy.primaryDestination', primaryDestination);
    return this;
  }

  policyWizWinLogSecondaryDestination(secondaryDestination) {
    _set(this.state, 'usm.policyWizard.policy.secondaryDestination', secondaryDestination);
    return this;
  }

  policyWizWinLogLogServers() {
    _set(this.state, 'usm.policyWizard.listOfLogServers', logServers);
    return this;
  }

  policyWizWinLogLogServersEmpty() {
    _set(this.state, 'usm.policyWizard.listOfLogServers', []);
    return this;
  }

  policyWizWinLogProtocol(protocol) {
    _set(this.state, 'usm.policyWizard.policy.protocol', protocol);
    return this;
  }

  policyWizWinLogEnabled(enabled) {
    _set(this.state, 'usm.policyWizard.policy.enabled', enabled);
    return this;
  }

  policyWizWinLogSendTestLog(sendTestLog) {
    _set(this.state, 'usm.policyWizard.policy.sendTestLog', sendTestLog);
    return this;
  }

  /* TODO is this deprecated? Should this be deleted when group wizard is done ??? */
  group(config) {
    _set(this.state, 'usm.group', config);
    return this;
  }

  groupRanking(status) {
    _set(this.state, 'usm.groupWizard.groupRankingStatus', status);
    return this;
  }

  groupRankingWithData(data) {
    const groupRanking = data ? data : groups;
    _set(this.state, 'usm.groupWizard.groupRanking', groupRanking);
    return this;
  }

  selectGroupRanking(data) {
    _set(this.state, 'usm.groupWizard.selectedGroupRanking', data);
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

  groupWizId(id) {
    _set(this.state, 'usm.groupWizard.group.id', id);
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

  groupWizStepShowErrors(stepId, showErrors) {
    switch (stepId) {
      case 'identifyGroupStep':
        _set(this.state, 'usm.groupWizard.steps.0.showErrors', showErrors);
        break;
      case 'defineGroupStep':
        _set(this.state, 'usm.groupWizard.steps.1.showErrors', showErrors);
        break;
      case 'applyPolicyStep':
        _set(this.state, 'usm.groupWizard.steps.2.showErrors', showErrors);
        break;
      default:
        break;
    }
    return this;
  }

  groupWizGroup(group, updateOrig = false) {
    if (group.groupCriteria && group.groupCriteria.criteria) {
      const criteria = group.groupCriteria.criteria.slice();
      _set(this.state, 'usm.groupWizard.criteriaCache', criteria);
    } else {
      _set(this.state, 'usm.groupWizard.criteriaCache', null);
    }
    _set(this.state, 'usm.groupWizard.group', group);
    if (updateOrig) {
      _set(this.state, 'usm.groupWizard.groupOrig', group);
    }
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

  groupWizPolicyList(policyList) {
    _set(this.state, 'usm.groupWizard.policyList', policyList);
    return this;
  }

  groupWizPolicyListStatus(status) {
    _set(this.state, 'usm.groupWizard.policyListStatus', status);
    return this;
  }

  groupWizAssignedPolicies(policies) {
    _set(this.state, 'usm.groupWizard.group.assignedPolicies', policies);
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

  focusedGroup(config) {
    _set(this.state, 'usm.groups.focusedItem', config);
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

  focusedPolicy(config) {
    _set(this.state, 'usm.policies.focusedItem', config);
    return this;
  }

  selectedPolicies(config) {
    _set(this.state, 'usm.policies.itemsSelected', config);
    return this;
  }

}
