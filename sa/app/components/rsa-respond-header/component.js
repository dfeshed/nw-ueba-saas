import Ember from 'ember';

const {
  Component,
  computed: {
    equal
  },
  inject: {
    service
  }
} = Ember;

const _PAGE_CARD = 'card';
const _PAGE_LIST = 'list';

export default Component.extend({
  tagName: 'header',
  classNames: 'rsa-respond-index-header',

  respondMode: service(),
  listViewActive: equal('respondMode.selected', _PAGE_LIST),

  actions: {
    listIconClicked() {
      this.set('respondMode.selected', _PAGE_LIST);
    },
    tileIconClicked() {
      this.set('respondMode.selected', _PAGE_CARD);
    }
  }
});
