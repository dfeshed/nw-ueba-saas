import Component from '@ember/component';
import layout from './template';

export default Component.extend({

  tagName: 'footer',

  layout,

  classNames: ['details-footer'],

  item: null,

  // item type that is being handled eg. Column Group
  itemType: null

});
