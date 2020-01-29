/* eslint-disable */

// COPIED FROM
// https://raw.githubusercontent.com/cibernox/ember-power-select/master/addon/components/power-select/options.js

import Component from '@ember/component';
import { computed } from '@ember/object';
// import layout from '../../templates/components/power-select/options';

const isTouchDevice = (!!window && 'ontouchstart' in window);
(function(ElementProto) {
  if (typeof ElementProto.matches !== 'function') {
    ElementProto.matches = ElementProto.msMatchesSelector || ElementProto.mozMatchesSelector || ElementProto.webkitMatchesSelector;
  }

  if (typeof ElementProto.closest !== 'function') {
    ElementProto.closest = function closest(selector) {
      let element = this;
      while (element && element.nodeType === 1) {
        if (element.matches(selector)) {
          return element;
        }
        element = element.parentNode;
      }
      return null;
    };
  }
})(window.Element.prototype);

let mouseEnterFn, mouseLeaveFn;

export default Component.extend({
  isTouchDevice,
  // layout,
  tagName: 'ul',
  attributeBindings: ['role', 'aria-controls'],
  role: 'listbox',
  titleAttribute: null,

  // Lifecycle hooks
  didInsertElement() {
    this._super(...arguments);
    if (this.get('role') === 'group') {
      return;
    }
    let findOptionAndPerform = (action, e) => {
      let optionItem = e.target.closest('[data-option-index]');
      if (!optionItem) {
        return;
      }
      if (optionItem.closest('[aria-disabled=true]')) {
        return; // Abort if the item or an ancestor is disabled
      }
      let optionIndex = optionItem.getAttribute('data-option-index');
      action(this._optionFromIndex(optionIndex), e);
    };

    // DKB, I ADDED THIS TO ORIGINAL CODE
    this.element.addEventListener('mousedown', (e) => {
      e.preventDefault();
      const optionMouseDownCallback = this.get('onmousedown');
      if (optionMouseDownCallback) {
        optionMouseDownCallback(e);
      }
    });
    // DKB, END ADDED TO ORIGINAL CODE

    this.element.addEventListener('mouseup', (e) => findOptionAndPerform(this.get('select.actions.choose'), e));
    this.element.addEventListener('mouseover', (e) => findOptionAndPerform(this.get('select.actions.highlight'), e));

    mouseEnterFn = this.handleMouseEnter.bind(this);
    mouseLeaveFn = this.handleMouseLeave.bind(this);

    this.element.addEventListener('mouseenter', mouseEnterFn);
    this.element.addEventListener('mouseleave', mouseLeaveFn);

    if (this.get('isTouchDevice')) {
      this._addTouchEvents();
    }
    if (this.get('role') !== 'group') {
      let select = this.get('select');
      select.actions.scrollTo(select.highlighted);
    }
  },

  // GTB, I ADDED THIS TO ORIGINAL CODE
  // Event handlers
  handleMouseEnter(e) {
    const optionMouseEnterCallback = this.get('onmouseenter');
    if (optionMouseEnterCallback) {
      optionMouseEnterCallback(e);
    }
  },
  handleMouseLeave() {
    const select = this.get('select');
    select.actions.highlight(null);
  },
  // GTB, END ADDED TO ORIGINAL CODE

  // CPs
  'aria-controls': computed('select.uniqueId', function() {
    return `ember-power-select-trigger-${this.get('select.uniqueId')}`;
  }),

  // Methods
  _addTouchEvents() {
    let touchMoveHandler = () => {
      this.hasMoved = true;
      if (this.element) {
        this.element.removeEventListener('touchmove', touchMoveHandler);
      }
    };
    // Add touch event handlers to detect taps
    this.element.addEventListener('touchstart', () => {
      this.element.addEventListener('touchmove', touchMoveHandler);
    });
    this.element.addEventListener('touchend', (e) => {
      let optionItem = e.target.closest('[data-option-index]');

      if (!optionItem) {
        return;
      }

      e.preventDefault();
      if (this.hasMoved) {
        this.hasMoved = false;
        return;
      }

      let optionIndex = optionItem.getAttribute('data-option-index');
      this.get('select.actions.choose')(this._optionFromIndex(optionIndex), e);
    });
  },

  _optionFromIndex(index) {
    let parts = index.split('.');
    let options = this.get('options');
    let option = options[parseInt(parts[0], 10)];
    for (let i = 1; i < parts.length; i++) {
      option = option.options[parseInt(parts[i], 10)];
    }
    return option;
  },

  willDestroyElement() {
    this._super(...arguments);

    this.element.removeEventListener('mouseenter', mouseEnterFn);
    this.element.removeEventListener('mouseleave', mouseLeaveFn);

    mouseEnterFn = null;
    mouseLeaveFn = null;
  }
});
