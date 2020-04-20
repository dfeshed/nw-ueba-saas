import classic from 'ember-classic-decorator';
import { action } from '@ember/object';
import { classNames, tagName } from '@ember-decorators/component';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getFileDetailTabs } from 'investigate-files/reducers/visuals/selectors';
import { setNewFileTab } from 'investigate-files/actions/visual-creators';
import { componentSelectionForFileType } from 'investigate-shared/utils/file-analysis-view-util';

const stateToComputed = (state) => ({
  fileDetailTabs: getFileDetailTabs(state),
  selectedDetailFile: state.files.fileList.selectedDetailFile
});

const dispatchToActions = {
  setNewFileTab
};

@classic
@tagName('hbox')
@classNames('file-details-tabs')
class FileDetailsTabs extends Component {
  @action
  setFileTabAndFetchData(tabName) {
    this.send('setNewFileTab', tabName);
    const { format } = this.get('selectedDetailFile');
    const fileFormat = componentSelectionForFileType(format).format || '';
    this.get('switchToSelectedFileDetailsTab')(tabName, fileFormat);
  }
}

export default connect(stateToComputed, dispatchToActions)(FileDetailsTabs);
