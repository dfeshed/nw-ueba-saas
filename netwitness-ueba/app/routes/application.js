import Route from '@ember/routing/route';
import { getOwner } from '@ember/application';
import { inject as service } from '@ember/service';

export default Route.extend({

  dateFormat: service(),
  timeFormat: service(),
  timezone: service(),

  model() {

    // When running in sa, these are set as part of protected route,
    // just setting defaults here so preferences exist
    this.setProperties({
      'timezone.options': [{
        'displayLabel': 'UTC (GMT+00:00)',
        'offset': 'GMT+00:00',
        'zoneId': 'UTC'
      }],
      'i18n.locale': 'en-us',
      'dateFormat.selected': 'MM/dd/yyyy',
      'timeFormat.selected': 'HR24',
      'timezone.selected': 'UTC'
    });
    this.set('accessControl.roles', [
      'accessAdminModule',
      'viewAppliances',
      'viewServices',
      'viewEventSources',
      'viewUnifiedSources',
      'accessHealthWellness',
      'manageSystemSettings',
      'manageSASecurity',
      'searchLiveResources',
      'accessInvestigationModule',
      'respond-server.*',
      'investigate-server.*',
      'integration-server.*',
      'endpoint-server.agent.read'
    ]);

    // When running microservices, need to login and get cookie
    // so requests do not fail.
    //
    // However we do not want to force a login if we are running
    // local mocks (local node server)
    
  }
});
