import Ember from 'ember';
import Services from './services';
import EventColumnGroups from './event-column-groups';
import Tree from 'sa/utils/tree/tree';
import Meta from './meta';
import Recon from './recon';

const {
  Object: EmberObject
} = Ember;

export default EmberObject.extend({

  // List of available Core services. User can choose one to query.
  services: Services.create(),

  // Cache of Core languages, keyed by Core service Id. Each hash value is an array of meta keys.
  languages: EmberObject.create(),

  // Cache of Core aliases, keyed by Core service Id. Each hash value is lookup table of meta values aliases, keyed by meta key name.
  aliases: EmberObject.create(),

  // Tree of queries executed (if any) during this user's session. Used for tracking an investigation's path.
  queryTree: Tree.create(),

  // Pointer to the currently visible query's node in the `queryTree`.
  queryNode: undefined,

  // Pointer to the previously shown query node in the `queryTree`. Used for transitioning from node to node.
  lastQueryNode: undefined,

  // Route-level status, indicating whether route is ready to fetch query data. Either "wait", "resolved" or "rejected".
  routeStatus: undefined,

  // If `routeStatus` is `rejected`, the reason for the error.
  routeReason: undefined,

  // Represents groups of columns to display for events data table.
  eventColumnGroups: EventColumnGroups.create(),

  // Meta data for the `events`.
  meta: Meta.create(),

  // State of the recon section of the investigate UI.
  recon: Recon.create()
}).create();
