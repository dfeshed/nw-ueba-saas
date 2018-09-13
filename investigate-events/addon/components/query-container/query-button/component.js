import Component from '@ember/component';
import { connect } from 'ember-redux';

import { canQueryGuided } from 'investigate-events/reducers/investigate/query-node/selectors';

const stateToComputed = (state) => ({
  requiredValuesToQuery: canQueryGuided(state),
  isQueryRunning: state.investigate.queryNode.isQueryRunning
});

const QueryButton = Component.extend({
  tagName: 'span',
  classNames: ['query-button-wrapper'],

  click() {
    this.get('executeQuery')();
  }
});

export default connect(stateToComputed)(QueryButton);
