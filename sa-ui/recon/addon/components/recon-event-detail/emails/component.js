import Component from '@ember/component';
import { connect } from 'ember-redux';
import layout from './template';
import {
  hasRenderIds,
  renderedEmails,
  hasNoEmailContent
} from 'recon/reducers/emails/selectors';

import {
  hasSelectedFiles
} from 'recon/reducers/files/selectors';

const stateToComputed = ({ recon, recon: { data, emails } }) => ({
  emails: emails.emails,
  renderIds: data,
  hasRenderIds: hasRenderIds(recon),
  renderedEmails: renderedEmails(recon),
  hasNoEmailContent: hasNoEmailContent(recon),
  hasSelectedFiles: hasSelectedFiles(recon)
});

const dispatchToActions = {

};

const EmailReconComponent = Component.extend({
  layout
});

export default connect(stateToComputed, dispatchToActions)(EmailReconComponent);
