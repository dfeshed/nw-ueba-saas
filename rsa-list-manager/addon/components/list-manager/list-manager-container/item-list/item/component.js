import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import {
  selectedItem,
  highlightedId,
  hasIsEditableIndicators
} from 'rsa-list-manager/selectors/list-manager/selectors';

const stateToComputed = (state, attrs) => ({
  selectedItem: selectedItem(state, attrs.listLocation),
  highlightedId: highlightedId(state, attrs.listLocation),
  hasIsEditableIndicators: hasIsEditableIndicators(state, attrs.listLocation)
});

const Item = Component.extend({
  layout,
  tagName: 'li',
  classNames: ['rsa-list-item'],
  classNameBindings: ['isSelected', 'isHighlighted'],
  attributeBindings: ['tabindex'],
  tabindex: -1,
  listLocation: undefined,
  item: null,
  editItem: null,

  // Item that may be currently applied
  @computed('selectedItem', 'item')
  isSelected(selectedItem, item) {
    return selectedItem && item ? selectedItem.id == item.id : false;
  },

  @computed('highlightedId', 'item')
  isHighlighted(highlightedId, item) {
    return item && highlightedId ? highlightedId === item.id : false;
  },

  @computed('item')
  iconName(item) {
    if (!item.isEditable) {
      return 'lock-close-1';
    }
    return 'settings-1';
  },

  actions: {
    editDetails() {
      this.get('editItem')(this.get('item'));
    }
  }
});

export default connect(stateToComputed)(Item);
