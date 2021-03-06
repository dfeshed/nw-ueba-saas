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
  modelName: undefined,

  /*
   * Name identifying the list used to label buttons in the manager.
   * Name should sound plural, ending in 's'
   */
  listName: null,

  // Object to identify an item as selected in the manager's button caption
  selectedItemId: null,

  // true if an item can be selected and persist
  shouldSelectedItemPersist: null,

  // the original list
  list: null,

  // object for contextual help
  // e.g. { moduleId: "investigation", topicId: "eaColumnGroups" }
  helpId: null,

  // flag to disable the list-manager
  isDisabled: false,

  // function passed to list-manager-container
  itemSelection: () => {},

  didInsertElement() {
    const initialProperties =
      this.getProperties(
        'stateLocation',
        'listName',
        'list',
        'selectedItemId',
        'helpId',
        'modelName',
        'shouldSelectedItemPersist'
      );
    this.send('initializeListManager', initialProperties);
  },

  didReceiveAttrs() {
    // attrs received after didInsertElement - send them
    // in order to persist previous selection
    const updatedProperties =
      this.getProperties(
        'stateLocation',
        'selectedItemId',
      );
    this.send('initializeListManager', updatedProperties);
  }
});

export default connect(stateToComputed, dispatchToActions)(ListManager);
