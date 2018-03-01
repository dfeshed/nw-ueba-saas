import Component from '@ember/component';
import { connect } from 'ember-redux';
import { setNewTabView } from 'investigate-hosts/actions/data-creators/details';
import { toggleOverviewPanel } from 'investigate-hosts/actions/ui-state-creators';
import { getHostDetailTabs } from 'investigate-hosts/reducers/visuals/selectors';
import { hostName } from 'investigate-hosts/reducers/details/overview/selectors';
import { serviceList } from 'investigate-hosts/reducers/hosts/selectors';
import computed from 'ember-computed-decorators';

const stateToComputed = (state) => ({
  hostDetailTabs: getHostDetailTabs(state),
  hostName: hostName(state),
  serviceList: serviceList(state),
  isOverviewPanelVisible: state.endpoint.detailsInput.isOverviewPanelVisible
});

const dispatchToActions = {
  setNewTabView,
  toggleOverviewPanel
};

const TitleBarComponent = Component.extend({

  tagName: 'hbox',

  classNames: ['titlebar'],

  @computed('isOverviewPanelVisible')
  expandContract(isOverviewPanelVisible) {
    return isOverviewPanelVisible ? 'shrink-diagonal-2' : 'expand-diagonal-4';
  }
});

export default connect(stateToComputed, dispatchToActions)(TitleBarComponent);
