import Component from '@ember/component';
import layout from './template';

export default Component.extend({
  layout,
  tagName: 'span',
  classNames: ['option-name'],
  profile: null
});
