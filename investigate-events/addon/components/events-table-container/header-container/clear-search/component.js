import Component from '@ember/component';
import { connect } from 'ember-redux';
import { searchForTerm } from 'investigate-events/actions/interaction-creators';

const dispatchToActions = {
  searchForTerm
};

const ClearSearch = Component.extend({
  classNames: ['clear-search-trigger'],
  tagName: 'span',

  _toSend(action, argToPass) {
    this.send(action, argToPass);
  },

  actions: {
    toSend(action, value) {
      this._toSend(action, value);
    }
  }
});

export default connect(null, dispatchToActions)(ClearSearch);
