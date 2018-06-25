import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';

export default Component.extend({
  layout,
  tagName: '',

  @computed('entryId', 'path')
  respondLink(entryId, path) {
    return `${window.location.origin}/respond/${path}/${entryId}`;
  }
});
