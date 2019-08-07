import Component from '@ember/component';
import layout from './template';
import { htmlSafe } from '@ember/string';
import computed from 'ember-computed-decorators';

const menuOffsetsStyle = (el) => {
  if (el) {
    const elRect = el.getBoundingClientRect();
    return htmlSafe(`top: ${elRect.height - 1}px; min-width: ${elRect.width - 2}px`);
  } else {
    return null;
  }
};

export default Component.extend({
  layout,
  classNames: ['list-manager'],
  listName: null,
  selectedItem: null,
  list: null,
  filteredList: null,

  // style for the recon-button-menu derived from the buttonGroup style
  offsetsStyle: null,

  isExpanded: false,

  didInsertElement() {
    this.set('filteredList', this.get('list'));
  },

  @computed('listName', 'selectedItem')
  caption(listName, selectedItem) {
    // If there is a selected item for a listName e.g My Items (string ending with s(plural)),
    // the caption dispalyed will be My Item: selectedItemName
    if (selectedItem) {
      return `${listName.slice(0, -1)}: ${selectedItem.name}`;
    }
    return listName;
  },

  @computed('selectedItem')
  titleTooltip(selectedItem) {
    if (selectedItem) {
      return selectedItem.name;
    }
    return null;
  },

  actions: {
    collapseMenu() {
      if (this.get('isExpanded')) {
        this.toggleProperty('isExpanded');
      }
    },

    toggleExpand() {
      this.set('offsetsStyle', menuOffsetsStyle(this.get('element')));
      this.toggleProperty('isExpanded');
    },

    updateFilteredList(newList) {
      this.set('filteredList', newList);
    }

  }
});
