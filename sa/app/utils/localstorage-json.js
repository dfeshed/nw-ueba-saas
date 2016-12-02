/**
  ## Overview

  This class provides some convenience API by abstracting localStorage
  Abstraction also helps to stub out the class by client classes for unit testing, @see persistent-state-test
  @public
*/

import Ember from 'ember';

const {
  Logger,
  Object: EmberObject,
  Error
} = Ember;

export default EmberObject.extend({
  // key which holds the value
  key: null,

  // defaults to localStorage
  storage: localStorage,

  // when configured to true catches JSON parse exception and throws Ember.Error if the stored json string cannot be restored back to native object.
  // defaults to false.
  throwExceptionOnRestore: false,

  /**
  * @name init
  * @description if key does not yet exist in the storage, creates the key and it with a string representation of empty object
  * @public
  */
  init() {
    this._super(...arguments);
    if (this.get('key') && !this.exists()) {
      this.get('storage')[this.get('key')] = '{}';
    }
  },

  /**
  * @name reset
  * @description resets the key to an string representation of empty object.
  * @public
  */
  reset() {
    if (this.get('key')) {
      this.get('storage')[this.get('key')] = '{}';
    }
  },

  /**
  * @name store
  * @description stringifies the passed in native object and persists it in the storage
  * @params {string} obj - native javascript object
  * @public
  */
  store(obj) {
    const jsonString = JSON.stringify(obj);
    if (jsonString) {
      this.get('storage')[this.get('key')] = jsonString;
    }
  },

  /**
  * @name restore
  * @description reads and parses the stored json string
  * @returns native javascript object
  * @throws {Ember.Error}, if stored json string cannot be parsed.
  * @public
  */
  restore() {
    try {
      return JSON.parse(this.get('storage')[this.get('key')]);
    } catch (e) {
      Logger.error(e.message);
      if (this.get('throwExceptionOnRestore')) {
        throw new Error('State could not be restored.');
      } else {
        Logger.warn('State could not be restored. Returning empty object');
        return {};
      }
    }
  },

  /**
  * @name exists
  * @description checks whether or not a value is stored at the key in the storage
  * @returns boolean
  * @public
  */
  exists() {
    return (this.get('storage')[this.get('key')]) ? true : false;
  }

});