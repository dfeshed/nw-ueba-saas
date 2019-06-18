import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  hasError,
  errorMessage,
  queryInput
} from 'investigate-process-analysis/reducers/process-tree/selectors';
import { fetchProcessDetails } from 'investigate-process-analysis/actions/creators/process-properties';

const dispatchToActions = {
  fetchProcessDetails
};

const stateToComputed = (state) => ({
  hasError: hasError(state),
  errorMessage: errorMessage(state),
  queryInput: queryInput(state),
  isEventPanelExpanded: state.processAnalysis.processVisuals.isEventPanelExpanded,
  isProcessDetailsVisible: state.processAnalysis.processVisuals.isProcessDetailsVisible
});

const WrapperComponent = Component.extend({

  tagName: 'box',

  classNames: ['process-analysis-container', 'scrollable-panel-wrapper', 'col-xs-12'],

  classNameBindings: ['isProcessDetailsVisible:show-process-details:hide-process-details']

});

export default connect(stateToComputed, dispatchToActions)(WrapperComponent);
