import Component from '@ember/component';
import columns from './columns';
import creators from 'respond/actions/creators';

export default Component.extend({
  classNames: 'rsa-alerts',
  columns,
  creators: creators.alerts
});
