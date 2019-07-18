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
  tagName: 'hbox',
  classNames: ['list-manager'],
  listName: null,
  selectedItem: null,
  list: null,
  offsetsStyle: null,
  isExpanded: false,

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
    }

  }
});
