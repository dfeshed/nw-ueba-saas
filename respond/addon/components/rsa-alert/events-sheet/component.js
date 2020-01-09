import EventsSheet from 'respond/components/rsa-events-sheet/component';
import layout from './template';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';
import computed from 'ember-computed-decorators';
import { createEventAnalysisLink } from 'respond-shared/utils/event-analysis';
import _ from 'lodash';

const stateToComputed = ({ respond: { alert: { info, events }, recon: { serviceData } } }) => ({
  items: events,
  totalCount: info ? parseInt(info.alert.numEvents, 10) : null,
  services: serviceData
});

const AlertDatasheet = EventsSheet.extend({
  tagName: '',
  layout,
  items: null,
  totalCount: null,
  services: null,

  investigatePage: service(),

  @computed('items', 'services', 'investigatePage.legacyEventsEnabled')
  customizedItems(items, services, legacyEventsEnabled) {

    if (!legacyEventsEnabled && items) {
      return items.map((item) => {
        // eslint-disable-next-line camelcase
        if (item?.related_links) {
          const modifiedItem = _.cloneDeep(item);

          // Check if investigate original event specific related link exists in given event
          const isInvestigateEvent = modifiedItem.related_links.some((e) => e.type === 'investigate_original_event');
          if (isInvestigateEvent) {
            // Create event analysis url and replace it with legacy events url
            modifiedItem.related_links[0].url = createEventAnalysisLink(item, services);
          }

          return modifiedItem;
        } else {
          return item;
        }
      });
    }

    return items;
  }

});

export default connect(stateToComputed)(AlertDatasheet);
