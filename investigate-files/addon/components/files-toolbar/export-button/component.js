import classic from 'ember-classic-decorator';
import { classNames, tagName } from '@ember-decorators/component';
import { computed } from '@ember/object';
import { connect } from 'ember-redux';
import Component from '@ember/component';
import { fileExportLink, isExportButtonDisabled } from 'investigate-files/reducers/file-list/selectors';
import { exportFileAsCSV } from 'investigate-files/actions/data-creators';

const stateToComputed = (state) => ({
  downloadStatus: state.files.fileList.downloadStatus,
  exportLink: fileExportLink(state),
  isExportButtonDisabled: isExportButtonDisabled(state)
});
const dispatchToActions = {
  exportFileAsCSV
};

@classic
@tagName('')
@classNames('file-export-button')
class ExportButton extends Component {
  @computed('isExportButtonDisabled')
  get exportTitle() {
    return this.isExportButtonDisabled.isEndpointBroker ? 'investigateFiles.button.brokerExportToCSV' : 'investigateFiles.button.exportToCSV';
  }
}

export default connect(stateToComputed, dispatchToActions)(ExportButton);
