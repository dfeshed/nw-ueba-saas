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

const ExportButton = Component.extend({
  classNames: 'export-button'
});

export default connect(stateToComputed, dispatchToActions)(ExportButton);
