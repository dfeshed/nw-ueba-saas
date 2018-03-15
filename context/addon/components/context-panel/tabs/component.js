import Component from '@ember/component';
import { connect } from 'ember-redux';
import * as ContextActions from 'context/actions/context-creators';
import layout from './template';
import { getTabsCurrentState } from 'context/reducers/tabs/selectors';

const stateToComputed = ({ context: { context, tabs } }) => ({
  activeTabName: tabs.activeTabName,
  lookupData: context.lookupData,
  tabs: tabs.tabs,
  tabsCurrentState: getTabsCurrentState(tabs, context)
});

const dispatchToActions = (dispatch) => ({
  activate: (tabName) => dispatch(ContextActions.updateActiveTab(tabName))
});

const TabsComponent = Component.extend({
  layout
});

export default connect(stateToComputed, dispatchToActions)(TabsComponent);
