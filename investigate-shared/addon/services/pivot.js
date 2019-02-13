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
      const additionalFilter = [];
      if (category) {
        additionalFilter.push(`category="${category}"`);
      }

      if (item.machineName) {
        additionalFilter.push(`alias.host="${item.machineName}"`);
      }

      if (tab === 'EVENTS') {
        navigateToInvestigateEventsAnalysis({
          metaName,
          itemList: [item],
          additionalFilter: additionalFilter.join(' && ')
        }, investigateServer, dateRange, zoneId);
      } else {
        navigateToInvestigateNavigate({
          metaName,
          itemList: [item],
          additionalFilter: additionalFilter.join(' && ')
        }, investigateServer, dateRange, zoneId);
      }
    }
  }

});
