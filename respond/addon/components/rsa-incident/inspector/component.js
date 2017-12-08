import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed, { alias } from 'ember-computed-decorators';
import { updateItem, setViewMode, resizeIncidentInspector } from 'respond/actions/creators/incidents-creators';
import { storyPointCount, storyEventCount, getStorylineStatus } from 'respond/selectors/storyline';
import {
  getIncidentId,
  getIncidentInfo,
  getIncidentInfoStatus,
  getViewMode,
  getInspectorWidth } from 'respond/selectors/incidents';
import DragBehavior from 'respond/utils/behaviors/drag';
import { htmlSafe } from 'ember-string';
import FLASH_MESSAGE_TYPES from 'respond/utils/flash-message-types';
import Notifications from 'respond/mixins/notifications';
import $ from 'jquery';
import service from 'ember-service/inject';

const stateToComputed = (state) => {
  return {
    incidentId: getIncidentId(state),
    info: getIncidentInfo(state),
    infoStatus: getIncidentInfoStatus(state),
    storylineStatus: getStorylineStatus(state),
    viewMode: getViewMode(state),
    width: getInspectorWidth(state),
    storyPointCount: storyPointCount(state),
    storyEventCount: storyEventCount(state)
  };
};

const dispatchToActions = (dispatch) => ({
  setViewModeAction(viewMode) {
    dispatch(setViewMode(viewMode));
  },
  resizeAction(width) {
    dispatch(resizeIncidentInspector(width));
  },
  updateItem(entityId, fieldName, value) {
    dispatch(updateItem(entityId, fieldName, value, {
      onSuccess: () => (this.send('showFlashMessage', FLASH_MESSAGE_TYPES.SUCCESS, 'respond.entities.actionMessages.updateSuccess')),
      onFailure: () => (this.send('showFlashMessage', FLASH_MESSAGE_TYPES.ERROR, 'respond.entities.actionMessages.updateFailure'))
    }));
  }
});

const MINIMUM_WIDTH = 350;

// Validates a given width and enforces a minimum value.
const resolveWidth = (width) => Math.max((isNaN(width) ? 0 : width), MINIMUM_WIDTH);

const IncidentInspector = Component.extend(Notifications, {
  attributeBindings: ['style'],
  tagName: 'article',
  classNames: ['rsa-incident-inspector'],
  classNameBindings: ['isResizing'],
  accessControl: service(),
  incidentId: null,
  info: null,
  infoStatus: null,
  storylineStatus: null,
  viewMode: null,
  width: null,
  isResizing: false,
  setViewModeAction: null,
  storyPointCount: null,
  storyEventCount: null,

  // Same as `width`, but enforces minimum.
  @computed('width')
  resolvedWidth(width) {
    return resolveWidth(width);
  },

  @computed('resolvedWidth')
  style(width) {
    return htmlSafe(`width: ${width}px;`);
  },

  @alias('info.alertCount')
  storyPointCountExpected: null,

  @computed('storylineStatus', 'infoStatus', 'storyPointCount', 'storyPointCountExpected')
  isStorylineCutoff(storylineStatus, infoStatus, storyPointCount, storyPointCountExpected) {
    return (storylineStatus === 'completed') &&
      (infoStatus == 'completed') &&
      (storyPointCount < storyPointCountExpected);
  },

  // Wire up a drag behavior on resizer DOM node to invoke the "resizeAction".
  didInsertElement() {
    const dragstart = () => {
      this.setProperties({
        widthWas: this.get('width') || 0,
        isResizing: true
      });
    };
    const dragmove = (e, ctxt) => {
      const delta = ctxt.get('delta') || [];
      const width = this.get('widthWas') + delta[0];
      this.send('resizeAction', resolveWidth(width));
    };
    const dragend = () => {
      this.set('isResizing', false);
    };
    const behavior = DragBehavior.create({
      minMouseMoves: 0,
      callbacks: {
        dragstart,
        dragmove,
        dragend
      }
    });
    $('.js-incident-inspector-resizer').on('mousedown', function(e) {
      behavior.mouseDidDown(e);
      e.preventDefault();
      return false;
    });
    this.set('dragBehavior', behavior);
  },

  // Unwire the drag behavior that was wired up in didInsertElement.
  willDestroyElement() {
    $('.js-incident-inspector-resizer').off('mousedown');
    const drag = this.get('dragBehavior');
    drag.teardown();
    this.set('dragBehavior', null);
  }
});

/**
 * @class Incident Inspector
 * A Container for displaying information about an Incident in various view modes.
 *
 * @public
 */
export default connect(stateToComputed, dispatchToActions)(IncidentInspector);
