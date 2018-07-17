import Component from '@ember/component';
import { connect } from 'ember-redux';

import { canQueryNextGen } from 'investigate-events/reducers/investigate/next-gen/selectors';

const stateToComputed = (state) => ({
  requiredValuesToQuerySelector: canQueryNextGen(state)
});

const QueryButton = Component.extend({
  tagName: 'span',

  click() {
    this.get('executeQuery')();
  }
});

export default connect(stateToComputed)(QueryButton);
