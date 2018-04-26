import Component from '@ember/component';
import { connect } from 'ember-redux';
import { hasRequiredValuesToQuery, guidedHasFocus, freeFormHasFocus } from 'investigate-events/reducers/investigate/query-node/selectors';
import computed from 'ember-computed-decorators';
import { setQueryView } from 'investigate-events/actions/interaction-creators';
import { run } from '@ember/runloop';

const stateToComputed = (state) => ({
  hasRequiredValuesToQuery: hasRequiredValuesToQuery(state),
  guidedHasFocus: guidedHasFocus(state),
  freeFormHasFocus: freeFormHasFocus(state),
  freeFormText: state.investigate.queryNode.freeFormText,
  queryView: state.investigate.queryNode.queryView
});

const dispatchToActions = {
  setQueryView
};

const QueryContainer = Component.extend({
  classNames: ['rsa-investigate-query-container', 'rsa-button-group'],

  tagName: 'nav',

  classNameBindings: ['queryView'],

  @computed('queryView', 'freeFormText', 'filters')
  criteria(queryView, freeFormText, filters) {
    if (queryView === 'guided' || queryView === 'nextGen') {
      return filters;
    } else if (queryView === 'freeForm') {
      return freeFormText;
    }
  },

  actions: {
    changeView(view) {
      this.send('setQueryView', view, this.get('filters'));
      run.next(() => {
        if (this.get('guidedHasFocus')) {
          this.$('.rsa-query-meta input').focus();
        } else if (this.get('freeFormHasFocus')) {
          this.$('.rsa-investigate-free-form-query-bar input').focus();
        }
      });
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(QueryContainer);
