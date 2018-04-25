import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  hasError,
  errorMessage,
  queryInput
} from 'investigate-process-analysis/reducers/process-tree/selectors';

const stateToComputed = (state) => ({
  hasError: hasError(state),
  errorMessage: errorMessage(state),
  queryInput: queryInput(state)
});


const WrapperComponent = Component.extend({
  tagName: 'hbox',
  classNames: ['process-analysis-container', 'scrollable-panel-wrapper', 'col-xs-12']
});

export default connect(stateToComputed)(WrapperComponent);
