/**
  ## Overview

  Manages replay of attributes/properties configured by the component during component initialization.
  Provides 'properties' attributes, which takes a configuration of managed properties for replaying
  @public
*/

import Ember from 'ember';

const {
  Object: EmberObject,
  computed,
  isEmpty,
  isArray
} = Ember;

export default EmberObject.extend({

  /**
  An array of Replayable Ember Objects for e.g. columns, filters, flags.
  Replayables are 'set' by the component using configuration function.
  Each element should provide the 'path' of restoredState from which value has to be read and a computed property ('value') on which restored value has to be set.
  @see config replayConfig
  @public
  */
  replayables: [],

  /**
  The restored state from which values will be read and assigned to replayable computed ('value') property.
  @public
  */
  restoredState: null,

  /**
  Checks whether or not replayables attribute is a non empty array and restored state value is non empty obect.
  @name isReplayable
  @returns boolean
  @public
  */
  isReady: computed('replayables', 'restoredState', function() {
    return (isArray(this.get('replayables')) && !isEmpty(this.get('replayables')) && this.get('restoredState') && Object.keys(this.get('restoredState')).length > 0);
  }),

  /**
  Iterates over managed properties array. Reads the values from restored state using the configured 'path' and sets the configured computed properties.
  @name replay
  @public
  */
  replay() {
    this.get('replayables').forEach((replayable) => {
      replayable.set('value', this.get(`restoredState.${replayable.get('path')}`));
    });
  }

});
