import { computed } from '@ember/object';
import Component from '@ember/component';
import { inject as service } from '@ember/service';
import layout from './template';
import { connect } from 'ember-redux';
import {
  setHighlightedIndex,
  listVisibilityToggled,
  setSelectedItem,
  viewChanged
} from 'rsa-list-manager/actions/creators/creators';
import {
  selectedItemId,
  isListView
} from 'rsa-list-manager/selectors/list-manager/selectors';

const stateToComputed = (state, attrs) => ({
  selectedItemId: selectedItemId(state, attrs.stateLocation),
  isListView: isListView(state, attrs.stateLocation)
});

const dispatchToActions = {
  setHighlightedIndex,
  listVisibilityToggled,
  setSelectedItem,
  viewChanged
};

export const queryTabClicked = (target) => {
  return target.classList.contains('tabrow') ||
    (target.parentElement && queryTabClicked(target.parentElement));
};

export const afterOptionsClicked = (target) => {
  return target.classList.contains('ember-power-select-after-options') ||
  (target.parentElement && afterOptionsClicked(target.parentElement));
};

const ListManagerContainer = Component.extend({
  layout,
  classNames: ['list-manager-container'],
  eventBus: service(),
  stateLocation: undefined,
  itemSelection: () => {},

  // for rsa-content-tethered-panel
  panelId: computed(function() {
    return `listManager-${this.get('elementId')}`;
  }),

  actions: {
    handleItemUpdate(item) {
      const selectedItemId = this.get('selectedItemId');
      // if a selectedItem was updated, itemSelection should re-execute to reflect the changes
      if (selectedItemId === item.id) {
        this.get('itemSelection')(item);
        this.get('eventBus').trigger(`rsa-content-tethered-panel-toggle-${this.get('panelId')}`);
        this.send('listVisibilityToggled', this.get('stateLocation'));
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
      // close tethered panel
      this.get('eventBus').trigger(`rsa-content-tethered-panel-toggle-${this.get('panelId')}`);
      this.send('listVisibilityToggled', this.get('stateLocation'));
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(ListManagerContainer);
