import BodyCell from 'component-lib/components/rsa-data-table/body-cell/component';
import layout from './template';
import { inject as service } from '@ember/service';

export default BodyCell.extend({
  layout,

  dateFormat: service(),

  timeFormat: service(),

  timezone: service()
});