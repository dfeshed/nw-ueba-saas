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
import State from './persistent-state';

const {
  Object: EmberObject,
  typeOf
} = Ember;

export default EmberObject.extend({

  /*
  An array of POJO objects representing persistables for e.g. flags, columns, filters.
  Persistables are 'set' by the component using configuration function.
  Each POJO should provide addObserver, removeObserver and appropriate execute method implementation to persist
  @see config persistenceConfig
  @public
  */
  persistables: [],

  /*
  Persistent State Object.
  @see persistent-state
  */
  state: null,

  // key
  storageKey: null,

  /*
  creates a fresh persitent state object on init.
  */
  init() {
    this._super(...arguments);
    this.set('state', State.create({ storageKey: this.get('storageKey') }));
  },

  /**
  Persists the intial state, if persistables implement persistInitialState
  @public
  */
  persistInitialState() {
    this.get('persistables').forEach((persistable) => {
      if (persistable.persistInitialState && typeOf(persistable.persistInitialState) === 'function') {
        persistable.persistInitialState();
      }
    });
  },

  /**
  Adds observers if the persistables implement addObserver method.
  This class does not add observers by itself. It is invoked by the component at appropriate time for e.g during init or didReceiveAttrs during first render or re-render
  @name addObservers
  @public
  */
  createObservers() {
    this.get('persistables').forEach((persistable) => {
      if (persistable.createObserver && typeOf(persistable.createObserver) === 'function') {
        persistable.createObserver();
      }
    });
  },

  /**
  Removes the added observers, if the persistables implement addObserver method.
  This class does not add observers by itself. It is invoked by the component at appropriate time for e.g willDestroyElement of the component.
  @name addObservers
  @public
  */
  destroyObservers() {
    this.get('persistables').forEach((persistable) => {
      if (persistable.destroyObserver && typeOf(persistable.destroyObserver) === 'function') {
        persistable.destroyObserver();
      }
    });
  }

});
