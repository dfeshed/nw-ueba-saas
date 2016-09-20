import Ember from 'ember';
import layout from '../templates/components/rsa-update-indicator';

const {
  Component,
  computed,
  isEmpty
} = Ember;

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
    return isEmpty(this.get('model')) ? 0 : this.get('model').filterBy(this.get('updateKey'), true).length;
  }),

  isHidden: computed('model.[]','updateKey', 'totalUpdated', 'isIconOnly', function() {
    if (isEmpty(this.get('model'))) {
      return true;
    }
    let hasUpdate = this.get('model')[this.get('updateKey')] || false;
    return (this.get('isIconOnly')) ? !hasUpdate : this.get('totalUpdated') === 0;
  })
});
