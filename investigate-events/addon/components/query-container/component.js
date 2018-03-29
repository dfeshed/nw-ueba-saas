import Component from '@ember/component';
import { connect } from 'ember-redux';
import { hasRequiredValuesToQuery } from 'investigate-events/reducers/investigate/query-node/selectors';
import computed from 'ember-computed-decorators';

const stateToComputed = (state) => ({
  hasRequiredValuesToQuery: hasRequiredValuesToQuery(state)
});
const GUIDED = 'guided';

const QueryContainer = Component.extend({
  classNames: ['rsa-investigate-query-container', 'rsa-button-group'],

  tagName: 'nav',

  classNameBindings: ['queryView'],

  queryView: GUIDED,

  @computed('queryView', 'freeFormText', 'filters')
  criteria(queryView, freeFormText, filters) {
    if (queryView === 'guided') {
      this.set('freeFormText', '');
      return filters;
    } else if (queryView === 'freeForm') {
      this.set('filters', []);
      return freeFormText;
    }
  }

});

export default connect(stateToComputed)(QueryContainer);
