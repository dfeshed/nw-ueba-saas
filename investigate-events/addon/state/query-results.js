import Ember from 'ember';
import Events from './events';

const {
  computed,
  Object: EmberObject
} = Ember;

export default EmberObject.extend({
  // Event records fetched from the current `query`.
  events: computed(() => Events.create()),

  // Requests for values of each of the query definition's meta keys.
  metaKeyStates: computed(() => [])
});
