import Ember from 'ember';
import layout from '../templates/components/rsa-content-tethered-panel-trigger';
import { sendTetherEvent } from 'component-lib/utils/tooltip-trigger';

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

  // optional arbitrary data, passed from trigger to tooltip via eventBus
  // useful when re-using 1 tooltip instance with multiple triggers
  model: null,

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
        if (this.get('hideEvent')) {
          cancel(this.get('hideEvent'));
          this.set('hideEvent', null);
        }
        const displayEvent = later(() => {
          sendTetherEvent(
            this.element,
            this.get('panel'),
            this.get('eventBus'),
            'display',
            this.get('model')
          );
        }, this.get('displayDelay'));
        this.set('displayEvent', displayEvent);
      }
    }
  },

  mouseLeave() {
    if (this.get('isHover')) {
      if (this.get('displayEvent')) {
        cancel(this.get('displayEvent'));
        this.set('displayEvent', null);
      }
      const hideEvent = later(() => {
        sendTetherEvent(
          this.element,
          this.get('panel'),
          this.get('eventBus'),
          'hide'
        );
      }, this.get('hideDelay'));
      this.set('hideEvent', hideEvent);
    }
  },

  click() {
    if (!this.get('isDisabled')) {
      if (this.get('isClick')) {
        sendTetherEvent(
          this.element,
          this.get('panel'),
          this.get('eventBus'),
          'toggle',
          this.get('model')
        );
      }
    }
  }
});
