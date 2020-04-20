import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import { setSearchScroll } from 'investigate-events/actions/interaction-creators';

const dispatchToActions = {
  setSearchScroll
};

const PrevSearchMatch = Component.extend({
  classNames: ['prev-search-trigger'],
  tagName: 'span',

  _toSend(action, argToPass) {
    this.send(action, argToPass);
  },

  @computed('searchScrollIndex', 'searchMatchesCount')
  pendingIndex(searchScrollIndex, searchMatchesCount) {
    return searchScrollIndex === 0 ? searchMatchesCount - 1 : searchScrollIndex - 1;
  },

  actions: {
    toSend(action, value) {
      this._toSend(action, value);
    }
  }

});

export default connect(null, dispatchToActions)(PrevSearchMatch);
