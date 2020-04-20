import Service, { inject as service } from '@ember/service';
import { serviceId, timeRange, startTime, endTime } from 'investigate-shared/selectors/investigate/selectors';
import { navigateToInvestigateEventsAnalysis, navigateToInvestigateNavigate } from 'investigate-shared/utils/pivot-util';

export default Service.extend({

  timezone: service(),

  redux: service(),

  pivotToInvestigate(metaName, item, category, tab = 'EVENTS') {
    const state = this.get('redux').getState();
    const investigateServer = serviceId(state);
    if (investigateServer !== '-1') {
      const dateRange = timeRange(state);
      const sTime = startTime(state);
      const eTime = endTime(state);
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
          additionalFilter: additionalFilter.join(' && '),
          startTime: sTime,
          endTime: eTime
        }, investigateServer, dateRange, zoneId);
      } else {
        navigateToInvestigateNavigate({
          metaName,
          itemList: [item],
          additionalFilter: additionalFilter.join(' && '),
          startTime: sTime,
          endTime: eTime
        }, investigateServer, dateRange, zoneId);
      }
    }
  }

});
