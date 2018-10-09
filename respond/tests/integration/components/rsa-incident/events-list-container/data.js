import { storyLineEvents, eventSelectionId, alertSelectionId } from '../../../../integration/components/events-list/data';

export const storylineEventsWithStatus = (status) => {
  const state = Object.assign({}, storyLineEvents);
  return {
    ...state,
    respond: {
      ...state.respond,
      storyline: {
        ...state.respond.storyline,
        storylineEventsStatus: status
      }
    }
  };
};

export const storylineEventsWithSelection = (selectionType) => {
  const state = Object.assign({}, storyLineEvents);
  const eventSelection = {
    id: 'INC-108',
    type: 'event',
    ids: [
      eventSelectionId
    ]
  };
  const alertSelection = {
    id: 'INC-108',
    type: 'storyPoint',
    ids: [
      alertSelectionId
    ]
  };
  const selection = selectionType === 'alert' ? Object.assign({}, alertSelection) : Object.assign({}, eventSelection);
  return {
    ...state,
    respond: {
      ...state.respond,
      incident: {
        ...state.respond.incident,
        selection
      }
    }
  };
};

export { storyLineEvents };
