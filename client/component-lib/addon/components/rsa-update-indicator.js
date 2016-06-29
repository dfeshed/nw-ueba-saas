import Ember from 'ember';
import layout from '../templates/components/rsa-update-indicator';

export default Ember.Component.extend({
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

  totalUpdated: Ember.computed('updateKey', 'model.[]', function() {
    return Ember.isEmpty(this.get('model')) ? 0 : this.get('model').filterBy(this.get('updateKey'), true).length;
  }),

  isHidden: Ember.computed('updateKey', 'totalUpdated', 'isIconOnly', function() {
    let hasUpdate = this.get('model')[this.get('updateKey')] || false;
    return (this.get('isIconOnly')) ? !hasUpdate : this.get('totalUpdated') === 0;
  })
});
