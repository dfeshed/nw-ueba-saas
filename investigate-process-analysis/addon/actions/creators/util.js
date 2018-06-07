export const getQueryNode = function(input, selectedVid, isFetchParent, isList, filters) {
  const { et, st, sid, aid: agentId, vid } = input;

  let processId = vid;

  if (vid) {
    processId = selectedVid;
  }

  const queryNode = {
    endTime: et,
    startTime: st,
    queryTimeFormat: 'DB',
    serviceId: sid,
    metaFilter: _getMetaFilter(isList, agentId, processId, isFetchParent, filters)
  };
  return queryNode;
};
/**
 * To construct a process tree need to get all the createProcess event for given host and a process and it should include
 * only endpoint related events. Always query will contain fixed set of meta, agent.id, action, type and file name
 *
 * agent.id -> for a given host
 * action -> to get only createProcess events
 * device.type -> to get only endpoint related events
 * fileName.src -> selected process
 *
 * @param agentId
 * @param processName
 * @returns {{conditions: *[]}}
 * @private
 */
const _getMetaFilter = (isList, agentId, pid, isFetchParent, filters) => {
  const query = [
    {
      meta: 'agent.id',
      operator: '=',
      value: `'${agentId}'`
    },
    {
      meta: 'action',
      operator: '=',
      value: '\'createProcess\''
    },
    {
      meta: 'device.type',
      operator: '=',
      value: '\'nwendpoint\''
    },
    {
      value: isFetchParent ? `(vid.src = \'${pid}\' || vid.dst = \'${pid}\')` : `(vid.src = \'${pid}\')`
    },
    {
      value: '(category=\'Process Event\' || category = \'Registry Event\' || category = \'File Event\' || category = \'Network Event\')'
    }
  ];
  if (isList) {
    let conditions = [query[0], query[3], query[4]];
    if (filters && filters.length) {
      conditions = conditions.concat(filters);
    }

    return { conditions };
  }
  return { conditions: query };
};

/**
 * Event response is in ['key', 'value] formate, So need to convert the event meta into object
 * @param {*} event
 * @returns {*} event
 * @public
 */
export const hasherizeEventMeta = (event) => {
  if (event) {
    const { metas } = event;
    if (!metas) {
      return;
    }
    const len = (metas && metas.length) || 0;
    let i;
    for (i = 0; i < len; i++) {
      const meta = metas[i];
      if (meta[0] === 'filename.dst') {
        event.processName = meta[1];
      }
      if (meta[0] === 'agent.id') {
        event.agentId = meta[1];
      }
      if (meta[0] === 'checksum.dst') {
        if (meta[1].length === 64) {
          event.checksum = meta[1];
        }
      }
      event[meta[0]] = meta[1];
    }
    event.childCount = 0;
    event.metas = null;
  }
};
