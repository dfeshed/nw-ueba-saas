import Component from '@ember/component';
import layout from './template';
import { connect } from 'ember-redux';
import { initializeListManager } from 'rsa-list-manager/actions/creators/creators';
import { isListManagerReady } from 'rsa-list-manager/selectors/list-manager/selectors';

const stateToComputed = (state, attrs) => ({
  isListManagerReady: isListManagerReady(state, attrs.listLocation)
});

const dispatchToActions = {
  initializeListManager
};

const ListManager = Component.extend({
  layout,
  listLocation: undefined,

  /*
   * Name identifying the list used to label buttons in the manager.
   * Name should sound plural, ending in 's'
   */
  listName: null,

  classNames: ['list-manager'],

  // Object to identify an item as selected in the manager's button caption
  selectedItem: null,

  // the original list
  list: null,

  didInsertElement() {
    const initialProperties = this.getProperties('listLocation', 'listName', 'list', 'selectedItem');
    this.send('initializeListManager', initialProperties);
  }
});

export default connect(stateToComputed, dispatchToActions)(ListManager);
