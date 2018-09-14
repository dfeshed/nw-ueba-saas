import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getFileDetailTabs } from 'investigate-files/reducers/visuals/selectors';
import { setNewFileTab } from 'investigate-files/actions/data-creators';

const stateToComputed = (state) => ({
  fileDetailTabs: getFileDetailTabs(state)
});

const dispatchToActions = {
  setNewFileTab
};

const HeaderComponent = Component.extend({
  tagName: 'hbox',
  classNames: ['flexi-fit', 'file-header']
});

export default connect(stateToComputed, dispatchToActions)(HeaderComponent);
