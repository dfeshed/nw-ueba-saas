import Component from '@ember/component';
import { equal } from 'ember-computed';
import { run } from '@ember/runloop';
import { inject as service } from '@ember/service';
import layout from './template';
import { sendTetherEvent } from 'component-lib/utils/tooltip-trigger';

export default Component.extend({
  tagName: 'span',
  layout,
  classNames: ['rsa-content-tethered-panel-trigger'],
  classNameBindings: ['panel'],

  eventBus: service(),

  displayDelay: 1000,
  displayEvent: null,
  hideDelay: 1000,
  isDisabled: false,
  // optional arbitrary data, passed from trigger to tooltip via eventBus
  // useful when re-using 1 tooltip instance with multiple triggers
  model: null,
  panel: null,
  triggerEvent: 'hover', // [ 'click', 'hover']

  isClick: equal('triggerEvent', 'click'),
  isHover: equal('triggerEvent', 'hover'),

  mouseEnter() {
    if (!this.get('isDisabled')) {
      if (this.get('isHover')) {
        if (this.get('hideEvent')) {
          run.cancel(this.get('hideEvent'));
          this.set('hideEvent', null);
        }
        const displayEvent = run.later(() => {
          if (this.get('isDestroying') || this.get('isDestroyed') || !this.element) {
            // The element has been destroyed since the time when the delay started
            return;
          }
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
        run.cancel(this.get('displayEvent'));
        this.set('displayEvent', null);
      }
      const hideEvent = run.later(() => {
        if (!this.get('isDestroying') && !this.get('isDestroyed') && this.element) {
          sendTetherEvent(
            this.element,
            this.get('panel'),
            this.get('eventBus'),
            'hide'
          );
        }
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
