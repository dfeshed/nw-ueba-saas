import { get } from '@ember/object';


export const doesStateHaveViewData =
  (state, type) => !!get(state, type.dataKey);

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
  code: 3,
  id: 'text',
  name: 'TEXT',
  component: 'recon-event-detail/text-content',
  dataKey: 'text.textContent'
}, {
  code: 1,
  id: 'packet',
  name: 'PACKET',
  component: 'recon-event-detail/packets',
  dataKey: 'packets.packets'
}, {
  code: 2,
  id: 'file',
  name: 'FILE',
  component: 'recon-event-detail/files',
  dataKey: 'files.files'
}, {
  code: 4,
  id: 'mail',
  name: 'MAIL',
  isClassicReconView: true
}, {
  code: 5,
  id: 'web',
  name: 'WEB',
  isClassicReconView: true
}];

const RECON_VIEW_TYPES_BY_NAME = {};
RECON_VIEW_TYPES.forEach((t) => RECON_VIEW_TYPES_BY_NAME[t.name] = t);

export {
  RECON_VIEW_TYPES,
  RECON_VIEW_TYPES_BY_NAME
};
