import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { editItem } from 'rsa-list-manager/actions/creators/creators';

const dispatchToActions = {
  editItem
};

const Item = Component.extend({
  layout,
  tagName: 'li',
  classNames: ['rsa-list-item'],
  classNameBindings: ['isSelected', 'isHighlighted'],
  attributeBindings: ['tabindex'],
  tabindex: -1,
  stateLocation: null,
  item: null,
  selectedItemId: null,
  highlightedId: null,

  // Item that may be currently applied
  @computed('selectedItemId', 'item')
  isSelected(selectedItemId, item) {
    return selectedItemId && item ? selectedItemId === item.id : false;
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
      this.send('editItem', this.get('item').id, this.get('stateLocation'));
    }
  }
});

export default connect(undefined, dispatchToActions)(Item);
