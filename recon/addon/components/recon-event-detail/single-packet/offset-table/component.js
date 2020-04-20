import Component from '@ember/component';
import layout from './template';

export default Component.extend({
  layout,
  tagName: 'section',
  classNames: 'rsa-offset-column',

  byteRows: null
});
