import Component from '@ember/component';
import layout from './template';

export default Component.extend({
  layout,
  tagName: 'tr',
  testId: 'eventsListRow',
  attributeBindings: ['testId:test-id'],
  classNames: ['events-list-table-row'],
  click() {
    this.expand(this.item.id);
  }
});
