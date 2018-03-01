import Component from '@ember/component';

export default Component.extend({

  tagName: 'hbox',

  classNames: 'col-xs-6 col-md-7',

  classNameBindings: ['property-value']

});
