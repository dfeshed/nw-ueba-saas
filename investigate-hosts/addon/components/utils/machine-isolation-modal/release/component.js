import classic from 'ember-classic-decorator';
import { action, computed } from '@ember/object';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { success, failure } from 'investigate-shared/utils/flash-messages';
import { stopIsolationRequest } from 'investigate-hosts/actions/data-creators/host';

const callBackOptions = {
  onSuccess: () => success('investigateHosts.networkIsolation.releaseHost.success'),

  onFailure: (message) => failure(message, null, false)
};

const dispatchToActions = {
  stopIsolationRequest
};

@classic
class Release extends Component {
  isReleaseFromIsolationCommentEmpty = false;
  releaseFromIsolationComment = '';

  @computed('releaseFromIsolationComment')
  get releaseFromIsolationCommentInfo() {
    return {
      isCharacterLimitReached: this.releaseFromIsolationComment.length === 900,
      isReleaseFromIsolation: !this.releaseFromIsolationComment.length
    };
  }

  @action
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
  }

  @action
  onFocusOutValidateComment(releaseFromIsolationComment) {
    this.set('isReleaseFromIsolationCommentEmpty', !releaseFromIsolationComment.length);
  }

  @action
  onKeyUpValidateComment(releaseFromIsolationComment) {
    if (releaseFromIsolationComment.length > 0) {
      this.set('isReleaseFromIsolationCommentEmpty', false);
    }
  }
}

export default connect(undefined, dispatchToActions)(Release);