import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getFileDetailTabs } from 'investigate-files/reducers/visuals/selectors';
import { setNewFileTab, toggleFilePropertyPanel } from 'investigate-files/actions/visual-creators';
import { fileSummary } from 'investigate-files/reducers/file-detail/selectors';

const stateToComputed = (state) => ({
  fileDetailTabs: getFileDetailTabs(state),
  summary: fileSummary(state),
  isFilePropertyPanelVisible: state.files.visuals.isFilePropertyPanelVisible
});

const dispatchToActions = {
  setNewFileTab,
  toggleFilePropertyPanel
};

const TitleBar = Component.extend({
  tagName: 'hbox',
  classNames: ['title-bar']
});

export default connect(stateToComputed, dispatchToActions)(TitleBar);
