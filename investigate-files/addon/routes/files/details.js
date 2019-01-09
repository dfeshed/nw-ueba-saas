import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { initializerForFileDetailsAndAnalysis } from 'investigate-files/actions/data-creators';
import { setNewFileTab } from 'investigate-files/actions/visual-creators';
import { next } from '@ember/runloop';

export default Route.extend({
  redux: service(),

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
    }
  },

  model(params) {
    const redux = this.get('redux');
    const { checksum, sid, tabName, fileFormat = '' } = params;

    next(() => {
      if (sid) {
        redux.dispatch(initializerForFileDetailsAndAnalysis(checksum, sid, tabName, fileFormat));
      }
    });
  },

  actions: {
    switchToSelectedFileDetailsTab(tabName, fileFormat) {
      this.get('redux').dispatch(setNewFileTab(tabName));
      this.transitionTo({
        queryParams: {
          tabName,
          fileFormat
        }
      });
    }
  }
});
