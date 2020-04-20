import Route from '@ember/routing/route';
import { inject } from '@ember/service';
import groupsCreators from 'admin-source-management/actions/creators/groups-creators';

export default Route.extend({
  redux: inject(),
  contextualHelp: inject(),

  model() {
    const redux = this.get('redux');
    redux.dispatch(groupsCreators.initializeGroups());
  },

  activate() {
    this.set('contextualHelp.module', this.get('contextualHelp.usmModule'));
    this.set('contextualHelp.topic', this.get('contextualHelp.usmGroups'));
  },

  deactivate() {
    this.set('contextualHelp.module', null);
    this.set('contextualHelp.topic', null);
  }
});
