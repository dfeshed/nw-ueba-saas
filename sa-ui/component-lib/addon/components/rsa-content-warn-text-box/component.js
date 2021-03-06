import Component from '@ember/component';
import layout from './template';

export default Component.extend({
  layout,

  classNames: ['rsa-content-warn-text-box'],
  classNameBindings: ['isAlert', 'isShowing', 'isInfo']
});
