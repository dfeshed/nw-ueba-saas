import Component from '@ember/component';
import layout from './template';
import { htmlSafe } from '@ember/string';
import { connect } from 'ember-redux';
import {
  EDIT_VIEW
} from 'rsa-list-manager/constants/list-manager';
import {
  setHighlightedIndex,
  toggleListVisibility,
  setSelectedItem,
  viewChanged,
  closeListManager
} from 'rsa-list-manager/actions/creators/creators';
import {
  isExpanded,
  selectedItemId,
  isListView,
  caption,
  titleTooltip,
  editItem
} from 'rsa-list-manager/selectors/list-manager/selectors';

const stateToComputed = (state, attrs) => ({
  isExpanded: isExpanded(state, attrs.stateLocation),
  caption: caption(state, attrs.stateLocation),
  titleTooltip: titleTooltip(state, attrs.stateLocation),
  selectedItemId: selectedItemId(state, attrs.stateLocation),
  isListView: isListView(state, attrs.stateLocation),
  editItem: editItem(state, attrs.stateLocation) // item rendered for details
});

const dispatchToActions = {
  setHighlightedIndex,
  toggleListVisibility,
  setSelectedItem,
  viewChanged,
  closeListManager
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
  stateLocation: undefined,

  // style for the recon-button-menu derived from the buttonGroup style
  offsetsStyle: null,

  _updateView(viewName) {
    this.send('viewChanged', viewName, this.get('stateLocation'));
  },

  actions: {
    collapseManagerList() {
      this.send('closeListManager', this.get('stateLocation'));
    },

    listOpened() {
      this.set('offsetsStyle', menuOffsetsStyle(this.get('element')));
    },

    handleItemSelection(item) {
      const selectedItemId = this.get('selectedItemId');
      // Some types of lists don't have an active/selected item, in those cases once
      // the selection is processed the list-manager returns to an unselected state
      if (selectedItemId !== item.id) {
        this.get('itemSelection')(item);
        this.send('setSelectedItem', item, this.get('stateLocation'));
      }
      this.send('toggleListVisibility', this.get('stateLocation'));
    },

    editItem(item) {
      this.set('itemForEdit', item);
      this._updateView(EDIT_VIEW);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(ListManagerContainer);
