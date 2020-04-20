import Route from '@ember/routing/route';
import { getFileAnalysisData } from 'investigate-shared/actions/data-creators/file-analysis-creators';
import { inject as service } from '@ember/service';

export default Route.extend({

  redux: service(),

  model(params) {
    const redux = this.get('redux');
    const parentParam = this.modelFor('hosts.details.tab');
    const { fileHash, fileFormat, fileSid } = params;
    if (fileHash && fileFormat && fileSid) {
      redux.dispatch(getFileAnalysisData(fileHash, fileFormat, fileSid));
    }
    return { ...params, ...parentParam };
  }
});
