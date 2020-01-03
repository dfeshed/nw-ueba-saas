import classic from 'ember-classic-decorator';
import { action, computed } from '@ember/object';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { success, failure } from 'investigate-shared/utils/flash-messages';
import { editExclusionListRequest } from 'investigate-hosts/actions/data-creators/host';
import { isolateMachineValidation } from 'investigate-hosts/util/util';
import { isolationComment, excludedIps } from 'investigate-hosts/reducers/hosts/selectors';
const callBackOptions = {
  onSuccess: () => success('investigateHosts.networkIsolation.editExclusionList.success'),

  onFailure: (message) => failure(message, null, false)
};

const stateToComputed = (state) => ({
  comment: isolationComment(state),
  excludedIps: excludedIps(state)
});

const dispatchToActions = {
  editExclusionListRequest
};

@classic
class Edit extends Component {
  isExclusionListError = false;
  isIsolationCommentEmpty = false;

  init() {
    super.init(...arguments);
    const { comment, excludedIps } = this.getProperties(['comment', 'excludedIps']);
    this.set('isolationComment', comment);
    this.set('exclusionList', excludedIps);
  }

  @computed('isolationComment')
  get isolationCommentInfo() {
    return {
      isCharacterLimitReached: this.isolationComment.length === 900,
      isIsolateHostDisabled: !this.isolationComment.length
    };
  }

  @action
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
  }

  @action
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
  }

  @action
  onFocusOutValidateComment(isolationComment) {
    this.set('isIsolationCommentEmpty', !isolationComment.length);
  }

  @action
  onKeyUpValidateComment(isolationComment) {
    if (isolationComment.length > 0) {
      this.set('isIsolationCommentEmpty', false);
    }
  }
}

export default connect(stateToComputed, dispatchToActions)(Edit);