/**
  ## Overview

  Manages persistence of attributes/properties configured by the component.
  Composes Persistent-State Object and utilizes it for persistence.
  Provides 'properties' attributes, which takes a configuration of managed properties for persistence
  Uses observers to observe change in managed properties.
  Addition and removal of Observers is requested by the client of this class. This class does not do so on it's own.
  @public
*/

import Ember from 'ember';
import { storageKey } from './config';
import PersistenceManager from 'sa/utils/persistence-manager';

const {
  Object: EmberObject,
  typeOf
} = Ember;

export default EmberObject.extend({

  /*
  Persistent State Manager.
  @see persistent-manager
  */
  persistence: null,

  /*
  creates a fresh persitence manager object on init.
  */
  init() {
    this._super(...arguments);
    this.set('persistence', PersistenceManager.create({ storageKey }));
  },

  /**
  Looks up the current sort flag from the persisted state.
  Used by the route to determine sort field before making the model available for the component.
  @return {Object} containing field and sort order.
  @public
  */
  getSortedColumn() {
    const sortColumn = this.get('persistence.state.value.flags.currentSort');
    if (sortColumn) {
      const { isDescending } = this.get('persistence.state.value.columns')[sortColumn];
      if (typeOf(isDescending) == 'boolean') {
        return { field: sortColumn, descending: isDescending };
      }
    }
  }

});
