import Ember from 'ember';

export default Ember.Mixin.create({

  classNameBindings: ['tooltip'],

  eventBus: Ember.inject.service('event-bus'),

  tooltip: null,

  triggerEvent: 'hover', // ['hover', 'click']

  mouseEnter() {
    if (this.get('triggerEvent') === 'hover') {
      this.get('eventBus').trigger(`rsa-content-tooltip-display-${this.get('tooltip')}`, this.get('triggerEvent'));
    }
  },

  mouseLeave() {
    if (this.get('triggerEvent') === 'hover') {
      this.get('eventBus').trigger(`rsa-content-tooltip-hide-${this.get('tooltip')}`, this.get('triggerEvent'));
    }
  },

  click() {
    if (this.get('triggerEvent') === 'click') {
      this.get('eventBus').trigger(`rsa-content-tooltip-toggle-${this.get('tooltip')}`, this.get('triggerEvent'));
    }
  }
});
