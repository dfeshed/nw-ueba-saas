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

const FileDetailsTabs = Component.extend({
  tagName: 'hbox',
  classNames: ['file-details-tabs'],

  actions: {
    setFileTabAndFetchData(tabName) {
      this.send('setNewFileTab', tabName);
      const { format } = this.get('selectedDetailFile');
      const fileFormat = componentSelectionForFileType(format).format || '';
      this.get('switchToSelectedFileDetailsTab')(tabName, fileFormat);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(FileDetailsTabs);
