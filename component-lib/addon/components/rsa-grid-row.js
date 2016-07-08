import Ember from 'ember';
import layout from '../templates/components/rsa-grid-row';

export default Ember.Component.extend({

  layout,

  classNames: ['rsa-grid-row'],

  classNameBindings: ['collapseHeight',
                      'expandHeight'],

  fullWidth: false,

  collapseHeight: false,

  expandHeight: false

});
