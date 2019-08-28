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

  /*
   * Name identifying the list used to label buttons in the manager.
   * Name should sound plural, ending in 's'
   */
  listName: null,

  // Object to identify an item as selected in the manager's button caption
  selectedItem: null,

  // the original list
  list: null,

  // list rendered on filtering
  filteredList: null,

  // highlightedIndex in list
  highlightedIndex: -1,

  // View to be rendered through button actions (list-view, detail-view, etc)
  viewName: null,

  // item rendered for details
  itemForEdit: null,

  // style for the recon-button-menu derived from the buttonGroup style
  offsetsStyle: null,

  isExpanded: false,

  didInsertElement() {
    this.set('filteredList', this.get('list'));
    this.set('viewName', 'list-view');
  },

  /*
   * For a listName eg Column Groups, item type will be Column Group
   */
  @computed('listName')
  itemType(listName) {
    return listName.slice(0, -1);
  },

  @computed('listName', 'selectedItem')
  caption(listName, selectedItem) {
    // If there is a selected item for a listName e.g My Items (string ending with s(plural)),
    // the caption dispalyed will be My Item: selectedItemName
    if (selectedItem) {
      return `${this.get('itemType')}: ${selectedItem.name}`;
    }
    return listName;
  },

  @computed('viewName')
  isListView(viewName) {
    return viewName === 'list-view';
  },

  @computed('selectedItem')
  titleTooltip(selectedItem) {
    if (selectedItem) {
      return selectedItem.name;
    }
    return null;
  },

  _updateView(viewName) {
    this.set('viewName', viewName);
  },

  actions: {
    collapseManagerList() {
      if (this.get('isExpanded')) {
        this.toggleProperty('isExpanded');
      }
    },

    toggleExpandManagerList() {
      this.set('offsetsStyle', menuOffsetsStyle(this.get('element')));
      this.set('viewName', 'list-view');
      this.toggleProperty('isExpanded');
      this.set('highlightedIndex', -1);
    },

    updateFilteredList(newList) {
      this.set('filteredList', newList);
    },

    resetHighlightedIndex() {
      this.set('highlightedIndex', -1);
    },

    editItem(item) {
      this.set('itemForEdit', item);
      this._updateView('edit-view');
    },

    detailsDone() {
      this._updateView('list-view');
    }

  }
});
