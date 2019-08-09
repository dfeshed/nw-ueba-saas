import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getHostDetailTabs, isOnOverviewTab, isActiveTabDownloads } from 'investigate-hosts/reducers/visuals/selectors';
import { hostName } from 'investigate-hosts/reducers/details/overview/selectors';

const stateToComputed = (state) => ({
  agentId: state.endpoint.detailsInput.agentId,
  hostDetailTabs: getHostDetailTabs(state),
  hostName: hostName(state),
  activeHostDetailTab: state.endpoint.visuals.activeHostDetailTab,
  selectedTab: state.endpoint.explore.selectedTab,
  showRightPanelButton: isOnOverviewTab(state),
  hideSnapshotAndExploreSearch: isActiveTabDownloads(state)
});

const TitleBarComponent = Component.extend({

  tagName: 'hbox',

  classNames: ['titlebar']

});

export default connect(stateToComputed)(TitleBarComponent);
