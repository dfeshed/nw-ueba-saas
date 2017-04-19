import get from 'ember-metal/get';

const doesStateHaveViewData = function(state) {
  return !!get(state, this.dataKey);
};

/*
 * code: id
 * name: is used by RECON_VIEW_TYPES_BY_NAME below, when
 *   wanting to refer to a type by name
 * label: used in dropdown selection
 * component: Ember path to component that renders this view type
 * dataKey: Redux state key where data for this type resides,
 *   used for determining if data for type already exists in redux
 */
const RECON_VIEW_TYPES = [{
  code: 1,
  name: 'PACKET',
  label: 'Packet View',
  component: 'recon-event-detail/packets',
  dataKey: 'packets.packets',
  doesStateHaveViewData
}, {
  code: 2,
  name: 'FILE',
  label: 'File View',
  component: 'recon-event-detail/files',
  dataKey: 'files.files',
  doesStateHaveViewData
}, {
  code: 3,
  name: 'TEXT',
  label: 'Text View',
  component: 'recon-event-detail/text-content',
  dataKey: 'text.textContent',
  doesStateHaveViewData
}];

const RECON_VIEW_TYPES_BY_NAME = {};
RECON_VIEW_TYPES.forEach((t) => RECON_VIEW_TYPES_BY_NAME[t.name] = t);

export {
  RECON_VIEW_TYPES,
  RECON_VIEW_TYPES_BY_NAME
};