import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';

export default Component.extend({

  layout,

  tagName: 'hbox',

  classNames: 'col-xs-6 col-md-7',

  classNameBindings: ['property-value'],

  @computed
  contextItems() {
    return [
      {
        label: 'analyzeUser',
        prefix: 'investigateShared.endpoint.fileActions.',
        action([selection]) {
          if (selection) {
            const userName = selection.split('\\');
            const path = `${window.location.origin}/investigate/users?ueba=/username/${userName[1]}`;
            window.open(path);
          }
        }
      }
    ];
  },

  @computed('property')
  propertyValueLength({ value }) {
    return Array.isArray(value) ? `(${value.length})` : '';
  }
});
