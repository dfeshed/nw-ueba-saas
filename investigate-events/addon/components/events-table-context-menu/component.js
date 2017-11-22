import RsaContextMenu from 'component-lib/components/rsa-context-menu/component';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';

const stateToComputed = (state) => {
  const { investigate: { queryNode: { serviceId, startTime, endTime, metaFilter }, dictionaries } } = state;
  return {
    endpointId: serviceId,
    startTime,
    endTime,
    queryConditions: metaFilter.conditions,
    language: dictionaries.language
  };
};

/*
 * Since the events table is a special custom table which has html tags with meta and value injected from the javascript
 * we cannot use the rsa-context-menu component as-is. This extended class captures the right click event, extracts the
 * meta and value from the html span, prepares the contextSelection property before invoking the parent rsa-context-menu action.
 */
const EventsTableContextMenu = RsaContextMenu.extend({

  metaName: null,
  metaValue: null,

  @computed('metaName', 'metaValue', 'endpointId')
  contextSelection: (metaName, metaValue) => ({ metaName, metaValue }),

  @computed('endpointId', 'startTime', 'endTime', 'queryConditions', 'language')
  contextDetails: (endpointId, startTime, endTime, queryConditions, language) => ({
    endpointId,
    startTime,
    endTime,
    queryConditions,
    language
  }),

  contextMenu({ target: { attributes } }) {
    const metaName = attributes.getNamedItem('metaname');
    const metaValue = attributes.getNamedItem('metavalue');
    if (metaName && metaValue) {
      this.set('metaName', metaName.value);
      this.set('metaValue', metaValue.value);
      this._super(...arguments);
    } // else do not call super so that the browser right-click event is preserved
  }
});

export default connect(stateToComputed)(EventsTableContextMenu);