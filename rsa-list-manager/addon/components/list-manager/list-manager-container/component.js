import Component from '@ember/component';
import layout from './template';
import { htmlSafe } from '@ember/string';
import { connect } from 'ember-redux';
import {
  LIST_MANAGER_CONTAINER_COMPONENT_PATH as COMPONENT_PATH,
  LIST_VIEW,
  EDIT_VIEW
} from 'rsa-list-manager/constants/list-manager';
import {
  setHighlightedIndex,
  toggleListVisibility,
  setSelectedItem,
  viewChanged
} from 'rsa-list-manager/actions/creators/creators';
import {
  listName,
  itemType,
  isExpanded,
  selectedItem,
  viewName,
  isListView,
  caption,
  titleTooltip
} from 'rsa-list-manager/selectors/list-manager/selectors';

const stateToComputed = (state, attrs) => ({
  listName: listName(state, attrs.listLocation),
  isExpanded: isExpanded(state, attrs.listLocation),
  itemType: itemType(state, attrs.listLocation),
  caption: caption(state, attrs.listLocation),
  titleTooltip: titleTooltip(state, attrs.listLocation),
  selectedItem: selectedItem(state, attrs.listLocation),
  viewName: viewName(state, attrs.listLocation),
  isListView: isListView(state, attrs.listLocation)
});

const dispatchToActions = {
  setHighlightedIndex,
  toggleListVisibility,
  setSelectedItem,
  viewChanged
};

const menuOffsetsStyle = (el) => {
  if (el) {
    const elRect = el.getBoundingClientRect();
    return htmlSafe(`top: ${elRect.height - 1}px; min-width: ${elRect.width - 2}px`);
  } else {
    return null;
  }
};

const ListManagerContainer = Component.extend({
  layout,
  classNames: ['list-manager-container'],
  listFilterComponent: `${COMPONENT_PATH}/list-filter`,
  itemListComponent: `${COMPONENT_PATH}/item-list`,
  itemDetailsComponent: `${COMPONENT_PATH}/item-details`,
  listLocation: undefined,

  // item rendered for details
  itemForEdit: null,

  // style for the recon-button-menu derived from the buttonGroup style
  offsetsStyle: null,

  _updateView(viewName) {
    this.send('viewChanged', viewName, this.get('listLocation'));
  },

  actions: {
    collapseManagerList() {
      if (this.get('isExpanded')) {
        this.send('toggleListVisibility', this.get('listLocation'));
      }
    },

    toggleExpandManagerList() {
      this.set('offsetsStyle', menuOffsetsStyle(this.get('element')));
      this.send('toggleListVisibility', this.get('listLocation'));
    },

    handleItemSelection(item) {
      const selectedItem = this.get('selectedItem');
      // Some types of lists don't have an active/selected item, in those cases once
      // the selection is processed the list-manager returns to an unselected state
      if (!selectedItem || (selectedItem && selectedItem.id !== item.id)) {
        this.get('itemSelection')(item);
        this.send('setSelectedItem', item, this.get('listLocation'));
      }
      this.send('toggleListVisibility', this.get('listLocation'));
    },

    editItem(item) {
      this.set('itemForEdit', item);
      this._updateView(EDIT_VIEW);
    },

    detailsDone() {
      this._updateView(LIST_VIEW);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(ListManagerContainer);
