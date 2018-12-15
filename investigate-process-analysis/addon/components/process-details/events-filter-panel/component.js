import Component from '@ember/component';
import { connect } from 'ember-redux';
import { resetFilterValue } from 'investigate-process-analysis/actions/creators/process-filter';

const stateToComputed = (state) => ({
  filterConfig: state.processAnalysis.processFilter.schema,
  selectedProcessId: state.processAnalysis.processTree.selectedProcess ? state.processAnalysis.processTree.selectedProcess.processId : null
});

const dispatchToActions = {
  resetFilterValue
};

const eventsFilterPanel = Component.extend({

  classNames: ['eventsFilterPanel'],
  tagName: 'vbox'

});

export default connect(stateToComputed, dispatchToActions)(eventsFilterPanel);
