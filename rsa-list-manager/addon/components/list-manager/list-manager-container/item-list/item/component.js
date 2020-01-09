import { computed } from '@ember/object';
import Component from '@ember/component';
import layout from './template';
import { connect } from 'ember-redux';
import { beginEditItem } from 'rsa-list-manager/actions/creators/creators';

const dispatchToActions = {
  beginEditItem
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
  isSelected: computed('selectedItemId', 'item', function() {
    return this.selectedItemId && this.item ? this.selectedItemId === this.item.id : false;
  }),

  isHighlighted: computed('highlightedId', 'item', function() {
    return this.item && this.highlightedId ? this.highlightedId === this.item.id : false;
  }),

  contentType: computed('item', function() {
    const contentType = this.item.isEditable ? 'user-created' : 'built-in';
    const i18n = this.get('i18n');

    const toolTip = i18n.t(`contentType.${contentType}.tooltip`);
    const typeIcon = i18n.t(`contentType.${contentType}.icon`);
    const detailsIcon = i18n.t(`contentType.${contentType}.detailsIcon`);
    return { toolTip, typeIcon, detailsIcon };
  }),

  actions: {
    editDetails() {
      this.send('beginEditItem', this.get('item').id, this.get('stateLocation'));
    }
  }
});

export default connect(undefined, dispatchToActions)(Item);
