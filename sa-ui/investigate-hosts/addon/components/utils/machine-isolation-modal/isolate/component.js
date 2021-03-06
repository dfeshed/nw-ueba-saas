import classic from 'ember-classic-decorator';
import { action, computed } from '@ember/object';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { success, failure } from 'investigate-shared/utils/flash-messages';
import { isolateHostRequest } from 'investigate-hosts/actions/data-creators/host';
import { isolateMachineValidation } from 'investigate-hosts/util/util';


const dispatchToActions = {
  isolateHostRequest
};

const callBackOptions = {
  onSuccess: () => success('investigateHosts.networkIsolation.isolate.success'),

  onFailure: (message) => failure(message, null, false)
};

@classic
class Isolate extends Component {
  isExclusionListEnabled = false;
  isExclusionListError = false;
  isolationComment = '';
  isIsolationCommentEmpty = false;

  @computed('isolationComment')
  get isolationCommentInfo() {
    return {
      isCharacterLimitReached: this.isolationComment.length === 900,
      isIsolateHostDisabled: !this.isolationComment.length
    };
  }

  init() {
    super.init(...arguments);
    this.exclusionList = this.exclusionList || [];
  }

  @action
  confirmIsolation() {
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
      this.send('isolateHostRequest', data, serverId, callBackOptions);
      this.closeConfirmModal();
    }
  }

  @action
  toggleExclusionListEnabled() {
    this.toggleProperty('isExclusionListEnabled');
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

export default connect(undefined, dispatchToActions)(Isolate);