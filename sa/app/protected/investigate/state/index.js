import Ember from 'ember';
import Services from './services';
import Events from './events';
import EventCount from './event-count';
import EventColumns from './event-columns';
import Tree from 'sa/utils/tree/tree';
import Meta from './meta';

const { Object: EmberObject } = Ember;

export default EmberObject.extend({

  // List of available Core services. User can choose one to query.
  services: Services.create(),

  // Tree of queries executed (if any) during this user's session. Used for tracking an investigation's path.
  queryTree: Tree.create(),

  // Pointer to the currently visible query's node in the `queryTree`.
  query: undefined,

  // Event records fetched from the current `query`.
  events: Events.create(),

  // Total count of events that match the current `query`. (Not all may have been fetched yet.)
  eventCount: EventCount.create(),

  // Columns to display for events data table.
  eventColumns: [].concat(EventColumns),

  // Meta data for the `events`.
  meta: Meta.create()
}).create();
