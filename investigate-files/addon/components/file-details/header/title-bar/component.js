import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getFileDetailTabs, displayCloseRightPanel } from 'investigate-files/reducers/visuals/selectors';
import { setNewFileTab, toggleFilePropertyPanel } from 'investigate-files/actions/visual-creators';
import { fileSummary } from 'investigate-files/reducers/file-detail/selectors';
import { getFileAnalysisData } from 'investigate-shared/actions/data-creators/file-analysis-creators';
import { componentSelectionForFileType } from 'investigate-shared/utils/file-analysis-view-util';
import { serviceList } from 'investigate-files/reducers/file-list/selectors';
import computed from 'ember-computed-decorators';

const stateToComputed = (state) => ({
  fileDetailTabs: getFileDetailTabs(state),
  summary: fileSummary(state),
  isFilePropertyPanelVisible: state.files.visuals.isFilePropertyPanelVisible,
  selectedDetailFile: state.files.fileList.selectedDetailFile,
  displayCloseRightPanel: displayCloseRightPanel(state),
  serviceList: serviceList(state)

});

const dispatchToActions = {
  setNewFileTab,
  toggleFilePropertyPanel,
  getFileAnalysisData
};

const TitleBar = Component.extend({
  tagName: 'hbox',
  classNames: ['title-bar'],


  @computed('selectedDetailFile')
  selectedItemList(selectedDetailFile) {
    return [selectedDetailFile];
  },

  actions: {
    setFileTabAndFetchData(tabName) {

      this.send('setNewFileTab', tabName);
      const { format } = this.get('selectedDetailFile');
      const fileFormat = componentSelectionForFileType(format).format || '';
      this.get('switchToSelectedFileDetailsTab')(tabName, fileFormat);
    },

    resetRiskScoreAction() {
      // TODO
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(TitleBar);
