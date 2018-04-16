import Component from '@ember/component';
import layout from './template';
import { connect } from 'ember-redux';
import {
  treeData,
  hasError,
  errorMessage } from 'investigate-shared/reducers/endpoint/process-tree/selectors';

const stateToComputed = (state) => ({
  treeData: treeData(state),
  hasError: hasError(state),
  errorMessage: errorMessage(state)
});


const WrapperComponent = Component.extend({
  layout,
  tagName: 'hbox',
  classNames: ['process-analysis-wrapper', 'scrollable-panel-wrapper', 'col-xs-12']
});

export default connect(stateToComputed)(WrapperComponent);
