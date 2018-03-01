import { connect } from 'ember-redux';
import Component from '@ember/component';
import { hostExportLink } from 'investigate-hosts/reducers/hosts/selectors';
import { exportAsFile } from 'investigate-hosts/actions/data-creators/host';

const stateToComputed = (state) => ({
  hostExportStatus: state.endpoint.machines.hostExportStatus,
  exportLink: hostExportLink(state)
});
const dispatchToActions = {
  exportAsFile
};

const ExportButton = Component.extend({ });

export default connect(stateToComputed, dispatchToActions)(ExportButton);
