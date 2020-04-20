import layout from './template';
import Component from '@ember/component';

export default Component.extend({
  layout,
  testId: 'uebaTlsEventFooter',
  attributeBindings: ['testId:test-id'],
  classNames: ['events-list-tls-footer']
});
