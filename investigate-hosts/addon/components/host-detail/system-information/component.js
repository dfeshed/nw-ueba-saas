import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import TABS from './tabsConfig';
import {
  hostFileEntries,
  machineOsType,
  selectedSystemInformationData,
  bashHistories } from 'investigate-hosts/reducers/details/system-information/selectors';
import {
  setSystemInformationTab,
  setBashHistoryFilteredData
} from 'investigate-hosts/actions/ui-state-creators';

const stateToComputed = (state) => ({
  animation: state.endpoint.detailsInput.animation,
  hostFileEntries: hostFileEntries(state),
  machineOsType: machineOsType(state),
  systemInformationData: selectedSystemInformationData(state),
  selectedTab: state.endpoint.visuals.activeSystemInformationTab,
  bashHistories: bashHistories(state)
});
const dispatchToActions = {
  setSystemInformationTab,
  setBashHistoryFilteredData
};
const SystemInformation = Component.extend({

  tagName: 'box',

  classNames: ['system-information'],

  selectedUser: 'ALL',

  @computed('bashHistories')
  userList(history) {
    return ['ALL', ...history.mapBy('userName').uniq()];
  },

  /**
   * list of all the tabs for system information
   * @public
   *
   */
  @computed('selectedTab', 'machineOsType')
  tabList(selectedTab, machineOsType) {
    return TABS.map((tab) => {
      return {
        ...tab,
        hidden: tab.hiddenFor ? tab.hiddenFor.includes(machineOsType) : false,
        selected: tab.name === selectedTab
      };
    });
  },
  /**
   * Table data for display, data will be calculated based on tab selection
   * @param selectedTab
   * @public
   */
  @computed('systemInformationData', 'isBashHistorySelected', 'selectedUser')
  tableData(systemInformationData, isBashHistorySelected, selectedUser) {
    const { columns, data } = { ...systemInformationData };
    if (isBashHistorySelected) {
      const filteredData = selectedUser === 'ALL' ? data : data.filterBy('userName', selectedUser);
      return { columns, data: filteredData };
    }
    return { columns, data };
  },

  @computed('selectedTab')
  isBashHistorySelected(tab) {
    return 'BASH_HISTORY' === tab;
  },
  actions: {
    onSelection(value) {
      this.set('selectedUser', value);
    }
  }
});
export default connect(stateToComputed, dispatchToActions)(SystemInformation);
