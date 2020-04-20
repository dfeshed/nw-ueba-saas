import layout from './template';
import Component from '@ember/component';

export default Component.extend({
  layout,
  tagName: 'dl',
  testId: 'uebaTlsEventHeader',
  attributeBindings: ['testId:test-id'],
  classNames: ['events-list-tls-header']
});
