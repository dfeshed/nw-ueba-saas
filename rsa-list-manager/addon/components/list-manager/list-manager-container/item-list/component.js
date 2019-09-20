import Component from '@ember/component';
import layout from './template';
import { connect } from 'ember-redux';
import {
  setHighlightedIndex
} from 'rsa-list-manager/actions/creators/creators';
import {
  highlightedIndex,
  isExpanded,
  filteredList,
  selectedItemId,
  selectedIndex,
  noResultsMessage,
  hasIsEditableIndicators,
  highlightedId
} from 'rsa-list-manager/selectors/list-manager/selectors';

const stateToComputed = (state, attrs) => ({
  filteredList: filteredList(state, attrs.stateLocation),
  isExpanded: isExpanded(state, attrs.stateLocation),
  highlightedIndex: highlightedIndex(state, attrs.stateLocation),
  highlightedId: highlightedId(state, attrs.stateLocation),
  selectedItemId: selectedItemId(state, attrs.stateLocation),
  selectedIndex: selectedIndex(state, attrs.stateLocation),
  noResultsMessage: noResultsMessage(state, attrs.stateLocation),
  hasIsEditableIndicators: hasIsEditableIndicators(state, attrs.stateLocation)
});

const dispatchToActions = {
  setHighlightedIndex
};

const ItemList = Component.extend({
  layout,
  tagName: 'ul',
  classNames: ['rsa-item-list'],
  stateLocation: undefined,
  onMouse: null, // true if user is using the mouse to navigate

  didInsertElement() {
    this._super(...arguments);

    const _boundKeyUpListener = this._onKeyUp.bind(this);
    this.set('_boundKeyUpListener', _boundKeyUpListener);
    window.addEventListener('keyup', _boundKeyUpListener);

    const _boundMouseOverListener = this._onMouseOver.bind(this);
    this.set('_boundMouseOverListener', _boundMouseOverListener);
    window.addEventListener('mouseover', _boundMouseOverListener);

    const _boundMouseMoveListener = this._onMouseMove.bind(this);
    this.set('_boundMouseMoveListener', _boundMouseMoveListener);
    window.addEventListener('mousemove', _boundMouseMoveListener);
  },

  willDestroyElement() {
    this._super(...arguments);
    window.removeEventListener('keyup', this.get('_boundKeyUpListener'));
    window.removeEventListener('mouseover', this.get('_boundMouseOverListener'));
    window.removeEventListener('mousemove', this.get('_boundMouseMoveListener'));
  },

  /**
   * set onMouse to true
   * to switch from keyboard navigation
   */
  _onMouseMove() {
    this.set('onMouse', true);
  },

  /**
   * when hovering over an item
   * highlight the item, unless it is the selected item
   */
  _onMouseOver(e) {
    const { isExpanded, onMouse } = this.getProperties('isExpanded', 'onMouse');
    if (isExpanded && onMouse) {
      const el = e.target.closest('.rsa-item-list li');
      if (el) {
        const selectorAll = this.element.querySelectorAll('.rsa-item-list li');
        const newCurrentIndex = Array.from(selectorAll).indexOf(el);
        this.send('setHighlightedIndex', newCurrentIndex, this.get('stateLocation'));
        el.focus();
      }
    }
  },

  /**
   * respond to Up Arrow, Down Arrow, Enter keys
   */
  _onKeyUp(e) {
    if (this.get('isExpanded')) {
      // prevent mouse navigation triggered by mouseover while navigating with keyboard
      this.set('onMouse', false);
      const filterInFocus = document.activeElement === document.querySelector('.list-filter input');

      if (e.keyCode === 38) {
      // Up Arrow - select previous item
      // do nothing if user is at filter
        if (filterInFocus) {
          return;
        }
        this.selectPrevious();

      } else if (e.keyCode === 40) {
        // Down Arrow - select next item
        this.selectNext();

      } else if (e.keyCode === 13) {
        // do nothing if user is at filter
        if (filterInFocus) {
          return;
        }
        // ENTER key - select the item at highlightedIndex
        // if not already selected
        const { selectedIndex, filteredList, highlightedIndex } = this.getProperties('selectedIndex', 'filteredList', 'highlightedIndex');
        if (highlightedIndex !== selectedIndex) {
          this.get('itemSelection')(filteredList[highlightedIndex]);
        }
      }
    }
  },

  /**
    * if nothing is selected, select first option
    * if last option is selected, select first option
    */
  selectNext() {
    const { selectedIndex, filteredList, highlightedIndex } = this.getProperties('selectedIndex', 'filteredList', 'highlightedIndex');
    let nextIndex;
    const currentIndexIsLast = highlightedIndex === (filteredList.get('length') - 1);

    // if there is no highlightedIndex, start at the first item
    if (highlightedIndex < 0) {
      nextIndex = 0;
    } else {
      // if at the last item, go to the first item
      // else, go to the next item
      nextIndex = currentIndexIsLast ? 0 : this.get('highlightedIndex') + 1;
    }

    this.send('setHighlightedIndex', nextIndex, this.get('stateLocation'));

    // if item is already selected, go to the next item
    if (nextIndex === selectedIndex) {
      this.selectNext();
    } else {
      // focus
      this.element.querySelector(`li:nth-of-type(${nextIndex + 1})`).focus();
    }
  },

  /**
   * if nothing is selected, select last option
   * if first option is selected, select last option
   */
  selectPrevious() {
    const { selectedIndex, filteredList, highlightedIndex } = this.getProperties('selectedIndex', 'filteredList', 'highlightedIndex');
    let nextIndex;

    // if there is no highlightedIndex, start at the last item
    if (highlightedIndex < 0) {
      nextIndex = filteredList.length - 1;
    } else {
      // if at the first item, go to the last item
      // else, go to the previous item
      nextIndex = this.get('highlightedIndex') === 0 ? filteredList.length - 1 : this.get('highlightedIndex') - 1;
    }
    this.send('setHighlightedIndex', nextIndex, this.get('stateLocation'));

    // if item is already selected, go to the previous item
    if (nextIndex === selectedIndex) {
      this.selectPrevious();
    } else {
      // focus
      this.element.querySelector(`li:nth-of-type(${nextIndex + 1})`).focus();
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(ItemList);
