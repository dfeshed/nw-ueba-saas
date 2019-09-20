import Component from '@ember/component';
import layout from './template';
import { connect } from 'ember-redux';
import { initializeListManager } from 'rsa-list-manager/actions/creators/creators';
import { isListManagerReady } from 'rsa-list-manager/selectors/list-manager/selectors';

const stateToComputed = (state, attrs) => ({
  isListManagerReady: isListManagerReady(state, attrs.stateLocation)
});

const dispatchToActions = {
  initializeListManager
};

const ListManager = Component.extend({
  layout,
  classNames: ['list-manager'],
  stateLocation: undefined,

  /*
   * Name identifying the list used to label buttons in the manager.
   * Name should sound plural, ending in 's'
   */
  listName: null,

  // Object to identify an item as selected in the manager's button caption
  selectedItemId: null,

  // the original list
  list: null,

  // object for contextual help
  // e.g. { moduleId: "investigation", topicId: "eaColumnGroups" }
  helpId: null,

  didInsertElement() {
    const initialProperties =
      this.getProperties('stateLocation', 'listName', 'list', 'selectedItemId', 'helpId');
    this.send('initializeListManager', { ...initialProperties, filterText: '' });
  }
});

export default connect(stateToComputed, dispatchToActions)(ListManager);
