import Component from 'ember-component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import COLUMNS from './columns';
import TABS from './tabsConfig';
import {
  getHostFileEntries,
  getMountedPaths,
  getNetworkShares,
  getBashHistories,
  machineOsType } from 'investigate-hosts/reducers/details/overview/selectors';

const stateToComputed = (state) => ({
  animation: state.endpoint.detailsInput.animation,
  hostFileEntries: getHostFileEntries(state),
  mountedPaths: getMountedPaths(state),
  networkShares: getNetworkShares(state),
  bashHistories: getBashHistories(state),
  machineOsType: machineOsType(state)
});

const SystemInformation = Component.extend({

  tagName: 'box',

  classNames: ['system-information'],

  /**
   * Default selected tab
   * @public
   */
  selectedTab: 'HOST_ENTRIES',

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
  @computed('selectedTab', 'hostFileEntries')
  tableData(selectedTab) {
    const { field, columns } = COLUMNS[selectedTab];
    return {
      data: this.get(field),
      columns
    };
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
    },
    setTabView(tabName) {
      this.set('selectedTab', tabName);
    }
  }
});
export default connect(stateToComputed, undefined)(SystemInformation);
