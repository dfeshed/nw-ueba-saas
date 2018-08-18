import Component from '@ember/component';
import layout from './template';
import { get, computed } from '@ember/object';
import { htmlSafe } from '@ember/string';

export default Component.extend({
  layout,
  testId: null,
  id: 'ueba-iframe',
  attributeBindings: ['testId:test-id', 'style'],
  style: computed('iframeVisible', function() {
    const iframeVisible = get(this, 'iframeVisible');
    const display = iframeVisible ? 'display: block' : 'display: none';
    return htmlSafe(display);
  })
});
