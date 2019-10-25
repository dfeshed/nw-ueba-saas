import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';
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
  @computed('selectedItemId', 'item')
  isSelected(selectedItemId, item) {
    return selectedItemId && item ? selectedItemId === item.id : false;
  },

  @computed('highlightedId', 'item')
  isHighlighted(highlightedId, item) {
    return item && highlightedId ? highlightedId === item.id : false;
  },

  @computed('item')
  contentType(item) {
    const contentType = item.isEditable ? 'user-created' : 'built-in';
    const i18n = this.get('i18n');

    const toolTip = i18n.t(`contentType.${contentType}.tooltip`);
    const typeIcon = i18n.t(`contentType.${contentType}.icon`);
    const detailsIcon = i18n.t(`contentType.${contentType}.detailsIcon`);
    return { toolTip, typeIcon, detailsIcon };
  },

  actions: {
    editDetails() {
      this.send('beginEditItem', this.get('item').id, this.get('stateLocation'));
    }
  }
});

export default connect(undefined, dispatchToActions)(Item);
