import Component from '@ember/component';
import { connect } from 'ember-redux';
import { setNewTabView } from 'investigate-hosts/actions/data-creators/details';
import { getHostDetailTabs, isOnOverviewTab } from 'investigate-hosts/reducers/visuals/selectors';
import { hostName } from 'investigate-hosts/reducers/details/overview/selectors';

const stateToComputed = (state) => ({
  hostDetailTabs: getHostDetailTabs(state),
  hostName: hostName(state),
  activeHostDetailTab: state.endpoint.visuals.activeHostDetailTab,
  showRightPanelButton: isOnOverviewTab(state)
});

const dispatchToActions = {
  setNewTabView
};

const TitleBarComponent = Component.extend({

  tagName: 'hbox',

  classNames: ['titlebar']

});

export default connect(stateToComputed, dispatchToActions)(TitleBarComponent);
