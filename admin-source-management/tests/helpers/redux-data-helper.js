import Immutable from 'seamless-immutable';
import _ from 'lodash';
import { buildInitialState as policyWizInitialState } from 'admin-source-management/reducers/usm/policy-wizard/policy-wizard-reducers';
import { initialState as groupWizInitialState } from 'admin-source-management/reducers/usm/group-wizard-reducers';
import { initialState as filtersInitialState } from 'admin-source-management/reducers/usm/filters/filters-reducers';
import {
  groups,
  policies,
  endpointServers,
  logServers,
  focusedItem
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

  policyWizPolicyOrig() {
    _set(this.state, 'usm.policyWizard.policyOrig', this.state.usm.policyWizard.policy);
    return this;
  }

  policyWizSourceType(type) {
    _set(this.state, 'usm.policyWizard.policy.policyType', type);
    return this;
  }

  policyWizDefaultPolicy(defaultPolicy) {
    _set(this.state, 'usm.policyWizard.policy.defaultPolicy', defaultPolicy);
    return this;
  }

  policyWizCreatedOn(createdOn) {
    _set(this.state, 'usm.policyWizard.policy.createdOn', createdOn);
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

  policyWizScanMbr(scanMbr) {
    _set(this.state, 'usm.policyWizard.policy.scanMbr', scanMbr);
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

  policyWizPrimaryAlias(primaryAlias) {
    _set(this.state, 'usm.policyWizard.policy.primaryAlias', primaryAlias);
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

  policyWizPolicyFetchStatus(status) {
    _set(this.state, 'usm.policyWizard.policyFetchStatus', status);
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

  policyWizWinLogChannelFilters(channelFilters) {
    _set(this.state, 'usm.policyWizard.policy.channelFilters', channelFilters);
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
    _set(this.state, 'usm.groupWizard.focusedItem', focusedItem);
    return this;
  }

  selectGroupRanking(data) {
    _set(this.state, 'usm.groupWizard.selectedGroupRanking', data);
    return this;
  }

  selectedSourceType(data) {
    _set(this.state, 'usm.groupWizard.selectedSourceType', data);
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

  groupWizGroupOrig() {
    _set(this.state, 'usm.groupWizard.groupOrig', this.state.usm.groupWizard.group);
    return this;
  }

  groupWizGroupFetchStatus(status) {
    _set(this.state, 'usm.groupWizard.groupFetchStatus', status);
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

  // ====================================================================
  // group details single attribute setters
  // ====================================================================

  setGroupLastPublishedOn(config) {
    _set(this.state, 'usm.groups.focusedItem.lastPublishedOn', config);
    return this;
  }

  setGroupCreatedBy(config) {
    _set(this.state, 'usm.groups.focusedItem.createdBy', config);
    return this;
  }

  setGroupCreatedOn(config) {
    _set(this.state, 'usm.groups.focusedItem.createdOn', config);
    return this;
  }

  setGroupLastModifiedBy(config) {
    _set(this.state, 'usm.groups.focusedItem.lastModifiedBy', config);
    return this;
  }

  setGroupLastModifiedOn(config) {
    _set(this.state, 'usm.groups.focusedItem.lastModifiedOn', config);
    return this;
  }

  setGroupAssignedPolicies(config) {
    _set(this.state, 'usm.groups.focusedItem.assignedPolicies', config);
    return this;
  }

  setGroupCriteria(config) {
    _set(this.state, 'usm.groups.focusedItem.groupCriteria', config);
    return this;
  }

  setGroupSourceCount(config) {
    _set(this.state, 'usm.groups.focusedItem.sourceCount', config);
    return this;
  }

  setGroupDirty(config) {
    _set(this.state, 'usm.groups.focusedItem.dirty', config);
    return this;
  }

  groupsPolicyList(policyList) {
    _set(this.state, 'usm.groups.policyList', policyList);
    return this;
  }

  groupsPolicyListStatus(status) {
    _set(this.state, 'usm.groups.policyListStatus', status);
    return this;
  }

  fetchPolicyStatus(status) {
    _set(this.state, 'usm.policies.itemsStatus', status);
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

  groupRankingPrevListStatus(config) {
    _set(this.state, 'usm.policies.groupRankingPrevListStatus', config);
    return this;
  }

  // ====================================================================
  // policy details single attribute setters
  // ====================================================================

  setPolicyCustomConfig(config) {
    _set(this.state, 'usm.policies.focusedItem.customConfig', config);
    return this;
  }

  setPolicyGroups(config) {
    _set(this.state, 'usm.policies.focusedItem.associatedGroups', config);
    return this;
  }

  setPolicyDefaultPolicy(config) {
    _set(this.state, 'usm.policies.focusedItem.defaultPolicy', config);
    return this;
  }

  setPolicyLastPublishedOn(config) {
    _set(this.state, 'usm.policies.focusedItem.lastPublishedOn', config);
    return this;
  }

  setPolicyCreatedBy(config) {
    _set(this.state, 'usm.policies.focusedItem.createdBy', config);
    return this;
  }

  setPolicyCreatedOn(config) {
    _set(this.state, 'usm.policies.focusedItem.createdOn', config);
    return this;
  }

  setPolicyLastModifiedBy(config) {
    _set(this.state, 'usm.policies.focusedItem.lastModifiedBy', config);
    return this;
  }

  setPolicyLastModifiedOn(config) {
    _set(this.state, 'usm.policies.focusedItem.lastModifiedOn', config);
    return this;
  }

  setPolicyPrimaryAddress(config) {
    _set(this.state, 'usm.policies.focusedItem.primaryAddress', config);
    return this;
  }

  setPolicyPrimaryHttpsPort(config) {
    _set(this.state, 'usm.policies.focusedItem.primaryHttpsPort', config);
    return this;
  }

  setPolicyPrimaryUdpPort(config) {
    _set(this.state, 'usm.policies.focusedItem.primaryUdpPort', config);
    return this;
  }

  setPolicyPrimaryHttpsBeaconInterval(config) {
    _set(this.state, 'usm.policies.focusedItem.primaryHttpsBeaconInterval', config);
    return this;
  }

  setPolicyPrimaryHttpsBeaconIntervalUnit(config) {
    _set(this.state, 'usm.policies.focusedItem.primaryHttpsBeaconIntervalUnit', config);
    return this;
  }

  setPolicyPrimaryUdpBeaconInterval(config) {
    _set(this.state, 'usm.policies.focusedItem.primaryUdpBeaconInterval', config);
    return this;
  }

  setPolicyPrimaryUdpBeaconIntervalUnit(config) {
    _set(this.state, 'usm.policies.focusedItem.primaryUdpBeaconIntervalUnit', config);
    return this;
  }

  setPolicyScanType(config) {
    _set(this.state, 'usm.policies.focusedItem.scanType', config);
    return this;
  }

  setPolicyScanStartDate(config) {
    _set(this.state, 'usm.policies.focusedItem.scanStartDate', config);
    return this;
  }

  setPolicyScanStartTime(config) {
    _set(this.state, 'usm.policies.focusedItem.scanStartTime', config);
    return this;
  }

  setPolicyRecurInterval(config) {
    _set(this.state, 'usm.policies.focusedItem.recurrenceInterval', config);
    return this;
  }

  setPolicyRecurUnit(config) {
    _set(this.state, 'usm.policies.focusedItem.recurrenceUnit', config);
    return this;
  }

  setPolicyRunDaysOfWeek(config) {
    _set(this.state, 'usm.policies.focusedItem.runOnDaysOfWeek', config);
    return this;
  }

  setPolicyCpuMax(config) {
    _set(this.state, 'usm.policies.focusedItem.cpuMax', config);
    return this;
  }

  setPolicyCpuVm(config) {
    _set(this.state, 'usm.policies.focusedItem.cpuMaxVm', config);
    return this;
  }

  setPolicyRequestScan(config) {
    _set(this.state, 'usm.policies.focusedItem.requestScanOnRegistration', config);
    return this;
  }

  setPolicyScanMbr(config) {
    _set(this.state, 'usm.policies.focusedItem.scanMbr', config);
    return this;
  }

  setPolicyBlockingEnabled(config) {
    _set(this.state, 'usm.policies.focusedItem.blockingEnabled', config);
    return this;
  }

  setRarEnabled(config) {
    _set(this.state, 'usm.policies.focusedItem.rarEnabled', config);
    return this;
  }

  setPolicyAgentMode(config) {
    _set(this.state, 'usm.policies.focusedItem.agentMode', config);
    return this;
  }

  setPolicyChannels(config) {
    _set(this.state, 'usm.policies.focusedItem.channelFilters', config);
    return this;
  }

  setPolicyWindowsPrimaryDest(config) {
    _set(this.state, 'usm.policies.focusedItem.primaryDestination', config);
    return this;
  }

  setPolicyWindowsSecondaryDest(config) {
    _set(this.state, 'usm.policies.focusedItem.secondaryDestination', config);
    return this;
  }

  setPolicyWindowsProtocol(config) {
    _set(this.state, 'usm.policies.focusedItem.protocol', config);
    return this;
  }

  setPolicyWindowsEnabled(config) {
    _set(this.state, 'usm.policies.focusedItem.enabled', config);
    return this;
  }

  setPolicyWindowsSendTestLog(config) {
    _set(this.state, 'usm.policies.focusedItem.sendTestLog', config);
    return this;
  }

  selectedPolicies(config) {
    _set(this.state, 'usm.policies.itemsSelected', config);
    return this;
  }

  // ====================================================================
  // policies filter (filtersInitialState is used for all filters)
  // ====================================================================
  policiesFilter() {
    _set(this.state, 'usm.policiesFilter', _.cloneDeep(filtersInitialState));
    return this;
  }

  policiesFilterSelectedFilter(selectedFilter) {
    _set(this.state, 'usm.policiesFilter.selectedFilter', selectedFilter);
    return this;
  }

  policiesFilterExpressionList(expressionList) {
    _set(this.state, 'usm.policiesFilter.expressionList', expressionList);
    return this;
  }
}
