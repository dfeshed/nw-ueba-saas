import Component from '@ember/component';
import layout from './template';
import { computed } from '@ember/object';

export default Component.extend({
  layout,

  tagName: 'box',

  classNames: ['host-name-list'],

  attributeBindings: ['testId:test-id'],

  testId: 'hostNameList',

  items: null,

  isLoading: false,

  onItemClick: null,

  onPivotToInvestigate: null,

  itemCount: null,

  hasData: computed('itemCount', function() {
    return this.itemCount > 0;
  }),

  countLabelKey: computed('itemCount', function() {
    return 100 < this.itemCount ? 'investigateFiles.message.listOfHostMessage' : '';
  }),


  actions: {
    handleHostNameClick(item) {
      if (this.onItemClick) {
        this.onItemClick('HOST_NAME', item);
      }
    },
    onPivotClick(item) {
      if (this.onItemClick) {
        this.onItemClick('PIVOT_ICON', item);
      }
    }
  }

});
