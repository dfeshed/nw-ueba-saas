import Component from '@ember/component';
import layout from './template';

export default Component.extend({

  layout,

  tagName: 'hbox',

  classNames: 'col-xs-6 col-md-7',

  classNameBindings: ['property-value']

});
