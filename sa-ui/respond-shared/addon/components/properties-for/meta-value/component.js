import layout from './template';
import Component from '@ember/component';

export default Component.extend({
  layout,
  tagName: 'dd',
  classNames: ['entity'],
  attributeBindings: ['testId:test-id', 'key:data-meta-key', 'value:data-entity-id']
});
