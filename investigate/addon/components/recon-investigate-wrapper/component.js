import { computed } from '@ember/object';
import Component from '@ember/component';
import moment from 'moment';

export default Component.extend({
  testId: 'reconInvestigateWrapper',
  attributeBindings: ['testId:test-id'],
  classNames: ['recon-standalone-container'],

  queryInputs: computed('endpointId', function() {
    const now = moment();
    // Default to a time range of last 7 days since
    const queryInputs = {
      endTime: now.unix(),
      serviceId: this.endpointId,
      startTime: now.subtract(7, 'days').unix()
    };
    return queryInputs;
  })
});
