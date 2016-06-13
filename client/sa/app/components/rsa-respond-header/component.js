import Ember from 'ember';

const _PAGE_CARD = 'card',
  _PAGE_LIST = 'list';

export default Ember.Component.extend({
  tagName: 'header',
  classNames: 'rsa-respond-index-header',

  respondMode: Ember.inject.service(),
  listViewActive: Ember.computed.equal('respondMode.selected', _PAGE_LIST),

  actions: {
    listIconClicked() {
      this.set('respondMode.selected', _PAGE_LIST);
    },
    tileIconClicked() {
      this.set('respondMode.selected', _PAGE_CARD);
    }
  }
});
