import Ember from 'ember';

const {
  Mixin,
  inject: {
    service
  },
  run: {
    later
  }
} = Ember;

export default Mixin.create({

  classNameBindings: ['tooltip'],

  eventBus: service(),

  tooltip: null,

  displayDelay: 1000,

  hideDelay: 1000,

  mouseEnter() {
    later(()=> {
      this.get('eventBus').trigger(`rsa-content-tooltip-display-${this.get('tooltip')}`);
    }, this.get('displayDelay'));
  },

  mouseLeave() {
    later(()=> {
      this.get('eventBus').trigger(`rsa-content-tooltip-hide-${this.get('tooltip')}`);
    }, this.get('hideDelay'));
  }

});
