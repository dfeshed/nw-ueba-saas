import Component from '@ember/component';
import { dirtyQueryToggle } from 'investigate-events/actions/query-validation-creators';
import { setFreeFormText } from 'investigate-events/actions/interaction-creators';
import { connect } from 'ember-redux';
import { encodeMetaFilterConditions } from 'investigate-events/actions/fetch/utils';

const stateToComputed = (state) => ({
  freeFormText: state.investigate.queryNode.freeFormText
});

const dispatchToActions = {
  dirtyQueryToggle,
  setFreeFormText
};

const freeForm = Component.extend({
  classNames: ['rsa-investigate-free-form-query-bar'],

  didReceiveAttrs() {
    this._super(...arguments);
    // transform the pills(if present) to raw text
    if (this.get('filters')) {
      const filters = this.get('filters').slice();
      let guidedFiltersString;
      if (filters.length > 1) {
        filters.pop(); // remove the empty object
        guidedFiltersString = encodeMetaFilterConditions(filters);
      } else {
        guidedFiltersString = encodeMetaFilterConditions(filters).trim();
      }
      this.send('setFreeFormText', guidedFiltersString);
    }

  },

  actions: {
    keyDown(e) {
      this.send('dirtyQueryToggle');
      if (e.keyCode === 13) {
        this.$('input').blur();
        this.executeQuery(this.get('freeFormText'));
      }
    },
    /* set text in state on every focus out event */
    focusOut(e) {
      this.send('setFreeFormText', e.target.value);
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(freeForm);