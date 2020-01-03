import classic from 'ember-classic-decorator';
import { classNames, tagName } from '@ember-decorators/component';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { isInsightsAgent, serviceList } from 'investigate-hosts/reducers/hosts/selectors';
import { serviceId } from 'investigate-shared/selectors/investigate/selectors';
import { exportFileContext } from 'investigate-hosts/actions/data-creators/details';
import { downloadMFT } from 'investigate-hosts/actions/data-creators/host';
import { lastScanTime, hostWithStatus, getRARStatus, hostName } from 'investigate-hosts/reducers/details/overview/selectors';

const stateToComputed = (state) => ({
  hostDetails: hostWithStatus(state),
  lastScanTime: lastScanTime(state),
  isInsightsAgent: isInsightsAgent(state),
  serviceList: serviceList(state),
  serviceId: serviceId(state),
  hostName: hostName(state),
  scanTime: state.endpoint.detailsInput.scanTime,
  agentId: state.endpoint.detailsInput.agentId,
  isAgentRoaming: getRARStatus(state),
  isIsolated: state.endpoint.overview.hostOverview?.agentStatus?.isolationStatus?.isolated
});

const dispatchToActions = {
  exportFileContext,
  downloadMFT
};

@classic
@tagName('hbox')
@classNames('host-overview', 'host-item', 'flexi-fit', 'col-xs-12')
class HostStatus extends Component {}

export default connect(stateToComputed, dispatchToActions)(HostStatus);
