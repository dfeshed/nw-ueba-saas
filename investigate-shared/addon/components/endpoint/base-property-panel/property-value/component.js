import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';

export default Component.extend({

  tagName: 'hbox',
  layout,

  classNames: 'col-xs-6 col-md-7',

  classNameBindings: ['property-value'],

  @computed('property')
  propertyValueLength({ value }) {
    return Array.isArray(value) ? `(${value.length})` : '';
  }
});
