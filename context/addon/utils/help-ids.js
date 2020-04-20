/**
 * Created by gahoiv on 7/7/18.
 * Added to configure the moduleId and topicId for the help icons in context panel
 * @public
 */
const isRespond = () => {
  return window.location.href.indexOf('/respond') > -1;
};
export const contextHelpIds = () => {
  if (isRespond()) {
    return {
      panelHelpId: {
        moduleId: 'respond',
        topicId: 'respContextPnl'
      },
      AddToListHelpIds: {
        moduleId: 'respond',
        topicId: 'respAddToList'
      }
    };
  } else {
    return {
      panelHelpId: {
        moduleId: 'investigation',
        topicId: 'invContextPnl2'
      },
      AddToListHelpIds: {
        moduleId: 'investigation',
        topicId: 'invAddToList2'
      }
    };
  }
};