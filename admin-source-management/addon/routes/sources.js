import Route from '@ember/routing/route';
import { inject } from '@ember/service';
import sourcesCreators from 'admin-source-management/actions/creators/sources-creators';

export default Route.extend({
  redux: inject(),
  contextualHelp: inject(),

  model() {
    const redux = this.get('redux');
    redux.dispatch(sourcesCreators.initializeSources());
  },

  activate() {
    this.set('contextualHelp.module', this.get('contextualHelp.usmModule'));
    this.set('contextualHelp.topic', this.get('contextualHelp.usmSources'));
  },

  deactivate() {
    this.set('contextualHelp.module', null);
    this.set('contextualHelp.topic', null);
  }
});
