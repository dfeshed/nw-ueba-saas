import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getFileDetailTabs } from 'investigate-files/reducers/visuals/selectors';
import { setNewFileTab } from 'investigate-files/actions/visual-creators';
import { fileSummary } from 'investigate-files/reducers/file-detail/selectors';

const stateToComputed = (state) => ({
  fileDetailTabs: getFileDetailTabs(state),
  summary: fileSummary(state)
});

const dispatchToActions = {
  setNewFileTab
};

const TitleBar = Component.extend({
  tagName: 'hbox',
  classNames: ['title-bar']
});

export default connect(stateToComputed, dispatchToActions)(TitleBar);
