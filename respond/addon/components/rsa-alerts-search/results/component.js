import Component from 'ember-component';
import layout from './template';

export default Component.extend({
  tagName: 'vbox',
  layout,
  classNames: ['rsa-alerts-search-results'],

  // The entity { type, id } that these search results queried for
  entity: null,

  // Identifier name of the timeFrame that these search results queried for
  timeFrameName: null,

  // Array of 1 or more device identifiers that these search results quered for;
  // each identifier is one of: 'source.device', 'destination.device' or 'detector'
  devices: null,

  // Streaming set of search result records (alerts POJOs)
  items: null,

  // Either 'streaming', 'complete', 'error' or 'stopped'
  itemsStatus: null,

  // Columns configuration for the rsa-data-table that displays the search results
  columnsConfig: [{
    field: 'summary',
    width: '100%'
  }]
});

