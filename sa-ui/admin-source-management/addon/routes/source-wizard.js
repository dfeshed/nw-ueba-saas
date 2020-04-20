import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { run } from '@ember/runloop';
import { initializeSource } from 'admin-source-management/actions/creators/source-wizard-creators';

export default Route.extend({
  redux: service(),
  contextualHelp: service(),

  model({ sourceId }) {
    run.next(() => {
      this.get('redux').dispatch(initializeSource(sourceId));
    });
    return {
      sourceId
    };
  },

  actions: {
    transitionToSources() {
      this.transitionTo('sources');
    }
  },

  activate() {
    this.set('contextualHelp.module', this.get('contextualHelp.usmModule'));
    this.set('contextualHelp.topic', this.get('contextualHelp.usmSourcesWizard'));
  },

  deactivate() {
    this.set('contextualHelp.module', null);
    this.set('contextualHelp.topic', null);
  }
});