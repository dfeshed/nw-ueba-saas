import Component from '@ember/component';
import layout from './template';
import { htmlSafe } from '@ember/string';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import {
  setHighlightedIndex,
  toggleListVisibility
} from 'rsa-list-manager/actions/creators/creators';
import {
  listName,
  itemType,
  isExpanded
} from 'rsa-list-manager/selectors/list-manager/selectors';

const stateToComputed = (state, attrs) => ({
  listName: listName(state, attrs.listLocation),
  isExpanded: isExpanded(state, attrs.listLocation),
  itemType: itemType(state, attrs.listLocation)
});

const dispatchToActions = {
  setHighlightedIndex,
  toggleListVisibility
};

const menuOffsetsStyle = (el) => {
  if (el) {
    const elRect = el.getBoundingClientRect();
    return htmlSafe(`top: ${elRect.height - 1}px; min-width: ${elRect.width - 2}px`);
  } else {
    return null;
  }
};

const COMPONENT_PATH = 'list-manager/list-manager-container';

const ListManagerContainer = Component.extend({
  layout,
  classNames: ['list-manager-container'],
  listFilterComponent: `${COMPONENT_PATH}/list-filter`,
  itemListComponent: `${COMPONENT_PATH}/item-list`,
  itemDetailsComponent: `${COMPONENT_PATH}/item-details`,
  listLocation: undefined,

  // Object to identify an item as selected in the manager's button caption
  selectedItem: null,

  // View to be rendered through button actions (list-view, detail-view, etc)
  viewName: null,

  // item rendered for details
  itemForEdit: null,

  // style for the recon-button-menu derived from the buttonGroup style
  offsetsStyle: null,

  didInsertElement() {
    this.set('viewName', 'list-view');
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
        this.send('toggleListVisibility', this.get('listLocation'));
      }
    },

    toggleExpandManagerList() {
      this.set('offsetsStyle', menuOffsetsStyle(this.get('element')));
      this.set('viewName', 'list-view');
      this.send('toggleListVisibility', this.get('listLocation'));
    },

    handleItemSelection(item) {
      const selectedItem = this.get('selectedItem');
      // Some types of lists don't have an active/selected item, in those cases once
      // the selection is processed the list-manager returns to an unselected state
      if (!selectedItem || (selectedItem && selectedItem.id !== item.id)) {
        this.get('itemSelection')(item);
      }
      this.send('toggleListVisibility', this.get('listLocation'));
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

export default connect(stateToComputed, dispatchToActions)(ListManagerContainer);
