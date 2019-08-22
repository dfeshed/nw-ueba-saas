import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';

export default Component.extend({
  layout,
  tagName: 'ul',
  classNames: ['rsa-item-list'],
  listName: null,
  list: null,
  selectedItem: null,
  currentIndex: -1,

  @computed('list')
  hasOOTBIndicators(list) {
    const ootbIndicatedItems = list.filter((item) => typeof item.ootb !== 'undefined');
    return ootbIndicatedItems.length > 0;
  },

  @computed('listName')
  noResultsMessage(listName) {
    return `All ${listName.toLowerCase()} have been excluded by the current filter`;
  },

  didInsertElement() {
    this._super(...arguments);
    const _boundKeyUpListener = this._onKeyUp.bind(this);
    this.set('_boundKeyUpListener', _boundKeyUpListener);
    window.addEventListener('keyup', _boundKeyUpListener);
  },

  willDestroyElement() {
    this._super(...arguments);
    window.removeEventListener('keyup', this.get('_boundKeyUpListener'));
  },

  /**
   * @description Respond to the user keyboard actions on options
   * @public
   */
  _onKeyUp(e) {
    if (this.get('isExpanded')) {
      if (e.keyCode === 38) {
        // Up Arrow - select previous item
        this.selectPrevious();
      } else if (e.keyCode === 40) {
        // Down Arrow - select next item
        this.selectNext();
      } else if (e.keyCode === 13) {
        // ENTER key - select the item at currentIndex
        // if not already selected
        const { selectedItem, list, currentIndex } = this.getProperties('selectedItem', 'list', 'currentIndex');
        const selectedIndex = list.findIndex((item) => item.id === selectedItem.id);
        if (currentIndex !== selectedIndex) {
          this.get('itemSelection')(list[currentIndex]);
        }
        this.toggleProperty('isExpanded');
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
    const { selectedItem, list, currentIndex } = this.getProperties('selectedItem', 'list', 'currentIndex');
    let selectedItemIndex;

    // start at selected index
    if (currentIndex < 0 && selectedItem) {
      this.set('currentIndex', list.findIndex((item) => item.id === selectedItem.id));
    }

    // if at the last item, go to the first item
    if (this.get('currentIndex') === (list.get('length') - 1)) {
      selectedItemIndex = 0;
    } else {
      selectedItemIndex = this.get('currentIndex') + 1;
    }
    this.set('currentIndex', selectedItemIndex);
    this.element.querySelector(`li:nth-of-type(${selectedItemIndex + 1})`).focus();
  },

  /**
   * @description Respond to the user pressing up on the keyboard
   * if nothing is selected, select last option
   * if first option is selected, select last option
   * @public
   */
  selectPrevious() {
    const { selectedItem, list, currentIndex } = this.getProperties('selectedItem', 'list', 'currentIndex');
    let selectedItemIndex;

    // start at selected index
    if (currentIndex < 0 && selectedItem) {
      this.set('currentIndex', list.findIndex((item) => item.id === selectedItem.id));
    }
    // if at the first item, go to the last item
    if (this.get('currentIndex') < 1) {
      selectedItemIndex = list.get('length') - 1;
    } else {
      selectedItemIndex = this.get('currentIndex') - 1;
    }
    this.set('currentIndex', selectedItemIndex);
    this.element.querySelector(`li:nth-of-type(${selectedItemIndex + 1})`).focus();
  }
});
