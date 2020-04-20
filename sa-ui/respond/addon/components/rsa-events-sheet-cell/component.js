import DataTableBodyCell from 'component-lib/components/rsa-data-table/body-cell/component';
import HighlightsEntities from 'context/mixins/highlights-entities';

/**
 * @class Events Sheet Table Cell Component
 * The same Component as `rsa-data-table/body-cell` (the generic data cell for rsa-data-table), but equipped with the
 * HighlightsEntities Mixin from the context addon, which enables the component to decorate nodes which correspond to
 * entities (IPs, Domains, Users, Hosts, etc) and to wire those nodes up to the context tooltip component.
 * @public
 */
export default DataTableBodyCell.extend(HighlightsEntities, {

  // Configuration for wiring up entities to context lookups.
  // @see context/mixins/highlights-entities
  entityEndpointId: 'IM',
  autoHighlightEntities: true
});
