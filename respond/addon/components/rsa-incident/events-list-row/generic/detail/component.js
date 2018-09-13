import Component from '@ember/component';

export default Component.extend({
  testId: 'genericEventDetail',
  attributeBindings: ['testId:test-id'],
  classNames: ['events-list-generic-detail']
});
