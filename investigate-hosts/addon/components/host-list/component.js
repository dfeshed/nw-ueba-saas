import { connect } from 'ember-redux';
import Component from '@ember/component';
import { setDataSourceTab, setHostPropertyTabView } from 'investigate-hosts/actions/data-creators/details';
import { toggleRiskPanel } from 'investigate-hosts/actions/data-creators/host';
import { getDataSourceTab, getContext } from 'investigate-hosts/reducers/visuals/selectors';
import { getAlertsCount, getIncidentsCount } from 'investigate-shared/selectors/context';
import { inject as service } from '@ember/service';

import {
  resetFilters
} from 'investigate-hosts/actions/data-creators/filter';

import { setEndpointServer } from 'investigate-hosts/actions/data-creators/endpoint-server';

const stateToComputed = (state) => ({
  schemaLoading: state.endpoint.schema.schemaLoading,
  activeDataSourceTab: state.endpoint.visuals.activeDataSourceTab,
  contextError: state.endpoint.visuals.contextError,
  dataSourceTabs: getDataSourceTab(state),
  context: getContext(state),
  alertsCount: getAlertsCount(state),
  incidentsCount: getIncidentsCount(state),
  showRiskPanel: state.endpoint.visuals.showRiskPanel,
  contextLoadingStatus: state.endpoint.visuals.contextLoadingStatus,
  servers: state.endpointServer,
  serverId: state.endpointQuery.serverId
});

const dispatchToActions = {
  resetFilters,
  setDataSourceTab,
  setHostPropertyTabView,
  toggleRiskPanel,
  setEndpointServer
};
const Container = Component.extend({

  tagName: '',

  classNames: 'host-list show-more-filter main-zone',

  features: service(),

  actions: {
    closeRiskPanel() {
      this.sendAction('toggleRiskPanel', false);
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(Container);
