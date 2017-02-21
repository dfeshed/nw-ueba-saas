import Ember from 'ember';
import connect from 'ember-redux/components/connect';
import computed from 'ember-computed-decorators';

import TabList from 'context/config/dynamic-tab';
import * as ContextActions from 'context/actions/context-creators';
import layout from './template';

const NO_COUNT_TABS = ['overview', 'liveConnect'];

const {
  Component
} = Ember;

const stateToComputed = ({ context }) => ({
  activeTabName: context.activeTabName
});

const dispatchToActions = (dispatch) => ({
  activate: (tabName) => dispatch(ContextActions.updateActiveTab(tabName))
});

const determineTabCountText = (tabName, tabData = []) => {
  if (NO_COUNT_TABS.indexOf(tabName) > -1) {
    return '';
  } else {
    return `(${tabData.length})`;
  }
};

const TabsComponent = Component.extend({
  layout,
  tagName: '',

  model: null,
  toolbar: null,

  /*
   * Iterates over all the possible tab lists and finds a match
   * for the current meta. Then determines countText to display for each tab.
   * columns only recomputes when the context item changes.
   */
  @computed('model.meta')
  tabs(meta) {
    const tabList = TabList.find((tab) => tab.tabType === meta);

    // Set the toolbar from the selected tab configuration
    this.set('toolbar', tabList.toolbar);

    // Map over the columns and build the tab count text for each tab
    return tabList.columns.map((tab) => ({
      ...tab,
      countText: determineTabCountText(tab.field, this.get('model.contextData')[tab.dataSourceType])
    }));
  },

  /*
   * When the active tab changes, sets an isActive flag.
   * Using new computed to store collection with active flag to avoid
   * recalculating entire tabList when tab is clicked
   */
  @computed('activeTabName', 'tabs')
  tabsActive(activeTabName, tabs) {
    return tabs.map((tab) => ({
      ...tab,
      isActive: tab.field === activeTabName
    }));
  }
});

export default connect(stateToComputed, dispatchToActions)(TabsComponent);