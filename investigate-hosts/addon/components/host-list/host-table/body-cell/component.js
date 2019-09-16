import BodyCell from 'component-lib/components/rsa-data-table/body-cell/component';
import { inject as service } from '@ember/service';
import computed from 'ember-computed-decorators';

export default BodyCell.extend({

  dateFormat: service(),

  timeFormat: service(),

  timezone: service(),

  @computed('item')
  isIsolated(item) {
    return item?.agentStatus?.isolationStatus?.isolated;
  }
});
