import Ember from 'ember';
import Events from './events';
import EventCount from './event-count';
import EventTimeline from './event-timeline';

const {
  computed,
  Object: EmberObject
} = Ember;

export default EmberObject.extend({
  // Event records fetched from the current `query`.
  events: computed(() => Events.create()),

  // Total count of events that match the current `query`. (Not all may have been fetched yet.)
  eventCount: computed(() => EventCount.create()),

  // Event counts (or sizes) aggregated by time.
  eventTimeline: computed(() => EventTimeline.create())
});
