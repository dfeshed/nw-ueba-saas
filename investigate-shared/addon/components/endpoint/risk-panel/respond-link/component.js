import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';

export default Component.extend({
  layout,

  @computed('entryId')
  respondLink(entryId) {
    return `${window.location.origin}/respond/incident/${entryId}`;
  }
});
