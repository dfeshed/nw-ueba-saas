import Service, { inject as service } from '@ember/service';
import { serviceId, timeRange } from 'investigate-shared/selectors/investigate/selectors';
import { navigateToInvestigateEventsAnalysis, navigateToInvestigateNavigate } from 'investigate-shared/utils/pivot-util';
import { lookup } from 'ember-dependency-lookup';

export default Service.extend({

  timezone: service(),

  pivotToInvestigate(metaName, item, category, tab = 'NAVIGATE') {
    const redux = lookup('service:redux');
    const state = redux.getState();
    const investigateServer = serviceId(state);
    if (investigateServer !== '-1') {
      const dateRange = timeRange(state);
      const { zoneId } = this.get('timezone.selected');
      let additionalFilter;
      if (category) {
        additionalFilter = `category="${category}"`;
      }
      if (tab === 'EVENTS') {
        navigateToInvestigateEventsAnalysis({
          metaName,
          itemList: [item],
          additionalFilter
        }, investigateServer, dateRange, zoneId);
      } else {
        navigateToInvestigateNavigate({
          metaName,
          itemList: [item],
          additionalFilter
        }, investigateServer, dateRange, zoneId);
      }
    }
  }

});
