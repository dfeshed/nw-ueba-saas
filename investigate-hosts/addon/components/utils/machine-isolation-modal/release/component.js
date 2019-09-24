import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import { success, failure } from 'investigate-shared/utils/flash-messages';
import { stopIsolationRequest } from 'investigate-hosts/actions/data-creators/host';

const callBackOptions = {
  onSuccess: () => success('investigateHosts.networkIsolation.releaseHost.success'),

  onFailure: (message) => failure(message, null, false)
};

const dispatchToActions = {
  stopIsolationRequest
};

const Release = Component.extend({
  isReleaseFromIsolationCommentEmpty: false,

  releaseFromIsolationComment: '',

  @computed('releaseFromIsolationComment')
  releaseFromIsolationCommentInfo(releaseFromIsolationComment) {
    return {
      isCharacterLimitReached: releaseFromIsolationComment.length === 900,
      isReleaseFromIsolation: !releaseFromIsolationComment.length
    };
  },

  actions: {
    confirmStopIsolationRequest() {
      const {
        releaseFromIsolationComment,
        agentId,
        serverId
      } = this.getProperties('releaseFromIsolationComment', 'agentId', 'serverId');
      let data = {};
      const isReleaseFromIsolationCommentEmpty = releaseFromIsolationComment.trim() === '';
      this.set('isReleaseFromIsolationCommentEmpty', isReleaseFromIsolationCommentEmpty);

      if (!isReleaseFromIsolationCommentEmpty) {
        data = {
          agentId,
          comment: releaseFromIsolationComment
        };
        this.send('stopIsolationRequest', data, serverId, callBackOptions);
        this.closeConfirmModal();
      }
    },

    onFocusOutValidateComment(releaseFromIsolationComment) {
      this.set('isReleaseFromIsolationCommentEmpty', !releaseFromIsolationComment.length);
    },

    onKeyUpValidateComment(releaseFromIsolationComment) {
      if (releaseFromIsolationComment.length > 0) {
        this.set('isReleaseFromIsolationCommentEmpty', false);
      }
    }
  }
});

export default connect(undefined, dispatchToActions)(Release);