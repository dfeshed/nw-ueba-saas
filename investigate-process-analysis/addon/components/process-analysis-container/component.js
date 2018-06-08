import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  hasError,
  errorMessage,
  queryInput
} from 'investigate-process-analysis/reducers/process-tree/selectors';
import { isEventsSelected } from 'investigate-process-analysis/reducers/process-visuals/selectors';
import { fetchProcessDetails } from 'investigate-process-analysis/actions/creators/process-properties';

const dispatchToActions = {
  fetchProcessDetails
};

const stateToComputed = (state) => ({
  hasError: hasError(state),
  errorMessage: errorMessage(state),
  queryInput: queryInput(state),
  isEventsSelected: isEventsSelected(state),
  isEventPanelExpanded: state.processAnalysis.processVisuals.isEventPanelExpanded,
  isProcessDetailsVisible: state.processAnalysis.processVisuals.isProcessDetailsVisible
});

const WrapperComponent = Component.extend({
  tagName: 'hbox',
  classNames: ['process-analysis-container', 'scrollable-panel-wrapper', 'col-xs-12'],
  classNameBindings: ['isProcessDetailsVisible:show-process-details']
});

export default connect(stateToComputed, dispatchToActions)(WrapperComponent);
