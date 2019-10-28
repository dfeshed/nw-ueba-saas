import Component from '@ember/component';
import layout from './template';
import { htmlSafe } from '@ember/string';
import { connect } from 'ember-redux';
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
  isListView
} from 'rsa-list-manager/selectors/list-manager/selectors';

const stateToComputed = (state, attrs) => ({
  isExpanded: isExpanded(state, attrs.stateLocation),
  selectedItemId: selectedItemId(state, attrs.stateLocation),
  isListView: isListView(state, attrs.stateLocation)
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
    return htmlSafe(`top: ${elRect.height - 1}px; left: 2px;  min-width: ${elRect.width - 2}px`);
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

  actions: {
    collapseManagerList(e) {
      // do not close list manager if user clicked on search input field of power select
      if (e.target.classList.contains('ember-power-select-search-input')) {
        if (e.target.type === 'search' && e.target.tagName === 'INPUT') {
          return;
        }
      }
      this.send('closeListManager', this.get('stateLocation'));
    },

    listOpened() {
      this.set('offsetsStyle', menuOffsetsStyle(this.get('element')));
    },

    handleItemUpdate(item) {
      const selectedItemId = this.get('selectedItemId');
      // if a selectedItem was updated, itemSelection should re-execute to reflect the changes
      if (selectedItemId === item.id) {
        this.get('itemSelection')(item);
        this.send('toggleListVisibility', this.get('stateLocation'));
      }
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
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(ListManagerContainer);
