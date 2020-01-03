import classic from 'ember-classic-decorator';
import { classNames, tagName } from '@ember-decorators/component';
import { action, computed } from '@ember/object';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import TABS from './tabsConfig';
import {
  machineOsType,
  selectedSystemInformationData,
  isSelectedTabSecurityConfig,
  bashHistories } from 'investigate-hosts/reducers/details/system-information/selectors';
import {
  setSystemInformationTab,
  setBashHistoryFilteredData
} from 'investigate-hosts/actions/ui-state-creators';

const stateToComputed = (state) => ({
  animation: state.endpoint.detailsInput.animation,
  machineOsType: machineOsType(state),
  systemInformationData: selectedSystemInformationData(state),
  selectedTab: state.endpoint.visuals.activeSystemInformationTab,
  bashHistories: bashHistories(state),
  isSelectedTabSecurityConfig: isSelectedTabSecurityConfig(state)
});
const dispatchToActions = {
  setSystemInformationTab,
  setBashHistoryFilteredData
};

@classic
@tagName('box')
@classNames('system-information')
class SystemInformation extends Component {
  selectedUser = 'ALL';

  @computed('bashHistories')
  get userList() {
    return ['ALL', ...this.bashHistories.mapBy('userName').uniq()];
  }

  @computed('selectedTab', 'machineOsType')
  get tabList() {
    return TABS.map((tab) => {
      return {
        ...tab,
        hidden: tab.hiddenFor ? tab.hiddenFor.includes(this.machineOsType) : false,
        selected: tab.name === this.selectedTab
      };
    });
  }

  @computed(
    'systemInformationData',
    'isBashHistorySelected',
    'selectedUser',
    'isSelectedTabSecurityConfig'
  )
  get tableData() {
    if (!this.isSelectedTabSecurityConfig) {
      const { columns, data = [] } = { ...this.systemInformationData };
      if (this.isBashHistorySelected) {
        const filteredData = this.selectedUser === 'ALL' ? data : data.filterBy('userName', this.selectedUser);
        return { columns, data: filteredData };
      }
      return { columns, data };
    }
    return null;
  }

  @computed('selectedTab')
  get isBashHistorySelected() {
    return 'BASH_HISTORY' === this.selectedTab;
  }

  @action
  onSelection(value) {
    this.set('selectedUser', value);
  }
}

export default connect(stateToComputed, dispatchToActions)(SystemInformation);
