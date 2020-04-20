import layout from './template';
import Component from '@ember/component';

export default Component.extend({
  layout,
  tagName: 'span',
  classNames: ['no-value']
});
