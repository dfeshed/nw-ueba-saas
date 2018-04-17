import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  treeData,
  hasError,
  errorMessage
} from 'investigate-process-analysis/reducers/process-tree/selectors';

const stateToComputed = (state) => ({
  treeData: treeData(state),
  hasError: hasError(state),
  errorMessage: errorMessage(state)
});


const WrapperComponent = Component.extend({
  tagName: 'hbox',
  classNames: ['process-analysis-container', 'scrollable-panel-wrapper', 'col-xs-12']
});

export default connect(stateToComputed)(WrapperComponent);
