import BodyCell from 'component-lib/components/rsa-data-table/body-cell/component';
import { inject as service } from '@ember/service';

export default BodyCell.extend({

  dateFormat: service(),

  timeFormat: service(),

  timezone: service()
});
