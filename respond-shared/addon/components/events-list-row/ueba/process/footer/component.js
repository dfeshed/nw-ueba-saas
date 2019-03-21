import Component from '@ember/component';
import layout from './template';

export default Component.extend({
  layout,
  testId: 'uebaProcessEventFooter',
  attributeBindings: ['testId:test-id'],
  classNames: ['events-list-process-footer']
});
