/* eslint-disable no-multi-spaces */
/**
 * This is a running list of meta we want to highlight and the type of event
 * each meta relates to.
 * @type {object[]}
 * @private
 */
const metaList = [
  { name: 'access.point', eventTypes: [           'log'            ] },
  { name: 'action',       eventTypes: ['network', 'log'            ] },
  { name: 'alias.host',   eventTypes: ['network', 'log', 'endpoint'] },
  { name: 'attachment',   eventTypes: ['network', 'log'            ] },
  { name: 'category',     eventTypes: [           'log', 'endpoint'] },
  { name: 'client',       eventTypes: ['network', 'log', 'endpoint'] },
  { name: 'content',      eventTypes: ['network'                   ] },
  { name: 'email',        eventTypes: ['network', 'log'            ] },
  { name: 'email.src',    eventTypes: ['network', 'log'            ] },
  { name: 'email.dst',    eventTypes: ['network', 'log'            ] },
  { name: 'event.time',   eventTypes: [           'log'            ] },
  { name: 'directory',    eventTypes: ['network', 'log'            ] },
  { name: 'domain.src',   eventTypes: ['network', 'log'            ] },
  { name: 'domain.dst',   eventTypes: ['network', 'log'            ] },
  { name: 'filename',     eventTypes: ['network', 'log'            ] },
  { name: 'msg.id',       eventTypes: [           'log'            ] },
  { name: 'policy.name',  eventTypes: [           'log'            ] },
  { name: 'query',        eventTypes: ['network', 'log'            ] },
  { name: 'referer',      eventTypes: ['network', 'log'            ] },
  { name: 'server',       eventTypes: ['network', 'log'            ] },
  { name: 'sld',          eventTypes: ['network', 'log'            ] },
  { name: 'tld',          eventTypes: ['network', 'log'            ] },
  { name: 'user.dst',     eventTypes: [           'log'            ] },
  { name: 'user.src',     eventTypes: [           'log'            ] },
  { name: 'username',     eventTypes: ['network', 'log'            ] }
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