import Component from '@ember/component';
import layout from './template';
import { connect } from 'ember-redux';
import { itemType } from 'rsa-list-manager/selectors/list-manager/selectors';
import { viewChanged } from 'rsa-list-manager/actions/creators/creators';
import { LIST_VIEW } from 'rsa-list-manager/constants/list-manager';

const stateToComputed = (state, attrs) => ({
  itemType: itemType(state, attrs.stateLocation)
});

const dispatchToActions = {
  viewChanged
};

const DetailsFooter = Component.extend({

  tagName: 'footer',
  layout,
  classNames: ['details-footer'],
  stateLocation: undefined,
  item: null,

  actions: {
    detailsDone() {
      this.send('viewChanged', LIST_VIEW, this.get('stateLocation'));
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(DetailsFooter);
