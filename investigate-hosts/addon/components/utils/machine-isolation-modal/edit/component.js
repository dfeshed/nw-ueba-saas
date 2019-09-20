import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import { success, failure } from 'investigate-shared/utils/flash-messages';
import { editExclusionListRequest } from 'investigate-hosts/actions/data-creators/host';
import { isolateMachineValidation } from 'investigate-hosts/util/util';

const callBackOptions = {
  onSuccess: () => success('investigateHosts.networkIsolation.isolate.success'),

  onFailure: (message) => failure(message, null, false)
};

const stateToComputed = (state) => ({
  comment: state.endpoint.overview.hostOverview.agentStatus.isolationStatus.comment,
  excludedIps: state.endpoint.overview.hostOverview.agentStatus.isolationStatus.excludedIps
});

const dispatchToActions = {
  editExclusionListRequest
};

const Edit = Component.extend({

  isExclusionListError: false,

  isIsolationCommentEmpty: false,

  init() {
    this._super(...arguments);
    const { comment, excludedIps } = this.getProperties(['comment', 'excludedIps']);
    this.set('isolationComment', comment);
    this.set('exclusionList', excludedIps);
  },

  @computed('isolationComment')
  isolationCommentInfo(isolationComment) {
    return {
      isCharacterLimitReached: isolationComment.length === 900,
      isIsolateHostDisabled: !isolationComment.length
    };
  },

  actions: {
    confirmEditExclusionList() {
      const { isolationComment,
        isExclusionListError,
        agentId,
        serverId,
        exclusionList } = this.getProperties('isolationComment', 'isExclusionListError', 'agentId', 'serverId', 'exclusionList');
      let data = {};
      const isIsolationCommentEmpty = isolationComment.trim() === '';
      this.set('isIsolationCommentEmpty', isIsolationCommentEmpty);

      if (!isExclusionListError && !isIsolationCommentEmpty) {
        data = {
          agentId,
          exclusionList,
          comment: isolationComment
        };
        this.send('editExclusionListRequest', data, serverId, callBackOptions);
        this.closeConfirmModal();
      }
    },

    validate(exclusionList) {
      const { listOfIPs, isInvalidIPFormatPresent = false } = isolateMachineValidation(exclusionList);
      let isExclusionListLengthError = false;
      let errorString = 'invalidIp';

      if (!isInvalidIPFormatPresent && (listOfIPs.length > 100)) {
        isExclusionListLengthError = true;
        errorString = 'ipListLimit';
      }
      this.set('exclusionList', listOfIPs);
      this.set('isExclusionListError', isInvalidIPFormatPresent || isExclusionListLengthError);
      this.set('errorString', `investigateHosts.networkIsolation.isolate.${errorString}`);
    },

    onFocusOutValidateComment(isolationComment) {
      this.set('isIsolationCommentEmpty', !isolationComment.length);
    },

    onKeyUpValidateComment(isolationComment) {
      if (isolationComment.length > 0) {
        this.set('isIsolationCommentEmpty', false);
      }
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(Edit);