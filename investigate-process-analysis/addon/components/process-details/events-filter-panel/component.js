import Component from '@ember/component';
import { connect } from 'ember-redux';
import { resetFilterValue } from 'investigate-process-analysis/actions/creators/process-filter';

const stateToComputed = (state) => ({
  filterConfig: state.processAnalysis.processFilter.schema,
  selectedProcessId: state.processAnalysis.processTree.selectedProcess.processId
});

const dispatchToActions = {
  resetFilterValue
};

const eventsFilterPanel = Component.extend({

  classNames: ['eventsFilterPanel']

});

export default connect(stateToComputed, dispatchToActions)(eventsFilterPanel);