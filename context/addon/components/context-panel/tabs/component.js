import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import * as ContextActions from 'context/actions/context-creators';
import layout from './template';
import { isDataSourceEnabled } from 'context/util/context-data-modifier';
import { setProperties } from '@ember/object';

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
  if (!isDataSourceEnabled(lookupData, dataSourceType)) {
    return i18n.t('context.noResults');
  }
};

const getLoadingIcon = (lookupData, { dataSourceType }) => {
  if (!lookupData) {
    return false;
  }
  return (dataSourceType === 'Endpoint') ? (lookupData.IOC || lookupData.Machines || lookupData.Modules) : lookupData[dataSourceType];
};

const TabsComponent = Component.extend({
  layout,
  tagName: '',
  model: null,

  /*
   * The list of tabs, independent of which tab is currently active.
   * Using new computed to store collection without active flag to avoid
   * recalculating entire tabList when tab is clicked
   */
  @computed('tabs', 'lookupData.[]')
  tabsActive(tabs, [lookupData]) {
    if (!tabs) {
      return;
    }
    return tabs.map((tab) => ({
      ...tab,
      toolTipText: getToolTipText(lookupData, tab, this.get('i18n')),
      loadingIcon: !getLoadingIcon(lookupData, tab) && tab.isConfigured
    }));
  },

  /*
   * When the active tab changes, updates `isActive` & `tabClass` of each tab.
   * Returns the same value as `tabsActive`, just updates tab properties.
   */
  @computed('tabsActive', 'activeTabName')
  statefulTabsActive(tabsActive, activeTabName) {
    if (tabsActive) {
      tabsActive.forEach((tab) => {
        setProperties(tab, {
          isActive: tab.field === activeTabName,
          tabClass: tab.field === activeTabName ? 'tab-active-background' : ''
        });
      });
    }
    return tabsActive;
  }
});

export default connect(stateToComputed, dispatchToActions)(TabsComponent);
