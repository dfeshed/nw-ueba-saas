import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';

export default Component.extend({
  layout,
  tagName: 'ul',
  classNames: ['rsa-item-list'],
  listName: null,
  list: null,

  @computed('list')
  hasOOTBIndicators(list) {
    const ootbIndicatedItems = list.filter((item) => typeof item.ootb !== 'undefined');
    return ootbIndicatedItems.length > 0;
  },

  @computed('listName')
  noResultsMessage(listName) {
    return `All ${listName.toLowerCase()} have been excluded by the current filter`;
  }

});
