import Component from 'ember-component';
import connect from 'ember-redux/components/connect';
import computed from 'ember-computed-decorators';
import * as ContextActions from 'context/actions/context-creators';
import layout from './template';
import { getCount } from 'context/util/context-data-modifier';

const stateToComputed = ({ context }) => ({
  activeTabName: context.activeTabName,
  dataSources: context.dataSources,
  lookupData: context.lookupData,
  tabs: context.tabs
});

const dispatchToActions = (dispatch) => ({
  activate: (tabName) => dispatch(ContextActions.updateActiveTab(tabName))
});

const getToolTipText = (lookupData, { dataSourceType, isConfigured }, i18n) => {
  if (!isConfigured) {
    return i18n.t('context.notConfigured');
  }
  if (getCount(lookupData, dataSourceType) === 0) {
    return i18n.t('context.noResults');
  }
};

const TabsComponent = Component.extend({
  layout,
  tagName: '',
  model: null,

  /*
   * When the active tab changes, sets an isActive flag.
   * Using new computed to store collection with active flag to avoid
   * recalculating entire tabList when tab is clicked
   */
  @computed('activeTabName', 'tabs', 'lookupData.[]')
  tabsActive(activeTabName, tabs, [lookupData]) {
    if (!tabs) {
      return;
    }
    return tabs.map((tab) => ({
      ...tab,
      toolTipText: getToolTipText(lookupData, tab, this.get('i18n')),
      title: tab.tabTitle || tab.title,
      isActive: tab.field === activeTabName,
      loadingIcon: (!lookupData || !lookupData[tab.dataSourceType]) && tab.isConfigured,
      tabClass: tab.field === activeTabName ? 'tab-active-background' : ''
    }));
  }
});

export default connect(stateToComputed, dispatchToActions)(TabsComponent);