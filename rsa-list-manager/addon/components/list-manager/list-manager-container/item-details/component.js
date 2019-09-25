import Component from '@ember/component';
import layout from './template';
import { connect } from 'ember-redux';
import { itemType, editItem, isItemsLoading } from 'rsa-list-manager/selectors/list-manager/selectors';

const stateToComputed = (state, attrs) => ({
  itemType: itemType(state, attrs.stateLocation),
  item: editItem(state, attrs.stateLocation),
  isItemsLoading: isItemsLoading(state, attrs.stateLocation)
});

const ItemDetails = Component.extend({
  layout,
  classNames: ['item-details'],
  stateLocation: undefined
});

export default connect(stateToComputed)(ItemDetails);
