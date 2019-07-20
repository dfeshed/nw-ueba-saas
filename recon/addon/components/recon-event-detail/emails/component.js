import Component from '@ember/component';
import { connect } from 'ember-redux';
import layout from './template';
import {
  hasRenderIds,
  renderedEmails,
  hasNoEmailContent,
  hasEmailAttachments
} from 'recon/reducers/emails/selectors';

const stateToComputed = ({ recon, recon: { data, emails } }) => ({
  emails: emails.emails,
  renderIds: data,
  hasRenderIds: hasRenderIds(recon),
  renderedEmails: renderedEmails(recon),
  hasNoEmailContent: hasNoEmailContent(recon),
  hasEmailAttachments: hasEmailAttachments(recon)
});

const dispatchToActions = {

};

const EmailReconComponent = Component.extend({
  layout,
  classNames: ['recon-email-view'],
  classNameBindings: ['hasEmailAttachments:warning']
});

export default connect(stateToComputed, dispatchToActions)(EmailReconComponent);
