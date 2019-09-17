import Component from '@ember/component';
import layout from './template';
import { connect } from 'ember-redux';
import { itemType } from 'rsa-list-manager/selectors/list-manager/selectors';

const stateToComputed = (state, attrs) => ({
  itemType: itemType(state, attrs.listLocation)
});

const DetailsFooter = Component.extend({

  tagName: 'footer',
  layout,
  classNames: ['details-footer'],
  listLocation: undefined,
  item: null
});

export default connect(stateToComputed)(DetailsFooter);
