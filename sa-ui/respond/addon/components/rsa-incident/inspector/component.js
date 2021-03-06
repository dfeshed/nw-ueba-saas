import { computed } from '@ember/object';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { alias } from '@ember/object/computed';
import {
  updateItem,
  setViewMode,
  resizeIncidentInspector
} from 'respond/actions/creators/incidents-creators';
import {
  storyPointCount,
  storyEventCount,
  getStorylineStatus
} from 'respond/selectors/storyline';
import {
  isSendToArcherAvailable,
  getIncidentId,
  getIncidentInfo,
  getIncidentInfoStatus,
  getViewMode,
  getInspectorWidth } from 'respond/selectors/incidents';
import DragBehavior from 'respond/utils/behaviors/drag';
import { htmlSafe } from '@ember/string';
import Notifications from 'component-lib/mixins/notifications';
import { inject as service } from '@ember/service';

const stateToComputed = (state) => ({
  incidentId: getIncidentId(state),
  info: getIncidentInfo(state),
  infoStatus: getIncidentInfoStatus(state),
  storylineStatus: getStorylineStatus(state),
  viewMode: getViewMode(state),
  width: getInspectorWidth(state),
  storyPointCount: storyPointCount(state),
  storyEventCount: storyEventCount(state),
  isSendToArcherAvailable: isSendToArcherAvailable(state)
});

const dispatchToActions = (dispatch) => ({
  setViewModeAction(viewMode) {
    dispatch(setViewMode(viewMode));
  },
  resizeAction(width) {
    dispatch(resizeIncidentInspector(width));
  },
  updateItem(entityId, fieldName, value) {
    dispatch(updateItem(entityId, fieldName, value, {
      onSuccess: () => (this.send('success', 'respond.entities.actionMessages.updateSuccess')),
      onFailure: () => (this.send('success', 'respond.entities.actionMessages.updateFailure'))
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
  riac: service(),
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
  mouseDownAction: null,

  // Same as `width`, but enforces minimum.
  resolvedWidth: computed('width', function() {
    return resolveWidth(this.width);
  }),

  style: computed('resolvedWidth', function() {
    return htmlSafe(`width: ${this.resolvedWidth}px;`);
  }),

  storyPointCountExpected: alias('info.alertCount'),

  isStorylineCutoff: computed(
    'storylineStatus',
    'infoStatus',
    'storyPointCount',
    'storyPointCountExpected',
    function() {
      return (this.storylineStatus === 'completed') &&
        (this.infoStatus == 'completed') &&
        (this.storyPointCount < this.storyPointCountExpected);
    }
  ),

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
    this.set('dragBehavior', behavior);
    this.mouseDownAction = this.createMouseDownAction(behavior);
    document.querySelectorAll('.js-incident-inspector-resizer').forEach((domElement) => domElement.addEventListener('mousedown', this.mouseDownAction));
  },

  // Unwire the drag behavior that was wired up in didInsertElement.
  willDestroyElement() {
    document.querySelectorAll('.js-incident-inspector-resizer').forEach((domElement) => domElement.removeEventListener('mousedown', this.mouseDownAction));
    const drag = this.get('dragBehavior');
    drag.teardown();
    this.set('dragBehavior', null);
  },
  createMouseDownAction(behavior) {

    const eventAction = (e) => {
      behavior.mouseDidDown(e);
      e.preventDefault();
      return false;
    };
    return eventAction;
  }
});

/**
 * @class Incident Inspector
 * A Container for displaying information about an Incident in various view modes.
 *
 * @public
 */
export default connect(stateToComputed, dispatchToActions)(IncidentInspector);
