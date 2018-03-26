import Component from '@ember/component';
import { connect } from 'ember-redux';
import { hasRequiredValuesToQuery } from 'investigate-events/reducers/investigate/query-node/selectors';

const stateToComputed = (state) => ({
  hasRequiredValuesToQuery: hasRequiredValuesToQuery(state)
});
const GUIDED = 'guided';

const QueryContainer = Component.extend({
  classNames: ['rsa-investigate-query-container', 'rsa-button-group'],

  tagName: 'nav',

  classNameBindings: ['queryView'],

  queryView: GUIDED

});

export default connect(stateToComputed)(QueryContainer);
