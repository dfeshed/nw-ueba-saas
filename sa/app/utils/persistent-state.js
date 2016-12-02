/**
  ## Overview

  Provides the API for persistence and removal of nodes.
  Requires storageKey to be configured, which in turn configures the Strorage instance to used by this class.
  Unless storage is configured persist and removal cannot be performed and Ember Error is thrown
  Clients can prefer to use 'volatile' (default false), if state has to be always read from storage and not the Ember cache
  By default, state is restored from the storage only once.
  Ignores 'volatile' attribute when new value is set by persistence or removal and always stores the value to the storage.

  @public
*/
import Ember from 'ember';
import Storage from './localstorage-json';

const {
  Object: EmberObject,
  computed,
  typeOf,
  isEmpty,
  isPresent,
  Error
} = Ember;

const SEPARATOR = '.';

/**
helper method used by persist to split the path into nodes using '.' as property separator
@name _split
@param {string} path - e.g. 'filters.priority' || 'fields.assigneeFirstLastName.visible'
@returns {array}
@private
*/
function _split(path) {
  if (typeOf(path) == 'string' && !isEmpty(path)) {
    return (path.indexOf(SEPARATOR) !== -1) ? path.split(SEPARATOR) : [path];
  } else {
    return [];
  }
}

/**
Transforms a path into an object branch onto an accumalator object (initialValue). Assigns value to last node.
If intermediate node in the path does not exists, creates them.
Helper method used by persist.
@name _transform
@param {Object} initialValue - intialValue to be used as an accumalator.
@param {String} path - path to be formed on the accumalator e.g. 'filters.priority' || 'fields.assigneeFirstLastName.visible'
@param {*} value - value to be assigned to the last node in the path.
@private
*/
function _transform(initialValue, path, value) {
  const nodes = _split(path);
  if (!isEmpty(nodes)) {
    nodes.reduce(function(parent, child, index, nodes) {
      if (index < nodes.length - 1) {
        return parent[child] = parent[child] || {};
      } else {
        parent[child] = value;
      }
    }, initialValue);
  }
}

/**
removes the path from state object
@name _remove
@param {Ember Object} state - Object from which the path has to be removed.
@param {String} path - path to be removed.
@param {boolean} recursive - If true, when removal of leaf node results in an empty parent then removal is performed recursively.
@private
*/
function _remove(state, path, recursive) {
  const splitIndex = path.lastIndexOf(SEPARATOR);
  const lastNodeParentPath = path.slice(0, splitIndex);
  const childKey = path.slice(splitIndex + 1);
  if (path === childKey) {
    delete state[childKey];
    return;
  } else {
    delete state.get(lastNodeParentPath)[childKey];
    if (typeOf(recursive) === 'boolean' && recursive && Object.keys(state.get(lastNodeParentPath)).length === 0) {
      _remove(state, path.slice(0, path.lastIndexOf(SEPARATOR)), recursive);
    }
  }
}

export default EmberObject.extend({
  /**
  storageKey to be used for storage and restoration of the state.
  @public
  */
  storageKey: null,

  /**
  flag for volatility, defaults to false so that state "value" can be cached and served from in-memory,
  instead of reaching the stroage for restoration of state
  If true, state 'value' is set to be volatile so that the value doesn't get cached.
  If true, state 'value' is always read from storage.
  @public
  */
  volatile: false,

  /**
  Storage to be used for storing key value(json string) pair.
  Computed property that depends on storageKey attribute, if storageKey changes this attribute is re-computed when accessed next time.
  Creates a Storage object once as it's own value.
  When storage value is set, persisted state is restored and assigned to 'value' attribute.
  @public
  */
  storage: computed('storageKey', {
    get() {
      return this.get('storageKey') ? Storage.create({ key: this.get('storageKey') }) : null;
    },
    set(key, storage) {
      if (storage) {
        this.set('value', storage.restore());
      }
      return storage;
    }
  }),

  /**
  Represents persisted value as native javascript object
  Computed property that depends on storage attribute.
  If 'storage' attribute has changed, then 'value' attribute is re-computed when accessed next time. Tries to restores persisted state from storage and assigns it as it's own value.
  If this attribute is declared as volatile, it always reads the persisted from storage, else state value is read from cache.
  When new value is set, it always stores the value in storage.
  @public
  */
  value: computed('storage', {
    get() {
      return this.get('storage') ? this.get('storage').restore() : null;
    },
    set(key, value) {
      if (this.get('storage')) {
        this.get('storage').store(value);
      }
      return value;
    }
  }),

  /**
  initializes by setting the storage and defining whether or not the value attribute should be volatile
  @public
  */
  init() {
    this._super(...arguments);

    if (this.get('volatile')) {
      this.value.volatile();
    }
  },

  /**
  Persists the path and value to the storage.
  if the path provided is dot separated then interprets the path as an object tree in top-down heirachy, with each segment representing a node in the tree.
  @name persist
  @param {string} path - full path e.g. 'filters.priority' || 'fields.assigneeFirstLastName.visible'
  @param {*} value - value of the property
  @throws {Ember.Error}, if storage attribute is not instantiated.
  @public
  */
  persist(path, value) {
    if (!this.get('storage')) {
      throw new Error('Attempted to persist without configuring storage. Cannot persist.');
    }

    const state = EmberObject.create(this.get('value'));
    _transform(state, path, value);
    this.set('value', state);

  },

  /**
  Removes the path from object tree.
  if the path provided is dot separated then interprets the path as an object tree in top-down heirachy, with each segment representing a node in the tree.
  Mere presence of node is tested, if leaf node is found it is removed.
  Operates on a copy of the state, only if removal is successful, then the new state is persisted.
  @name persist
  @param {string} path - full path e.g. 'filters.priority' || 'fields.assigneeFirstLastName.visible'
  @param {boolean} recursive - If true, when removal of leaf node results in an empty parent then removal is performed recursively, however the very top level object is never deleted by this method.
  @returns {boolean} - whether or not the path was removed successfully
  @throws {Ember.Error}, if storage attribute is not instantiated.
  @public
  */
  remove(path, recursive) {
    if (!this.get('storage')) {
      throw new Error('Attempted to remove node without configuring storage. Cannot remove.');
    }

    const state = EmberObject.create(this.get('value'));
    if (Object.keys(state).length === 0 && !isPresent(state.get(path))) {
      return false;
    }

    _remove(state, path, recursive);
    this.set('value', state);
    return true;

  },

  /**
  checks whether or not top level restored state value is replayable
  @name isReplayable
  @param {object} restoredState
  @returns boolean
  @public
  */
  isReplayable: computed('value', function() {
    return (this.get('value') && Object.keys(this.get('value')).length > 0);
  })

});