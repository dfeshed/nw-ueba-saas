import classic from 'ember-classic-decorator';
import { classNames } from '@ember-decorators/component';
import { computed } from '@ember/object';
import { connect } from 'ember-redux';
import Component from '@ember/component';
import { hostExportLink, isExportButtonDisabled } from 'investigate-hosts/reducers/hosts/selectors';
import { exportAsFile } from 'investigate-hosts/actions/data-creators/host';

const stateToComputed = (state) => ({
  hostExportStatus: state.endpoint.machines.hostExportStatus,
  exportLink: hostExportLink(state),
  isExportButtonDisabled: isExportButtonDisabled(state)
});
const dispatchToActions = {
  exportAsFile
};

@classic
@classNames('export-button')
class ExportButton extends Component {
  @computed('isExportButtonDisabled')
  get exportTitle() {
    return this.isExportButtonDisabled.isEndpointBroker ? 'investigateHosts.hosts.button.brokerExportCSV' : 'investigateHosts.hosts.button.exportCSV';
  }
}

export default connect(stateToComputed, dispatchToActions)(ExportButton);
