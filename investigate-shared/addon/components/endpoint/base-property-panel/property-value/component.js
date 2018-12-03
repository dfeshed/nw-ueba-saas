import Component from '@ember/component';
import layout from './template';

export default Component.extend({

  tagName: 'hbox',
  layout,

  classNames: 'col-xs-6 col-md-7',

  classNameBindings: ['property-value']

});
