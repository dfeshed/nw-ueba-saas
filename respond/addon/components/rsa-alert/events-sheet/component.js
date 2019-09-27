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
        if (!item || !item.related_links) {
          return;
        }

        const modifiedItem = _.cloneDeep(item);

        // Create event analysis url and replaced it with legacy events url
        const eventAnalysisLink = createEventAnalysisLink(item, services);

        modifiedItem.related_links[0].url = eventAnalysisLink;

        return modifiedItem;
      });
    }

    return items;
  }

});

export default connect(stateToComputed)(AlertDatasheet);
