import layout from './template';
import Component from '@ember/component';

export default Component.extend({
  layout,
  tagName: 'dl',
  testId: 'genericEventHeader',
  attributeBindings: ['testId:test-id'],
  classNames: ['events-list-generic-header']
});
