import layout from './template';
import computed from 'ember-computed-decorators';
import Component from 'ember-component';

export default Component.extend({
  layout,

  @computed('data')
  count: (data) => (data) ? [].concat(data).length : '',

  @computed('title', 'index')
  tetherPanelId: (title, index) => title.camelize().concat(index)

});
