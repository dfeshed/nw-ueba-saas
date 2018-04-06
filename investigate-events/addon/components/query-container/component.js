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

  toggledOnceFlag: false,

  classNameBindings: ['queryView'],

  queryView: GUIDED,

  @computed('queryView', 'toggledOnceFlag')
  guidedHasFocus(queryView, toggledOnceFlag) {
    return queryView === 'guided' && toggledOnceFlag;
  },

  @computed('queryView', 'freeFormText', 'filters')
  criteria(queryView, freeFormText, filters) {
    if (queryView === 'guided') {
      this.set('freeFormText', '');
      return filters;
    } else if (queryView === 'freeForm') {
      this.set('filters', []);
      this.send('toggleFocusFlag', true);
      return freeFormText;
    }
  },

  actions: {
    toggleFocusFlag(state) {
      this.set('toggledOnceFlag', state);
    }
  }

});

export default connect(stateToComputed)(QueryContainer);
