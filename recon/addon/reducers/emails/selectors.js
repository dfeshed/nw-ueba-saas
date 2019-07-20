import reselect from 'reselect';

const { createSelector } = reselect;
const emails = (recon) => recon.emails.emails || [];
const renderIds = (recon) => recon.emails.renderIds;
export const headers = ['to', 'from', 'subject', 'cc', 'bcc', 'replyTo', 'sent', 'received'];

export const hasRenderIds = createSelector(
  [renderIds],
  (renderIds) => !!renderIds && renderIds.length > 0
);

export const hasNoEmailContent = createSelector(
  [emails],
  (emails) => !!emails && emails.length <= 0
);

export const renderedEmails = createSelector(
  [emails, renderIds, hasRenderIds, hasNoEmailContent],
  (emails, renderIds, hasRenderIds, hasNoEmailContent) => {
    if (hasNoEmailContent || !hasRenderIds) {
      return [];
    }
    return emails.slice(0, 2);
  }
);

export const hasEmailAttachments = createSelector(
  [emails],
  (emails) => {
    return !!emails.find((e) => e.attachments && e.attachments.length);
  }
);
