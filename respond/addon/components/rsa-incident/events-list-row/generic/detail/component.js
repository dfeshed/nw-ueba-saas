import Component from '@ember/component';
import layout from './template';

export default Component.extend({
  layout,
  testId: 'genericEventDetail',
  attributeBindings: ['testId:test-id'],
  classNames: ['events-list-generic-detail']
});
