import Component from '@ember/component';

export default Component.extend({
  tagName: 'tr',
  testId: 'eventsListRow',
  attributeBindings: ['testId:test-id'],
  classNames: ['events-list-table-row']
});
