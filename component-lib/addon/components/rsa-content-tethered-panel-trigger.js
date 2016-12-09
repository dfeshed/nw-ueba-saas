import Ember from 'ember';
import layout from '../templates/components/rsa-content-tethered-panel-trigger';

const {
  Component,
  inject: {
    service
  },
  run: {
    later,
    cancel
  },
  computed: {
    equal
  }

} = Ember;

export default Component.extend({

  tagName: 'span',

  layout,

  classNames: ['rsa-content-tethered-panel-trigger'],

  classNameBindings: ['panel'],

  eventBus: service(),

  panel: null,

  displayDelay: 1000,

  hideDelay: 1000,

  displayEvent: null,

  triggerEvent: 'hover', // [ 'click', 'hover']

  isClick: equal('triggerEvent', 'click'),

  isHover: equal('triggerEvent', 'hover'),

  isDisabled: false,

  mouseEnter() {
    if (!this.get('isDisabled')) {
      if (this.get('isHover')) {
        const displayEvent = later(()=> {
          const height = this.$().height();
          const width = this.$().width();
          this.get('eventBus').trigger(`rsa-content-tethered-panel-display-${this.get('panel')}`, height, width, this.get('elementId'));
        }, this.get('displayDelay'));
        this.set('displayEvent', displayEvent);
      }
    }
  },

  mouseLeave() {
    if (this.get('isHover')) {
      cancel(this.get('displayEvent'));
      later(()=> {
        this.get('eventBus').trigger(`rsa-content-tethered-panel-hide-${this.get('panel')}`);
      }, this.get('hideDelay'));
    }
  },

  click() {
    if (!this.get('isDisabled')) {
      if (this.get('isClick')) {
        const height = this.$().height();
        const width = this.$().width();
        this.get('eventBus').trigger(`rsa-content-tethered-panel-toggle-${this.get('panel')}`, height, width, this.get('elementId'));
      }
    }
  }

});
