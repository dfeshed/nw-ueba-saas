import layout from './template';
import Component from '@ember/component';

export default Component.extend({
  layout,
  xs: '10',
  tagName: 'vbox',
  attributeBindings: ['xs']
});
