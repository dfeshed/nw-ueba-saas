import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';

export default Component.extend({
  layout,

  classNames: ['rsa-dashboard'],

  config: null,

  @computed('layoutStyle')
  layoutComponent(layout) {
    return `layout.${layout}-layout`;
  }
});
