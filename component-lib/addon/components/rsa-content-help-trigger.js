import Ember from 'ember';
import layout from '../templates/components/rsa-content-help-trigger';

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

  classNames: ['rsa-content-help-trigger'],

  classNameBindings: ['tooltip'],

  eventBus: service(),

  tooltip: null,

  displayDelay: 1000,

  hideDelay: 1000,

  displayEvent: null,

  triggerEvent: 'hover', // [ 'click', 'hover']

  isClick: equal('triggerEvent', 'click'),

  isHover: equal('triggerEvent', 'hover'),

  mouseEnter() {
    if (this.get('isHover')) {
      let displayEvent = later(()=> {
        let height = this.$().height();
        let width = this.$().width();
        this.get('eventBus').trigger(`rsa-content-tooltip-display-${this.get('tooltip')}`, height, width, this.get('elementId'));
      }, this.get('displayDelay'));
      this.set('displayEvent', displayEvent);
    }
  },

  mouseLeave() {
    if (this.get('isHover')) {
      cancel(this.get('displayEvent'));
      later(()=> {
        this.get('eventBus').trigger(`rsa-content-tooltip-hide-${this.get('tooltip')}`);
      }, this.get('hideDelay'));
    }
  },

  click() {
    if (this.get('isClick')) {
      let height = this.$().height();
      let width = this.$().width();
      this.get('eventBus').trigger(`rsa-content-tooltip-toggle-${this.get('tooltip')}`, height, width, this.get('elementId'));
    }
  }

});
