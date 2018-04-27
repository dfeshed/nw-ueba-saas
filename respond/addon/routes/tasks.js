import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { initializeTasks } from 'respond/actions/creators/remediation-task-creators';

export default Route.extend({
  accessControl: service(),
  contextualHelp: service(),
  i18n: service(),
  redux: service(),

  titleToken() {
    return this.get('i18n').t('respond.entities.remediationTasks');
  },

  beforeModel() {
    if (!this.get('accessControl.hasRespondRemediationAccess')) {
      this.transitionTo('index');
    }
  },

  model() {
    const redux = this.get('redux');
    redux.dispatch(initializeTasks());
  },


  activate() {
    this.set('contextualHelp.topic', this.get('contextualHelp.respRemTasksVw'));
  },

  deactivate() {
    this.set('contextualHelp.topic', null);
  }
});
