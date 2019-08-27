import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import { success, failure } from 'investigate-shared/utils/flash-messages';
import { isolateHostRequest } from 'investigate-hosts/actions/data-creators/host';


const dispatchToActions = {
  isolateHostRequest
};

const callBackOptions = {
  onSuccess: () => success('investigateHosts.networkIsolation.isolate.success'),

  onFailure: (message) => failure(message, null, false)
};

const Isolate = Component.extend({
  isExclusionListEnabled: false,

  exclusionList: '',

  isolationComment: '',

  @computed('isolationComment')
  isCharacterLimitReached(isolationComment) {
    return isolationComment.length === 900;
  },

  actions: {
    confirmIsolation() {
      let data = {};
      const { agentId, serverId, isolationComment } = this.getProperties('agentId', 'serverId', 'isolationComment');
      data = {
        agentId,
        exclusionList: [],
        comment: isolationComment
      };
      this.send('isolateHostRequest', data, serverId, callBackOptions);
      this.closeConfirmModal();
    },

    toggleExclusionListEnabled() {
      this.toggleProperty('isExclusionListEnabled');
    },

    validate() {
      // placeholder for the next exclusionList PR
    }
  }

});

export default connect(undefined, dispatchToActions)(Isolate);