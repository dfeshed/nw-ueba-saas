import Component from 'ember-component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import COLUMNS from './columns';
import TABS from './tabsConfig';
import {
  hostFileEntries,
  machineOsType,
  selectedSystemInformationData,
  bashHistories } from 'investigate-hosts/reducers/details/system-information/selectors';
import { setSystemInformationTab, setBashHistoryFilteredData } from 'investigate-hosts/actions/ui-state-creators';

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
  @computed('selectedTab')
  tabList(selectedTab) {
    const os = this.get('machineOsType');
    return TABS.map((tab) => {
      return {
        ...tab,
        hidden: tab.hiddenFor ? tab.hiddenFor.includes(os) : false,
        selected: tab.name === selectedTab
      };
    });
  },
  /**
   * Table data for display, data will be calculated based on tab selection
   * @param selectedTab
   * @returns {{data, columns}}
   * @public
   */
  @computed('systemInformationData')
  tableData(systemInformationData) {
    return { ...systemInformationData };
  },

  @computed('selectedTab')
  isBashHistorySelected(tab) {
    return 'BASH_HISTORY' === tab;
  },
  actions: {
    onSelection(value) {
      const tab = this.get('selectedTab');
      const { field, columns } = COLUMNS[tab];
      const data = this.get(field);
      const filteredData = value === 'ALL' ? data : data.filterBy('userName', value);
      this.set('selectedUser', value);
      this.set('tableData', { data: filteredData, columns });
    }
  }
});
export default connect(stateToComputed, dispatchToActions)(SystemInformation);
