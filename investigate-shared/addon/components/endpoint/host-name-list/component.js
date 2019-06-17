import Component from '@ember/component';
import layout from './template';
import { computed } from '@ember/object';

export default Component.extend({
  layout,

  classNames: ['host-name-list'],

  attributeBindings: ['testId:test-id'],

  testId: 'hostNameList',

  items: null,

  isLoading: false,

  onItemClick: null,

  onPivotToInvestigate: null,

  itemCount: computed('items', function() {
    return this.items && this.items.length;
  }),

  hasData: computed('itemCount', function() {
    return !!this.itemCount;
  }),

  countLabelKey: computed('itemCount', function() {
    return 1 < this.itemCount ? 'investigateShared.machineCount.plural' : 'investigateShared.machineCount.singular';
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
