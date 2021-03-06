import classic from 'ember-classic-decorator';
import { action } from '@ember/object';
import { classNames, classNameBindings, tagName } from '@ember-decorators/component';
import { inject as service } from '@ember/service';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { hasMachineId, hostListPropertyTabs, isInsightsAgent } from 'investigate-hosts/reducers/hosts/selectors';
import { getPageOfMachines, setHostListPropertyTab, setFocusedHostIndex } from 'investigate-hosts/actions/data-creators/host';
import { riskState } from 'investigate-hosts/reducers/visuals/selectors';
import {
  applyFilters,
  createCustomSearch,
  applySavedFilters,
  deleteFilter,
  resetFilters
} from 'investigate-shared/actions/data-creators/filter-creators';
import { FILTER_TYPES } from './filter-types';
import { selectedFilterId, savedFilter } from 'investigate-shared/selectors/endpoint-filters/selectors';

import {
  getUpdatedRiskScoreContext,
  setSelectedAlert
} from 'investigate-shared/actions/data-creators/risk-creators';
import hostDetailsConfig from 'investigate-hosts/components/property-panel/overview-property-config';

const stateToComputed = (state) => ({
  focusedHost: state.endpoint.machines.focusedHost,
  hostListPropertyTabs: hostListPropertyTabs(state),
  hasMachineId: hasMachineId(state),
  filter: state.endpoint.filter,
  selectedFilterId: selectedFilterId(state.endpoint),
  savedFilter: savedFilter(state.endpoint),
  hostFilters: state.endpoint.filter.savedFilterList,
  activeHostListPropertyTab: state.endpoint.machines.activeHostListPropertyTab,
  risk: riskState(state),
  isInsightsAgent: isInsightsAgent(state)
});

const dispatchToActions = {
  getPageOfMachines,
  setHostListPropertyTab,
  createCustomSearch,
  applySavedFilters,
  applyFilters,
  deleteFilter,
  resetFilters,
  getUpdatedRiskScoreContext,
  setSelectedAlert,
  setFocusedHostIndex
};

@classic
@tagName('hbox')
@classNames('host-engine host-container')
@classNameBindings('hasMachineId')
class Container extends Component {
  @service
  eventBus;

  filterTypes = FILTER_TYPES;
  hostDetailsConfig = hostDetailsConfig;

  click(event) {
    // this trigger is required to open start/stop scan modal window
    this.get('eventBus').trigger('rsa-application-click', event.target);
  }

  @action
  onPanelClose(side) {
    if (side === 'right') {
      this.send('setFocusedHostIndex', -1);
    }
  }
}

export default connect(stateToComputed, dispatchToActions)(Container);
