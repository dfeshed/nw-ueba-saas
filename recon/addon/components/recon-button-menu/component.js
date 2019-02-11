import Component from '@ember/component';
import layout from './template';
import $ from 'jquery';

export default Component.extend({
  layout,
  tagName: 'ul',
  classNames: ['recon-button-menu'],
  classNameBindings: ['isExpanded:expanded:collapsed', 'menuStyle'],
  attributeBindings: ['style'],
  style: null,
  isExpanded: false,
  menuStyle: null,
  currentIndex: -1,
  items: null,

  didInsertElement() {
    this._super(...arguments);
    const _boundKeyUpListener = this._onKeyUp.bind(this);
    this.set('_boundKeyUpListener', _boundKeyUpListener);
    window.addEventListener('keyup', _boundKeyUpListener);

    const _boundMouseOverListener = this._onMouseOver.bind(this);
    this.set('_boundMouseOverListener', _boundMouseOverListener);
    window.addEventListener('mouseover', _boundMouseOverListener);
  },

  willDestroyElement() {
    this._super(...arguments);
    window.removeEventListener('keyup', this.get('_boundKeyUpListener'));
    window.removeEventListener('mouseover', this.get(' _boundMouseOverListener'));
  },

  /**
   * @description Respond to the user keyboard actions on options
   * @public
   */
  _onKeyUp(e) {
    if (this.get('isExpanded')) {
      if (e.keyCode === 38) {
        this.selectPrevious();
      } else if (e.keyCode === 40) {
        this.selectNext();
      }
    }
  },

  /**
   * @description Respond to the user mouse hover on options
   * @public
   */
  _onMouseOver(e) {
    if (this.get('isExpanded')) {
      const el = e.target.closest('.recon-button-menu li');
      if (el) {
        this.set('currentIndex', $(el).index());
        el.focus();
      }
    }
  },

  /**
   * @description Respond to the user pressing down on the keyboard
   * if nothing is selected, select first option
   * if last option is selected, select first option
   * @public
   */
  selectNext() {
    let selectedItemIndex;
    const items = this.get('items');

    if (this.get('currentIndex') === (items.get('length') - 1)) {
      selectedItemIndex = 0;
    } else {
      selectedItemIndex = this.get('currentIndex') + 1;
    }
    this.set('currentIndex', selectedItemIndex);

    $(`.recon-button-menu li:nth-of-type(${selectedItemIndex + 1})`).focus();
  },

  /**
   * @description Respond to the user pressing up on the keyboard
   * if nothing is selected, select last option
   * if first option is selected, select last option
   * @public
   */
  selectPrevious() {
    let selectedItemIndex;

    if (this.get('currentIndex') < 1) {
      const items = this.get('items');
      selectedItemIndex = items.get('length') - 1;
    } else {
      selectedItemIndex = this.get('currentIndex') - 1;
    }
    this.set('currentIndex', selectedItemIndex);

    $(`.recon-button-menu li:nth-of-type(${selectedItemIndex + 1})`).focus();
  }

});
