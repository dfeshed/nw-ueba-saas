import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import moment from 'moment';

export default Component.extend({
  testId: 'reconInvestigateWrapper',
  attributeBindings: ['testId:test-id'],
  classNames: ['recon-standalone-container'],

  @computed('endpointId')
  queryInputs(endpointId) {
    const now = moment();
    // Default to a time range of last 7 days since
    const queryInputs = {
      endTime: now.unix(),
      serviceId: endpointId,
      startTime: now.subtract(7, 'days').unix()
    };
    return queryInputs;
  }
});
