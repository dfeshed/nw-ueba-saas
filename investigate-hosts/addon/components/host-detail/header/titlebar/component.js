import Component from '@ember/component';
import { connect } from 'ember-redux';
import { setNewTabView } from 'investigate-hosts/actions/data-creators/details';
import { toggleDetailRightPanel } from 'investigate-hosts/actions/ui-state-creators';
import { getHostDetailTabs, isOnOverviewTab } from 'investigate-hosts/reducers/visuals/selectors';
import { hostName } from 'investigate-hosts/reducers/details/overview/selectors';
import { serviceList } from 'investigate-hosts/reducers/hosts/selectors';
import { serviceId } from 'investigate-shared/selectors/investigate/selectors';

const stateToComputed = (state) => ({
  hostDetailTabs: getHostDetailTabs(state),
  hostName: hostName(state),
  serviceList: serviceList(state),
  isDetailRightPanelVisible: state.endpoint.detailsInput.isDetailRightPanelVisible,
  serviceId: serviceId(state),
  activeHostDetailTab: state.endpoint.visuals.activeHostDetailTab,
  showRightPanelButton: isOnOverviewTab(state)
});

const dispatchToActions = {
  setNewTabView,
  toggleDetailRightPanel
};

const TitleBarComponent = Component.extend({

  tagName: 'hbox',

  classNames: ['titlebar']

});

export default connect(stateToComputed, dispatchToActions)(TitleBarComponent);
