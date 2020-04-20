/* eslint-disable no-multi-spaces */
/**
 * This is a running list of meta we want to highlight and the type of event
 * each meta relates to.
 * @type {object[]}
 * @private
 */
const metaList = [
  { name: 'access.point', eventTypes: [           'log'            ] },
  { name: 'action',       eventTypes: ['network'                   ] },
  { name: 'alias.host',   eventTypes: ['network', 'log', 'endpoint'] },
  { name: 'attachment',   eventTypes: ['network'                   ] },
  { name: 'category',     eventTypes: [           'log', 'endpoint'] },
  { name: 'client',       eventTypes: ['network', 'log', 'endpoint'] },
  { name: 'content',      eventTypes: ['network'                   ] },
  { name: 'email',        eventTypes: ['network', 'log'            ] },
  { name: 'email.src',    eventTypes: ['network', 'log'            ] },
  { name: 'email.dst',    eventTypes: ['network', 'log'            ] },
  { name: 'directory',    eventTypes: ['network', 'log'            ] },
  { name: 'filename',     eventTypes: ['network', 'log'            ] },
  { name: 'policy.name',  eventTypes: [           'log'            ] },
  { name: 'query',        eventTypes: ['network'                   ] },
  { name: 'referer',      eventTypes: ['network'                   ] },
  { name: 'server',       eventTypes: ['network'                   ] },
  { name: 'sld',          eventTypes: ['network'                   ] },
  { name: 'tld',          eventTypes: ['network'                   ] },
  { name: 'user.dst',     eventTypes: [           'log'            ] },
  { name: 'user.src',     eventTypes: [           'log'            ] },
  { name: 'username',     eventTypes: [           'log'            ] }
];
/* eslint-enable no-multi-spaces */

const getMetaKeysByEventType = (eventType) => {
  return metaList.reduce((acc, meta) => {
    if (meta.eventTypes.includes(eventType)) {
      acc.push(meta.name);
    }
    return acc;
  }, []);
};

export default getMetaKeysByEventType;