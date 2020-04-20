import Component from '@ember/component';
import creators from 'respond/actions/creators';
import columns from './columns';

export default Component.extend({
  classNames: ['rsa-remediation-tasks'],
  columns,
  creators: creators.remediationTasks
});
