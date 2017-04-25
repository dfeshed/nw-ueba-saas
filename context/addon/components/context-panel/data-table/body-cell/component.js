import layout from './template';
import computed from 'ember-computed-decorators';
import get from 'ember-metal/get';
import Component from 'ember-component';

export default Component.extend({
  layout,

  @computed('item', 'column')
  getLink(item, column) {
    return window.location.origin.concat(column.path.replace('{0}', get(item, column.linkField || column.field)));
  }
});
