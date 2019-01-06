import Component from '@ember/component';
import layout from './template';

export default Component.extend({
  layout,

  tagName: 'hbox',

  classNames: ['entity-stats flexi-fit']
});
