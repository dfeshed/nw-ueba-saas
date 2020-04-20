import { computed } from '@ember/object';
import layout from './template';
import Component from '@ember/component';

export default Component.extend({
  layout,

  groupDataArray: computed('groupData', function() {
    return (this.groupData) ? [].concat(this.groupData) : [];
  }),

  groupSize: computed('groupDataArray', function() {
    return this.groupDataArray.length;
  }),

  tetherPanelId: computed('title', 'index', function() {
    return this.title.camelize().concat(this.index);
  })

});
