import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';
import _ from 'lodash';
import { headers, collapsedHeaders } from 'recon/reducers/emails/selectors';
import { connect } from 'ember-redux';
import { warn } from '@ember/debug';
import { isEmpty } from '@ember/utils';

import {
  filesWithSelection
} from 'recon/reducers/files/selectors';
import { fileSelectionChanged } from 'recon/actions/interaction-creators';

const stateToComputed = ({ recon, recon: { files } }) => ({
  files: filesWithSelection(recon),
  selectedFileIds: files.selectedFileIds
});

const dispatchToActions = {
  fileSelectionChanged
};

const ARROW_RIGHT = 'right';
const ARROW_DOWN = 'down';

const EmailReconComponent = Component.extend({
  layout,
  isEmailHeadersExpanded: false,
  isAttachmentsExpanded: false,
  allAttachmentsSelected: false,

  @computed('email.attachments', 'selectedFileIds')
  selectedAttachments(attachments, selectedFileIds) {
    if (isEmpty(attachments || selectedFileIds)) {
      return [];
    }

    return attachments.filter((attachment) => {
      const fileId = this._getFileId(attachment);
      return selectedFileIds.indexOf(fileId) > -1;
    }).map((a) => a.attachmentId);
  },

  @computed('email', 'isEmailExpanded')
  headerFields(email, isEmailExpanded) {
    const newHeaders = isEmailExpanded ? headers : collapsedHeaders;
    this.set('isEmailHeadersExpanded', false);
    return _.pickBy(email, (emailValue, emailField) => !(_.isEmpty(emailValue)) & newHeaders.includes(emailField));
  },

  @computed('email.headers')
  additionalHeaderFields(headers) {
    const createdHeaders = {};
    headers.forEach(function(header) {
      if (!_.isEmpty(header.value)) {
        createdHeaders[header.name] = header.value;
      }
    });
    return createdHeaders;
  },

  @computed('isEmailHeadersExpanded')
  headersCollapseArrowDirection(isEmailHeadersExpanded) {
    return isEmailHeadersExpanded ? ARROW_DOWN : ARROW_RIGHT;
  },

  @computed('isAttachmentsExpanded')
  attachmentsCollapseArrowDirection(isAttachmentsExpanded) {
    return isAttachmentsExpanded ? ARROW_DOWN : ARROW_RIGHT;
  },

  _getFileId({ filename }) {
    // Match the attachment to the corresponding file using filename (for now, since there is no other way)
    // The filenames are of the form - <sessionId>-<number>-<number>_attach.<number>.<attachment_name>
    // Revisit this when there is a better way to match up attachments to the files
    const file = this.get('files').find((f) => f.fileName.endsWith(`.${filename}`));
    if (!file) {
      warn(`No file found with name ${filename}`, false, { id: filename });
      return '';
    }
    return file.id;
  },

  actions: {
    toggleEmailHeadersExpansion() {
      this.toggleProperty('isEmailHeadersExpanded');
    },

    toggleAttachmentsExpansion() {
      this.toggleProperty('isAttachmentsExpanded');
    },

    toggleSelection(attachment, event) {
      this.send('fileSelectionChanged', [this._getFileId(attachment)], event.target.checked);
      if (!event.target.checked) {
        this.set('allAttachmentsSelected', false);
      }
    },

    toggleSelectAll() {
      const selected = this.toggleProperty('allAttachmentsSelected');
      const selectedFiles = this.get('email.attachments').map((attachment) => this._getFileId(attachment));
      this.send('fileSelectionChanged', selectedFiles, selected);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(EmailReconComponent);
