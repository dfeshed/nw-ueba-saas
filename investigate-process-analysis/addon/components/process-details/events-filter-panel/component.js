import Component from '@ember/component';
import { connect } from 'ember-redux';
import { resetFilterValue } from 'investigate-process-analysis/actions/creators/process-filter';

const stateToComputed = (state) => ({
  filterConfig: state.processAnalysis.processFilter.schema,
  selectedProcess: state.processAnalysis.processTree.selectedProcess
});

const dispatchToActions = {
  resetFilterValue
};

const eventsFilterPanel = Component.extend({

  classNames: ['eventsFilterPanel', 'rsa-nav-tab', 'is-left-aligned-primary']

});

export default connect(stateToComputed, dispatchToActions)(eventsFilterPanel);