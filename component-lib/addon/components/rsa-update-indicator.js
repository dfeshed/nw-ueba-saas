import Component from '@ember/component';
import computed from 'ember-computed';
import { isEmpty } from '@ember/utils';
import layout from '../templates/components/rsa-update-indicator';

export default Component.extend({
  layout,

  classNames: ['rsa-update-indicator'],
  classNameBindings: [
    'isIconOnly',
    'onTile',
    'isHidden'
  ],

  isIconOnly: false,
  onTile: false,
  hasUpdate: false,
  updateKey: '',

  model: null,

  totalUpdated: computed('updateKey', 'model.[]', function() {
    if (isEmpty(this.get('model')) || isEmpty(this.get('updateKey'))) {
      return 0;
    }

    return this.get('model').filterBy(this.get('updateKey'), true).length;
  }),

  isHidden: computed('model.[]', 'updateKey', 'totalUpdated', 'isIconOnly', function() {
    if (isEmpty(this.get('model'))) {
      return true;
    }
    const hasUpdate = this.get('model')[this.get('updateKey')] || false;
    return (this.get('isIconOnly')) ? !hasUpdate : this.get('totalUpdated') === 0;
  })
});
