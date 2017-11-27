import BodyCell from 'component-lib/components/rsa-data-table/body-cell/component';
import service from 'ember-service/inject';

export default BodyCell.extend({
  dateFormat: service(),
  timeFormat: service(),
  timezone: service()
});
