import Component from '@ember/component';
import { connect } from 'ember-redux';
import { lastScanTime,
  hostWithStatus,
  hostName } from 'investigate-hosts/reducers/details/overview/selectors';
import { isInsightsAgent, serviceList } from 'investigate-hosts/reducers/hosts/selectors';
import { serviceId } from 'investigate-shared/selectors/investigate/selectors';
import { exportFileContext } from 'investigate-hosts/actions/data-creators/details';
import { downloadMFT } from 'investigate-hosts/actions/data-creators/host';

const stateToComputed = (state) => ({
  hostDetails: hostWithStatus(state),
  lastScanTime: lastScanTime(state),
  isInsightsAgent: isInsightsAgent(state),
  serviceList: serviceList(state),
  serviceId: serviceId(state),
  hostName: hostName(state),
  scanTime: state.endpoint.detailsInput.scanTime,
  agentId: state.endpoint.detailsInput.agentId
});

const dispatchToActions = {
  exportFileContext,
  downloadMFT
};

const HostStatus = Component.extend({

  tagName: 'hbox',

  classNames: ['host-overview', 'host-item', 'flexi-fit', 'col-xs-12']

});

export default connect(stateToComputed, dispatchToActions)(HostStatus);
