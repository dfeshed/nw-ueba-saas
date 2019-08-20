import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { initializerForFileDetailsAndAnalysis } from 'investigate-files/actions/data-creators';

export default Route.extend({
  redux: service(),

  contextualHelp: service(),

  queryParams: {
    /**
     * selected sid for multi-server endpoint server
     * @type {string}
     * @public
     */
    sid: {
      refreshModel: true
    },
    /**
     * checksumSha256 for selected file
     * @type {string}
     * @public
     */
    checksum: {
      refreshModel: true
    },
    /**
     * tabName for selected tab
     * @type {string}
     * @public
     */
    tabName: {
      refreshModel: true
    },
    /**
     * fileFormat for selected file
     * @type {string}
     * @public
     */
    fileFormat: {
      refreshModel: false
    },
    sourceSid: {
      refreshModel: true
    }
  },

  model(params) {
    const redux = this.get('redux');
    const { checksum, sid, tabName, fileFormat = '', sourceSid = '' } = params;

    if (sid) {
      redux.dispatch(initializerForFileDetailsAndAnalysis(checksum, sid, tabName, fileFormat, sourceSid));
    }
  },

  deactivate() {
    this.set('contextualHelp.topic', this.get('contextualHelp.invFiles'));
  },

  actions: {
    switchToSelectedFileDetailsTab(tabName, fileFormat) {
      if (tabName === 'ANALYSIS') {
        this.set('contextualHelp.topic', this.get('contextualHelp.invEndpointFileAnalysis'));
      }
      this.transitionTo({
        queryParams: {
          tabName,
          fileFormat
        }
      });
    }

  }
});
