import classic from 'ember-classic-decorator';
import { classNames } from '@ember-decorators/component';
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

@classic
@classNames('eventsFilterPanel')
class eventsFilterPanel extends Component {}

export default connect(stateToComputed, dispatchToActions)(eventsFilterPanel);
